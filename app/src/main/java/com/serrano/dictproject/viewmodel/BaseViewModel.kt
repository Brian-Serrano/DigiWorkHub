package com.serrano.dictproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.AssigneeEdit
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DescriptionChange
import com.serrano.dictproject.utils.DialogsState
import com.serrano.dictproject.utils.DueChange
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.NameChange
import com.serrano.dictproject.utils.PriorityChange
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.RadioButtonDialogState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.StatusChange
import com.serrano.dictproject.utils.TypeChange
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

open class BaseViewModel(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    protected val mutableProcessState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = mutableProcessState.asStateFlow()

    private val mutableDialogsState = MutableStateFlow(DialogsState())
    val dialogsState: StateFlow<DialogsState> = mutableDialogsState.asStateFlow()

    fun changePriority(taskId: Int, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changePriority(PriorityChange(taskId, priority))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeStatus(taskId: Int, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeTaskStatus(StatusChange(taskId, status))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeType(taskId: Int, type: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeType(TypeChange(taskId, type))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeName(taskId: Int, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeName(NameChange(taskId, name))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeDescription(taskId: Int, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeDescription(DescriptionChange(taskId, description))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeDue(taskId: Int, due: LocalDateTime, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeDueDate(DueChange(taskId, due))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeAssignee(taskId: Int, assignee: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.editAssignees(AssigneeEdit(taskId, assignee))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
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

    fun updateRadioDialogState(newState: RadioButtonDialogState) {
        mutableDialogsState.value = mutableDialogsState.value.copy(radioButtonDialogState = newState)
    }

    fun updateDateDialogState(newState: DateDialogState) {
        mutableDialogsState.value = mutableDialogsState.value.copy(dateDialogState = newState)
    }

    fun updateSearchDialogState(newState: SearchUserDialogState) {
        mutableDialogsState.value = mutableDialogsState.value.copy(searchUserDialogState = newState)
    }

    fun updateSearchState(newSearch: SearchState) {
        mutableDialogsState.value = mutableDialogsState.value.copy(searchState = newSearch)
    }

    fun updateEditNameDialogState(newState: EditNameDialogState) {
        mutableDialogsState.value = mutableDialogsState.value.copy(editNameDialogState = newState)
    }

    fun updateViewAssigneeDialogState(newState: List<User>) {
        mutableDialogsState.value = mutableDialogsState.value.copy(viewAssigneeDialogState = newState)
    }
}