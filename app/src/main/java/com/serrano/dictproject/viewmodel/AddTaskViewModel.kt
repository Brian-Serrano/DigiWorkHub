package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.getUsers
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.AddTaskDialogs
import com.serrano.dictproject.utils.AddTaskState
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.TaskBody
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
    private val dao: Dao,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, dao, application) {

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

    /**
     * Add task to server database
     *
     * @param[navigate] Callback for navigation that will be invoked when the response is successful. Direct navigation should be done on user interface. Separation of concerns.
     */
    fun addTask(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                // disable the add task button
                updateTaskState(_addTaskState.value.copy(buttonEnabled = false))

                // check authorization of user
                MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

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
                        // show toast message
                        MiscUtils.toast(getApplication(), "Task Added Successfully!")

                        // enable the add task button
                        updateTaskState(_addTaskState.value.copy(buttonEnabled = true))

                        // save the created task in room database
                        val task = response.data!!
                        dao.dashboardInsertTasks(listOf(task.toEntity()), task.getUsers().toSet())

                        // navigate
                        navigate()
                    }
                    is Resource.ClientError -> {
                        // enable add task button and show error message
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.clientError?.message ?: ""
                            )
                        )
                    }
                    is Resource.GenericError -> {
                        // enable add task button and show error message
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.genericError ?: ""
                            )
                        )
                    }
                    is Resource.ServerError -> {
                        // enable add task button and show error message
                        updateTaskState(
                            _addTaskState.value.copy(
                                buttonEnabled = true,
                                errorMessage = response.serverError?.error ?: ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // enable add task button and show error message
                updateTaskState(
                    _addTaskState.value.copy(
                        buttonEnabled = true,
                        errorMessage = e.message ?: ""
                    )
                )
            }
        }
    }
}