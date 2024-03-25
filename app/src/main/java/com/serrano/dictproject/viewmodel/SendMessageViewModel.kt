package com.serrano.dictproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.MessageBody
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SendMessageDialogs
import com.serrano.dictproject.utils.SendMessageState
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendMessageViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
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

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                if (_sendMessageState.value.receiver != null) {
                    when (
                        val response = apiRepository.messageUser(
                            MessageBody(
                                _sendMessageState.value.receiver!!.id,
                                _sendMessageState.value.title,
                                _sendMessageState.value.description
                            )
                        )
                    ) {
                        is Resource.Success -> {
                            Toast.makeText(getApplication(), "Message Sent Successfully!", Toast.LENGTH_LONG).show()
                            updateSendMessageState(_sendMessageState.value.copy(buttonEnabled = true))
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

    fun searchUser(searchQuery: String, onSuccess: (List<User>) -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val response = apiRepository.searchUsers(searchQuery)) {
                    is Resource.Success -> {
                        onSuccess(response.data!!)
                    }
                    is Resource.ClientError -> {
                        Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.GenericError -> {
                        Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                    }
                    is Resource.ServerError -> {
                        Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}