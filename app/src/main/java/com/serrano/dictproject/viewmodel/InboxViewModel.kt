package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.toDTO
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.InboxDialogs
import com.serrano.dictproject.utils.InboxState
import com.serrano.dictproject.utils.MessageIdBody
import com.serrano.dictproject.utils.MessagePartDTO
import com.serrano.dictproject.utils.MessagePartState
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.Tags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _processState2 = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState2: StateFlow<ProcessState> = _processState2.asStateFlow()

    private val _sentMessages = MutableStateFlow<List<MessagePartState>>(emptyList())
    val sentMessages: StateFlow<List<MessagePartState>> = _sentMessages.asStateFlow()

    private val _receivedMessages = MutableStateFlow<List<MessagePartState>>(emptyList())
    val receivedMessage: StateFlow<List<MessagePartState>> = _receivedMessages.asStateFlow()

    private val _inboxState = MutableStateFlow(InboxState())
    val inboxState: StateFlow<InboxState> = _inboxState.asStateFlow()

    private val _dialogState = MutableStateFlow(InboxDialogs.NONE)
    val dialogState: StateFlow<InboxDialogs> = _dialogState.asStateFlow()

    /**
     * Get the sent messages in server or in room database
     */
    fun getSentMessages() {
        viewModelScope.launch {
            try {
                // get the messages in room database
                val localMessages = dao.getMessagePart(Tags.SENT_MESSAGE).first()

                // check if there are messages in room database
                if (localMessages.isEmpty()) {
                    // if there no messages
                    // check authorization
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server for the message
                    when (val response = apiRepository.getSentMessages()) {
                        is Resource.Success -> {
                            val messages = response.data!!

                            // assign the messages to the message state
                            _sentMessages.value = messages.map { mapToMessagePartState(it) }

                            // save messages in room database
                            dao.inboxInsertMessages(
                                messageParts = messages.map { it.toEntity(Tags.SENT_MESSAGE) },
                                users = messages.map { it.other.toEntity() }.toSet()
                            )

                            _processState.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            _processState.value = ProcessState.Error(response.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            _processState.value = ProcessState.Error(response.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            _processState.value = ProcessState.Error(response.serverError?.error ?: "")
                        }
                    }
                } else {
                    // if there are messages
                    val messages = localMessages.map { it.toDTO(dao) }

                    // assign the messages to the message state
                    _sentMessages.value = messages.map { mapToMessagePartState(it) }
                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Get the received messages in server or in room database
     */
    fun getReceivedMessages() {
        viewModelScope.launch {
            try {
                // get the messages in room database
                val localMessages = dao.getMessagePart(Tags.RECEIVED_MESSAGE).first()

                // check if there are messages in room database
                if (localMessages.isEmpty()) {
                    // if there are no messages
                    // check authorization
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server
                    when (val response = apiRepository.getReceivedMessages()) {
                        is Resource.Success -> {
                            val messages = response.data!!

                            // assign the messages to the message state
                            _receivedMessages.value = messages.map { mapToMessagePartState(it) }

                            // save the message in room database
                            dao.inboxInsertMessages(
                                messageParts = messages.map { it.toEntity(Tags.RECEIVED_MESSAGE) },
                                users = messages.map { it.other.toEntity() }.toSet()
                            )

                            _processState2.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            _processState2.value = ProcessState.Error(response.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            _processState2.value = ProcessState.Error(response.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            _processState2.value = ProcessState.Error(response.serverError?.error ?: "")
                        }
                    }
                } else {
                    // if there are messages
                    val messages = localMessages.map { it.toDTO(dao) }

                    // assign the messages to the message state
                    _receivedMessages.value = messages.map { mapToMessagePartState(it) }
                    _processState2.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState2.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh sent messages by requesting server
     */
    fun refreshSentMessages() {
        viewModelScope.launch {
            // show refresh icon
            updateInboxState(_inboxState.value.copy(isSentRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getSentMessages(),
                onSuccess = { messages ->
                    // assign the messages to the message state
                    _sentMessages.value = messages.map { mapToMessagePartState(it) }

                    // delete the messages previously save in room database
                    dao.deleteMessageParts(Tags.SENT_MESSAGE)

                    // save the new messages in room database
                    dao.inboxInsertMessages(
                        messageParts = messages.map { it.toEntity(Tags.SENT_MESSAGE) },
                        users = messages.map { it.other.toEntity() }.toSet()
                    )

                    // show successful message
                    MiscUtils.toast(getApplication(), "Messages loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            updateInboxState(_inboxState.value.copy(isSentRefreshing = false))
        }
    }

    /**
     * Refresh received messages by requesting server
     */
    fun refreshReceivedMessages() {
        viewModelScope.launch {
            // show refresh icon
            updateInboxState(_inboxState.value.copy(isReceivedRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getReceivedMessages(),
                onSuccess = { messages ->
                    // assign the messages to the message state
                    _receivedMessages.value = messages.map { mapToMessagePartState(it) }

                    // delete the message previously save in room database
                    dao.deleteMessageParts(Tags.RECEIVED_MESSAGE)

                    // save the new message in room database
                    dao.inboxInsertMessages(
                        messageParts = messages.map { it.toEntity(Tags.RECEIVED_MESSAGE) },
                        users = messages.map { it.other.toEntity() }.toSet()
                    )

                    // show successful message
                    MiscUtils.toast(getApplication(), "Messages loaded successfully.")

                    _processState2.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            updateInboxState(_inboxState.value.copy(isReceivedRefreshing = false))
        }
    }

    /**
     * Delete the message for only the user
     *
     * @param[messageId] What message to delete
     */
    fun deleteMessageFromUser(messageId: Int) {
        // disable button
        updateMessageButtons(messageId, false)

        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteMessageFromUser(MessageIdBody(messageId)),
                onSuccess = {
                    // delete message in room database that is shown on about message
                    dao.aboutMessageDeleteMessages(messageId)

                    // delete message in room database that is shown on inbox
                    dao.deleteMessagePart(messageId)

                    // update sent messages tab with the deleted message
                    updateSentMessages(
                        _sentMessages.value.filter {
                            it.messageId != messageId
                        }
                    )

                    // update received messages tab with the deleted message
                    updateReceivedMessages(
                        _receivedMessages.value.filter {
                            it.messageId != messageId
                        }
                    )
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }

        // enable button
        updateMessageButtons(messageId, true)
    }

    fun updateInboxDialogs(newState: InboxDialogs) {
        _dialogState.value = newState
    }

    fun updateConfirmDialogState(newState: ConfirmDialogState) {
        _inboxState.value = _inboxState.value.copy(confirmDialogState = newState)
    }

    private fun updateInboxState(newState: InboxState) {
        _inboxState.value = newState
    }

    private fun updateSentMessages(newMessage: List<MessagePartState>) {
        _sentMessages.value = newMessage
    }

    private fun updateReceivedMessages(newMessage: List<MessagePartState>) {
        _receivedMessages.value = newMessage
    }

    /**
     * Disable/Enable one delete button from a group of delete buttons
     */
    private fun updateMessageButtons(messageId: Int, value: Boolean) {
        updateSentMessages(
            _sentMessages.value.map {
                if (it.messageId == messageId) it.copy(deleteButtonEnabled = value) else it
            }
        )
        updateReceivedMessages(
            _receivedMessages.value.map {
                if (it.messageId == messageId) it.copy(deleteButtonEnabled = value) else it
            }
        )
    }

    /**
     * Convert MessagePartDTO to MessagePartState
     */
    private fun mapToMessagePartState(messagePartDTO: MessagePartDTO): MessagePartState {
        return MessagePartState(
            messageId = messagePartDTO.messageId,
            sentDate = messagePartDTO.sentDate,
            other = messagePartDTO.other,
            title = messagePartDTO.title,
            deleteButtonEnabled = true
        )
    }
}