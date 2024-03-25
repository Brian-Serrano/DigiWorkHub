package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.Message
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
class AboutMessageViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _message = MutableStateFlow(Message())
    val message: StateFlow<Message> = _message.asStateFlow()

    fun getMessage(messageId: Int) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val message = apiRepository.getMessage(messageId)) {
                    is Resource.Success -> {
                        _message.value = message.data!!
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
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

}