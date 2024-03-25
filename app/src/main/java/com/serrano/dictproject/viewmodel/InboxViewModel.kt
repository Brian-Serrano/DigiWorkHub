package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.MessagePart
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _processState2 = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState2: StateFlow<ProcessState> = _processState2.asStateFlow()

    private val _sentMessages = MutableStateFlow<List<MessagePart>>(emptyList())
    val sentMessages: StateFlow<List<MessagePart>> = _sentMessages.asStateFlow()

    private val _receivedMessages = MutableStateFlow<List<MessagePart>>(emptyList())
    val receivedMessage: StateFlow<List<MessagePart>> = _receivedMessages.asStateFlow()

    fun getSentMessages() {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val messages = apiRepository.getSentMessages()) {
                    is Resource.Success -> {
                        _sentMessages.value = messages.data!!
                        _processState.value = ProcessState.Success
                    }
                    is Resource.ClientError -> {
                        _processState.value = ProcessState.Error(messages.clientError?.message ?: "")
                    }
                    is Resource.GenericError -> {
                        _processState.value = ProcessState.Error(messages.genericError ?: "")
                    }
                    is Resource.ServerError -> {
                        _processState.value = ProcessState.Error(messages.serverError?.error ?: "")
                    }
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun getReceivedMessages() {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val messages = apiRepository.getReceivedMessages()) {
                    is Resource.Success -> {
                        _receivedMessages.value = messages.data!!
                        _processState2.value = ProcessState.Success
                    }
                    is Resource.ClientError -> {
                        _processState2.value = ProcessState.Error(messages.clientError?.message ?: "")
                    }
                    is Resource.GenericError -> {
                        _processState2.value = ProcessState.Error(messages.genericError ?: "")
                    }
                    is Resource.ServerError -> {
                        _processState2.value = ProcessState.Error(messages.serverError?.error ?: "")
                    }
                }
            } catch (e: Exception) {
                _processState2.value = ProcessState.Error(e.message ?: "")
            }
        }
    }
}