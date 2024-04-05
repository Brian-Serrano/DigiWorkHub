package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MessageBody
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SendMessageDialogs
import com.serrano.dictproject.utils.SendMessageState
import com.serrano.dictproject.utils.Tags
import com.serrano.dictproject.utils.UserDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class SendMessageViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    private val _sendMessageState = MutableStateFlow(SendMessageState())
    val sendMessageState: StateFlow<SendMessageState> = _sendMessageState.asStateFlow()

    private val _dialogState = MutableStateFlow(SendMessageDialogs.NONE)
    val dialogState: StateFlow<SendMessageDialogs> = _dialogState.asStateFlow()

    fun updateSendMessageState(newState: SendMessageState) {
        _sendMessageState.value = newState
    }

    fun updateDialogState(newState: SendMessageDialogs) {
        _dialogState.value = newState
    }

    fun sendMessage(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                updateSendMessageState(_sendMessageState.value.copy(buttonEnabled = false))

                if (_sendMessageState.value.receiver != null) {
                    val files = _sendMessageState.value.fileUris.map { FileUtils.getFileFromUri(getApplication(), it) }

                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (
                        val response = apiRepository.messageUser(
                            file = files.map { file ->
                                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                            },
                            messageBody = MessageBody(
                                _sendMessageState.value.receiver!!.id,
                                _sendMessageState.value.title,
                                _sendMessageState.value.description
                            )
                        )
                    ) {
                        is Resource.Success -> {
                            MiscUtils.toast(getApplication(), "Message Sent Successfully!")

                            updateSendMessageState(_sendMessageState.value.copy(buttonEnabled = true))

                            // save the sent message in local storage
                            val message = response.data!!
                            dao.inboxInsertMessages(
                                listOf(message.toEntity(Tags.SENT_MESSAGE)),
                                setOf(message.other.toEntity())
                            )

                            navigate()
                        }
                        is Resource.ClientError -> {
                            updateSendMessageState(
                                _sendMessageState.value.copy(
                                    buttonEnabled = true,
                                    errorMessage = response.clientError?.message ?: ""
                                )
                            )
                        }
                        is Resource.GenericError -> {
                            updateSendMessageState(
                                _sendMessageState.value.copy(
                                    buttonEnabled = true,
                                    errorMessage = response.genericError ?: ""
                                )
                            )
                        }
                        is Resource.ServerError -> {
                            updateSendMessageState(
                                _sendMessageState.value.copy(
                                    buttonEnabled = true,
                                    errorMessage = response.serverError?.error ?: ""
                                )
                            )
                        }
                    }

                    files.forEach { file ->
                        if (file.exists()) file.delete()
                    }
                } else {
                    updateSendMessageState(
                        _sendMessageState.value.copy(
                            buttonEnabled = true,
                            errorMessage = "Please provide recipient."
                        )
                    )
                }
            } catch (e: Exception) {
                updateSendMessageState(_sendMessageState.value.copy(buttonEnabled = true))
            }
        }
    }

    fun searchUser(searchQuery: String, onSuccess: (List<UserDTO>) -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiAddWrapper(
                response = apiRepository.searchUsers(searchQuery),
                onSuccess = onSuccess,
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }
}