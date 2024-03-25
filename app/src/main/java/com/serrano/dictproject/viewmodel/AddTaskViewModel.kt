package com.serrano.dictproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.AddTaskDialogs
import com.serrano.dictproject.utils.AddTaskState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.TaskBody
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, application) {

    private val _addTaskState = MutableStateFlow(AddTaskState())
    val addTaskState: StateFlow<AddTaskState> = _addTaskState.asStateFlow()

    private val _dialogState = MutableStateFlow(AddTaskDialogs.NONE)
    val dialogState: StateFlow<AddTaskDialogs> = _dialogState.asStateFlow()

    fun updateTaskState(newTaskState: AddTaskState) {
        _addTaskState.value = newTaskState
    }

    fun updateDialogState(newDialogState: AddTaskDialogs) {
        _dialogState.value = newDialogState
    }

    fun addTask(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                updateTaskState(_addTaskState.value.copy(buttonEnabled = false))

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (
                    val response = apiRepository.addTask(
                        TaskBody(
                            _addTaskState.value.name,
                            _addTaskState.value.description,
                            _addTaskState.value.priority,
                            _addTaskState.value.due,
                            _addTaskState.value.type,
                            _addTaskState.value.assignees.map { it.id }
                        )
                    )
                ) {
                    is Resource.Success -> {
                        Toast.makeText(getApplication(), "Task Added Successfully!", Toast.LENGTH_LONG).show()
                        updateTaskState(_addTaskState.value.copy(buttonEnabled = true))
                        navigate()
                    }
                    is Resource.ClientError -> {
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.clientError?.message ?: ""
                            )
                        )
                    }
                    is Resource.GenericError -> {
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.genericError ?: ""
                            )
                        )
                    }
                    is Resource.ServerError -> {
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.serverError?.error ?: ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                updateTaskState(_addTaskState.value.copy(buttonEnabled = true))
            }
        }
    }
}