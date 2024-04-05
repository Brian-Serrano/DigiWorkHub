package com.serrano.dictproject.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.toDTO
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.AboutMessageDialogs
import com.serrano.dictproject.utils.AboutMessageState
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MessageDTO
import com.serrano.dictproject.utils.MessageIdBody
import com.serrano.dictproject.utils.MessageReplyDTO
import com.serrano.dictproject.utils.MessageReplyState
import com.serrano.dictproject.utils.MessageState
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ReplyBody
import com.serrano.dictproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class AboutMessageViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _message = MutableStateFlow(MessageState())
    val message: StateFlow<MessageState> = _message.asStateFlow()

    private val _dialogState = MutableStateFlow(AboutMessageDialogs.NONE)
    val dialogState: StateFlow<AboutMessageDialogs> = _dialogState.asStateFlow()

    private val _aboutMessageState = MutableStateFlow(AboutMessageState())
    val aboutMessageState: StateFlow<AboutMessageState> = _aboutMessageState.asStateFlow()

    fun updateDialogState(newState: AboutMessageDialogs) {
        _dialogState.value = newState
    }

    fun updateAboutMessageState(newState: AboutMessageState) {
        _aboutMessageState.value = newState
    }

    private fun updateMessage(newMessage: MessageState) {
        _message.value = newMessage
    }

    fun updateConfirmDialogState(newState: ConfirmDialogState) {
        _aboutMessageState.value = _aboutMessageState.value.copy(confirmDialogState = newState)
    }

    fun getMessage(messageId: Int) {
        viewModelScope.launch {
            try {
                val localMessage = dao.getMessage(messageId).first()

                if (localMessage == null) {
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val message = apiRepository.getMessage(messageId)) {
                        is Resource.Success -> {
                            _message.value = mapToMessageState(message.data!!)

                            // save fetched data locally
                            storeInStorage(message.data)

                            _processState.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            _processState.value = ProcessState.Error(message.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            _processState.value = ProcessState.Error(message.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            _processState.value = ProcessState.Error(message.serverError?.error ?: "")
                        }
                    }
                } else {
                    val message = localMessage.toDTO(dao)

                    _message.value = mapToMessageState(message)

                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshMessage(messageId: Int) {
        viewModelScope.launch {
            updateAboutMessageState(_aboutMessageState.value.copy(isRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getMessage(messageId),
                onSuccess = { message ->
                    _message.value = mapToMessageState(message)

                    // delete the data previously save
                    dao.aboutMessageDeleteMessages(messageId)

                    // save fetched data locally
                    storeInStorage(message)

                    MiscUtils.toast(getApplication(), "Message loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateAboutMessageState(_aboutMessageState.value.copy(isRefreshing = false))
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAttachment(fileName: String, fileServerName: String) {
        viewModelScope.launch {
            MiscUtils.downloadAttachment(fileName, fileServerName, getApplication(), preferencesRepository, apiRepository)
        }
    }

    fun replyMessage() {
        viewModelScope.launch {
            updateAboutMessageState(_aboutMessageState.value.copy(buttonEnabled = false))

            val files = _aboutMessageState.value.fileUris.map { FileUtils.getFileFromUri(getApplication(), it) }

            MiscUtils.apiAddWrapper(
                response = apiRepository.replyToMessage(
                    file = files.map { file ->
                        MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                    },
                    replyBody = ReplyBody(
                        _message.value.messageId,
                        _aboutMessageState.value.description
                    )
                ),
                onSuccess = {
                    // make all the inputs empty
                    updateAboutMessageState(
                        _aboutMessageState.value.copy(fileUris = emptyList(), description = "")
                    )

                    // add the reply in the local storage
                    dao.insertReplies(listOf(it.toEntity()))
                    dao.updateReplyIdInMessage(it.messageReplyId, it.messageId)

                    // add the reply in the ui
                    updateMessage(
                        _message.value.copy(
                            replies = _message.value.replies + mapToReplyState(it)
                        )
                    )

                    MiscUtils.toast(getApplication(), "Reply Sent.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            files.forEach { file ->
                if (file.exists()) file.delete()
            }

            updateAboutMessageState(_aboutMessageState.value.copy(buttonEnabled = true))
        }
    }

    fun deleteMessage(messageId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            updateMessage(_message.value.copy(deleteButtonEnabled = false))

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessage(messageId),
                onSuccess = {
                    // delete message in storage that is shown on about message
                    dao.aboutMessageDeleteMessages(messageId)

                    // delete message in storage that is shown on inbox
                    dao.deleteMessagePart(messageId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateMessage(_message.value.copy(deleteButtonEnabled = true))
        }
    }

    fun deleteMessageFromUser(messageId: Int, navigate: () -> Unit) {
        updateMessage(_message.value.copy(deleteForUserButtonEnabled = false))

        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessageFromUser(MessageIdBody(messageId)),
                onSuccess = {
                    // delete message in storage that is shown on about message
                    dao.aboutMessageDeleteMessages(messageId)

                    // delete message in storage that is shown on inbox
                    dao.deleteMessagePart(messageId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }

        updateMessage(_message.value.copy(deleteForUserButtonEnabled = true))
    }

    fun deleteReply(messageReplyId: Int) {
        viewModelScope.launch {
            updateReplyButton(messageReplyId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessageReply(messageReplyId),
                onSuccess = {
                    // remove the reply from the ui
                    updateMessage(
                        _message.value.copy(
                            replies = _message.value.replies.filter {
                                it.messageReplyId != messageReplyId
                            }
                        )
                    )

                    // remove the reply in the local storage
                    dao.deleteReply(messageReplyId)
                    dao.updateReplyIdInMessage(messageReplyId, _message.value.messageId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateReplyButton(messageReplyId, true)
        }
    }

    private fun updateReplyButton(messageReplyId: Int, value: Boolean) {
        updateMessage(
            _message.value.copy(
                replies = _message.value.replies.map {
                    if (it.messageReplyId == messageReplyId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    private suspend fun storeInStorage(message: MessageDTO) {
        dao.aboutMessageInsertMessages(
            message = message.toEntity(),
            replies = message.replies.map { it.toEntity() },
            users = setOf(
                message.sender.toEntity(),
                message.receiver.toEntity()
            )
        )
    }

    private fun mapToMessageState(message: MessageDTO): MessageState {
        return MessageState(
            messageId = message.messageId,
            title = message.title,
            description = message.description,
            sentDate = message.sentDate,
            sender = message.sender,
            receiver = message.receiver,
            attachmentPaths = message.attachmentPaths,
            fileNames = message.fileNames,
            replies = message.replies.map { mapToReplyState(it) },
            deleteButtonEnabled = true,
            deleteForUserButtonEnabled = true
        )
    }

    private fun mapToReplyState(reply: MessageReplyDTO): MessageReplyState {
        return MessageReplyState(
            messageReplyId = reply.messageReplyId,
            messageId = reply.messageId,
            sentDate = reply.sentDate,
            description = reply.description,
            fromId = reply.fromId,
            attachmentPaths = reply.attachmentPaths,
            fileNames = reply.fileNames,
            deleteIconEnabled = true
        )
    }
}