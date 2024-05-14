package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.AssigneeEdit
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.DescriptionChange
import com.serrano.dictproject.utils.DialogsState
import com.serrano.dictproject.utils.DueChange
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.NameChange
import com.serrano.dictproject.utils.PriorityChange
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.RadioButtonDialogState
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.StatusChange
import com.serrano.dictproject.utils.TypeChange
import com.serrano.dictproject.utils.UserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * ViewModel that extended by DashboardViewModel, AboutTaskViewModel, AddTaskViewModel because of the sameness of some of their properties and functions.
 */
open class BaseViewModel(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    protected val mutableProcessState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = mutableProcessState.asStateFlow()

    private val mutableDialogsState = MutableStateFlow(DialogsState())
    val dialogsState: StateFlow<DialogsState> = mutableDialogsState.asStateFlow()

    /**
     * Change the priority of task
     *
     * @param[taskId] What task to change
     * @param[priority] The new priority
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changePriority(taskId: Int, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changePriority(PriorityChange(taskId, priority)),
                onSuccess = {
                    onSuccess()

                    // update the priority of task that is shown on dashboard and about task in room database
                    dao.updateTaskPriorities(priority, taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the status of task
     *
     * @param[taskId] What task to change
     * @param[status] The new status
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeStatus(taskId: Int, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeTaskStatus(StatusChange(taskId, status)),
                onSuccess = {
                    onSuccess()

                    // update the status of task that is shown on dashboard and about task in room database
                    dao.updateTaskStatuses(status, taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the type of task
     *
     * @param[taskId] What task to change
     * @param[type] The new type
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeType(taskId: Int, type: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeType(TypeChange(taskId, type)),
                onSuccess = {
                    onSuccess()

                    // update the type of task that is shown on dashboard and about task in room database
                    dao.updateTaskTypes(type, taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the name of task
     *
     * @param[taskId] What task to change
     * @param[name] The new name
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeName(taskId: Int, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeName(NameChange(taskId, name)),
                onSuccess = {
                    onSuccess()

                    // update the name of task that is shown on dashboard and about task in room database
                    dao.updateTaskTitles(name, taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the description of task
     *
     * @param[taskId] What task to change
     * @param[description] The new description
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeDescription(taskId: Int, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeDescription(DescriptionChange(taskId, description)),
                onSuccess = {
                    onSuccess()

                    // update the description of task that is shown on dashboard and about task in room database
                    dao.updateTaskDescriptions(description, taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the due date of task
     *
     * @param[taskId] What task to change
     * @param[due] The new due date
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeDue(taskId: Int, due: LocalDateTime, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeDueDate(DueChange(taskId, due)),
                onSuccess = {
                    onSuccess()

                    // update the due date of task that is shown on dashboard and about task in room database
                    dao.updateTaskDues(due.format(DateUtils.DATE_TIME_FORMATTER), taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the assignees of task
     *
     * @param[taskId] What task to change
     * @param[assignee] The new assignees
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
    fun changeAssignee(taskId: Int, assignee: List<UserDTO>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.editAssignees(AssigneeEdit(taskId, assignee.map { it.id })),
                onSuccess = {
                    onSuccess()

                    // add the users that are assignees in room database
                    dao.insertUsers(assignee.map { it.toEntity() }.toSet())

                    // update the assignee ids of task that is shown on dashboard and about task in room database
                    dao.updateTaskAssigneesId(assignee.map { it.id }.joinToString(","), taskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Search for user to add as assignee
     *
     * @param[searchQuery] Text use to filter the users
     * @param[onSuccess] Function that will be invoked when the response was successful
     */
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

    fun updateViewAssigneeDialogState(newState: List<UserDTO>) {
        mutableDialogsState.value = mutableDialogsState.value.copy(viewAssigneeDialogState = newState)
    }
}