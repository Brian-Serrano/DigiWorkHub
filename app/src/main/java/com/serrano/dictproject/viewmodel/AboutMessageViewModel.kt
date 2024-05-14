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

    /**
     * Get the message from the server or from the local storage
     *
     * @param[messageId] To determine what message to get
     */
    fun getMessage(messageId: Int) {
        viewModelScope.launch {
            try {
                // get data from the room database
                val localMessage = dao.getMessage(messageId).first()

                // check if there are data got
                if (localMessage == null) {
                    // if there are no data
                    // check the authorization of user
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server for data
                    when (val message = apiRepository.getMessage(messageId)) {
                        is Resource.Success -> {
                            // assign to the message state the transformed DTO to MessageState
                            _message.value = mapToMessageState(message.data!!)

                            // save fetched data in room database
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
                    // convert the data to Data Transfer Object
                    val message = localMessage.toDTO(dao)

                    // assign to the message state the transformed DTO to MessageState
                    _message.value = mapToMessageState(message)

                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh the data/message got by requesting server for the message again.
     *
     * @param[messageId] To determine what message to get
     */
    fun refreshMessage(messageId: Int) {
        viewModelScope.launch {
            // show the refresh icon of swipe refresh component
            updateAboutMessageState(_aboutMessageState.value.copy(isRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getMessage(messageId),
                onSuccess = { message ->
                    // assign to the message state the transformed DTO to MessageState
                    _message.value = mapToMessageState(message)

                    // delete the message previously save in room database
                    dao.aboutMessageDeleteMessages(messageId)

                    // save the new message in room database
                    storeInStorage(message)

                    // show toast message
                    MiscUtils.toast(getApplication(), "Message loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide the refresh icon of swipe refresh component
            updateAboutMessageState(_aboutMessageState.value.copy(isRefreshing = false))
        }
    }

    /**
     * Get the attachment data from server and save to users device shared storage
     *
     * @param[fileName] The name of file when it was uploaded. It will be used as the name of the stored file with the date appended.
     * @param[fileServerName] The path of file in the server. Used to determine what file to download.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAttachment(fileName: String, fileServerName: String) {
        viewModelScope.launch {
            MiscUtils.downloadAttachment(fileName, fileServerName, getApplication(), preferencesRepository, apiRepository)
        }
    }

    /**
     * Add reply to message
     */
    fun replyMessage() {
        viewModelScope.launch {
            // disable the reply button
            updateAboutMessageState(_aboutMessageState.value.copy(buttonEnabled = false))

            // convert the picked attachments of user in Uri object to file objects and store in a variable
            val files = _aboutMessageState.value.fileUris.map { FileUtils.getFileFromUri(getApplication(), it) }

            MiscUtils.apiAddWrapper(
                response = apiRepository.replyToMessage(
                    file = files.map { file ->
                        MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                    } /* Convert the files objects to form data when sending to server */,
                    replyBody = ReplyBody(
                        _message.value.messageId,
                        _aboutMessageState.value.description
                    )
                ),
                onSuccess = {
                    // this callback received a MessageReplyDTO (reply user added) as response from the server and can be added in room database or user interface
                    // make all the send reply inputs empty
                    updateAboutMessageState(
                        _aboutMessageState.value.copy(fileUris = emptyList(), description = "")
                    )

                    // add the reply in the room database
                    dao.insertReplies(listOf(it.toEntity()))

                    // update the message reply ids, add the new reply id to the list in room database
                    dao.updateReplyIdInMessage(it.messageReplyId, it.messageId)

                    // add the reply in the user interface
                    updateMessage(
                        _message.value.copy(
                            replies = _message.value.replies + mapToReplyState(it)
                        )
                    )

                    // show toast message
                    MiscUtils.toast(getApplication(), "Reply Sent.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // delete the files temporarily created in the internal storage when converting from uri to file
            files.forEach { file ->
                if (file.exists()) file.delete()
            }

            // enable the reply button
            updateAboutMessageState(_aboutMessageState.value.copy(buttonEnabled = true))
        }
    }

    /**
     * Delete the message for the sender and receiver
     *
     * @param[messageId] What message to delete
     * @param[navigate] Callback for navigation that will be invoked when the response is successful. Direct navigation should be done on user interface. Separation of concerns.
     */
    fun deleteMessage(messageId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            // disable the delete message button
            updateMessage(_message.value.copy(deleteButtonEnabled = false))

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessage(messageId),
                onSuccess = {
                    // delete the message in room storage that is shown on about message
                    dao.aboutMessageDeleteMessages(messageId)

                    // delete the message in room storage that is shown on inbox
                    dao.deleteMessagePart(messageId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete message button
            updateMessage(_message.value.copy(deleteButtonEnabled = true))
        }
    }

    /**
     * Delete the message for the user only
     *
     * @param[messageId] What message to delete
     * @param[navigate] Callback for navigation that will be invoked when the response is successful. Direct navigation should be done on user interface. Separation of concerns.
     */
    fun deleteMessageFromUser(messageId: Int, navigate: () -> Unit) {
        // disable the delete message button
        updateMessage(_message.value.copy(deleteForUserButtonEnabled = false))

        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessageFromUser(MessageIdBody(messageId)),
                onSuccess = {
                    // delete the message in room storage that is shown on about message
                    dao.aboutMessageDeleteMessages(messageId)

                    // delete the message in room storage that is shown on inbox
                    dao.deleteMessagePart(messageId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }

        // enable the delete message button
        updateMessage(_message.value.copy(deleteForUserButtonEnabled = true))
    }

    /**
     * Delete a reply of message
     *
     * @param[messageReplyId] What reply to delete
     */
    fun deleteReply(messageReplyId: Int) {
        viewModelScope.launch {
            // disable the reply button
            updateReplyButton(messageReplyId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessageReply(messageReplyId),
                onSuccess = {
                    // remove the reply from the user interface
                    updateMessage(
                        _message.value.copy(
                            replies = _message.value.replies.filter {
                                it.messageReplyId != messageReplyId
                            }
                        )
                    )

                    // remove the reply in the room database
                    dao.deleteReply(messageReplyId)

                    // remove the reply id from the list of message reply ids
                    dao.updateReplyIdInMessage(messageReplyId, _message.value.messageId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the reply button
            updateReplyButton(messageReplyId, true)
        }
    }

    /**
     * Enable/disable one delete reply icon from a list of delete reply icons
     */
    private fun updateReplyButton(messageReplyId: Int, value: Boolean) {
        updateMessage(
            _message.value.copy(
                replies = _message.value.replies.map {
                    if (it.messageReplyId == messageReplyId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Store message in room database
     */
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

    /**
     * Convert MessageDTO to MessageState
     */
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

    /**
     * Convert MessageReplyDTO to MessageReplyState
     */
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