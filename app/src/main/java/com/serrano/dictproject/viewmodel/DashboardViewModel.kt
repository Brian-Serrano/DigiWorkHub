package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.getUsers
import com.serrano.dictproject.room.toDTO
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.DashboardDialogs
import com.serrano.dictproject.utils.DashboardState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.DropDownMultiselect
import com.serrano.dictproject.utils.DropDownState
import com.serrano.dictproject.utils.LabelAndCollapsible
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.TaskPartDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, dao, application) {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _modifiedTasks = MutableStateFlow<DashboardDataState>(emptyList())
    val modifiedTasks: StateFlow<DashboardDataState> = _modifiedTasks.asStateFlow()

    private val _modifiedCreatedTasks = MutableStateFlow<DashboardDataState>(emptyList())
    val modifiedCreatedTasks: StateFlow<DashboardDataState> = _modifiedCreatedTasks.asStateFlow()

    private val _processState2 = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState2: StateFlow<ProcessState> = _processState2.asStateFlow()

    private val _dialogState = MutableStateFlow(DashboardDialogs.NONE)
    val dialogState: StateFlow<DashboardDialogs> = _dialogState.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskPartDTO>>(emptyList())
    val tasks: StateFlow<List<TaskPartDTO>> = _tasks.asStateFlow()

    private val _createdTasks = MutableStateFlow<List<TaskPartDTO>>(emptyList())
    val createdTasks: StateFlow<List<TaskPartDTO>> = _createdTasks.asStateFlow()

    /**
     * Get all assigned tasks
     */
    fun getTasks() {
        viewModelScope.launch {
            try {
                // get all assigned tasks from room database
                val localTasks = dao.getTasks("%${preferencesRepository.getData().first().id}%").first()

                // check if there are data in room database
                if (localTasks.isEmpty()) {
                    // if there are no data
                    // check authorization of user
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server for data
                    when (val tasks = apiRepository.getTasks()) {
                        is Resource.Success -> {
                            // assign tasks to the state
                            _tasks.value = tasks.data!!

                            // store the tasks in the room database
                            storeInStorage(tasks.data)

                            // do the sorting, grouping and filtering of tasks
                            filterTab(0)

                            mutableProcessState.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            mutableProcessState.value = ProcessState.Error(tasks.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            mutableProcessState.value = ProcessState.Error(tasks.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            mutableProcessState.value = ProcessState.Error(tasks.serverError?.error ?: "")
                        }
                    }
                } else {
                    // if there are tasks convert the tasks to DTO and assign to the tasks state
                    val tasks = localTasks.map { it.toDTO(dao) }

                    _tasks.value = tasks

                    // do the sorting, grouping and filtering of tasks
                    filterTab(0)

                    mutableProcessState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Get all created tasks
     */
    fun getCreatedTasks() {
        viewModelScope.launch {
            try {
                // get all created tasks in room database
                val localCreatedTasks = dao.getCreatedTasks(preferencesRepository.getData().first().id).first()

                // check if there are data in room database
                if (localCreatedTasks.isEmpty()) {
                    // if there are no data
                    // check authorization of user
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server for data
                    when (val createdTasks = apiRepository.getCreatedTasks()) {
                        is Resource.Success -> {
                            // assign tasks to the state
                            _createdTasks.value = createdTasks.data!!

                            // store the tasks in the room database
                            storeInStorage(createdTasks.data)

                            // do the sorting, grouping and filtering of tasks
                            filterTab(1)

                            _processState2.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            _processState2.value = ProcessState.Error(createdTasks.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            _processState2.value = ProcessState.Error(createdTasks.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            _processState2.value = ProcessState.Error(createdTasks.serverError?.error ?: "")
                        }
                    }
                } else {
                    // if there are tasks convert the tasks to DTO and assign to the tasks state
                    val tasks = localCreatedTasks.map { it.toDTO(dao) }

                    _createdTasks.value = tasks

                    // do the sorting, grouping and filtering of tasks
                    filterTab(1)

                    _processState2.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState2.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh assigned tasks
     */
    fun refreshTasks() {
        viewModelScope.launch {
            // show refresh icon
            _dashboardState.value = _dashboardState.value.copy(isTaskRefreshing = true)

            MiscUtils.apiAddWrapper(
                response = apiRepository.getTasks(),
                onSuccess = { task ->
                    // add to the state the tasks
                    _tasks.value = task

                    // delete the task previously save in room database
                    dao.deleteTasks("%${preferencesRepository.getData().first().id}%")

                    // save the new task in room database
                    storeInStorage(task)

                    // do the sorting, grouping and filtering of tasks
                    filterTab(0)

                    // show successful message
                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    mutableProcessState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            _dashboardState.value = _dashboardState.value.copy(isTaskRefreshing = false)
        }
    }

    /**
     * Refresh created tasks
     */
    fun refreshCreatedTasks() {
        viewModelScope.launch {
            // show refresh icon
            _dashboardState.value = _dashboardState.value.copy(isCreatedTaskRefreshing = true)

            MiscUtils.apiAddWrapper(
                response = apiRepository.getCreatedTasks(),
                onSuccess = { task ->
                    // add to the state the tasks
                    _createdTasks.value = task

                    // delete the task previously save in room database
                    dao.deleteCreatedTasks(preferencesRepository.getData().first().id)

                    // save the new task in room database
                    storeInStorage(task)

                    // do the sorting, grouping and filtering of data
                    filterTab(1)

                    // show successful message
                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    _processState2.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            _dashboardState.value = _dashboardState.value.copy(isCreatedTaskRefreshing = false)
        }
    }

    /**
     * Sort, Group and Filter Assigned Tasks or Created Tasks
     *
     * @param[bottomBarIdx] Whether assigned task or created task
     */
    fun filterTab(bottomBarIdx: Int) {
        when (bottomBarIdx) {
            0 -> _modifiedTasks.value = sortTasks(
                groupTasks(
                    filterTasks(_tasks.value, _dashboardState.value)
                )
            ).toList()
            1 -> _modifiedCreatedTasks.value = sortTasks(
                groupTasks(
                    filterTasks(_createdTasks.value, _dashboardState.value)
                )
            ).toList()
        }
    }

    /**
     * Sort, Group and Filter Assigned Tasks and Created Tasks
     */
    fun filterAllTabs() {
        filterTab(0)
        filterTab(1)
    }

    fun updateDialogState(newDialogState: DashboardDialogs) {
        _dialogState.value = newDialogState
    }

    /**
     * Update the Filter Options Dropdown Values base on users selected value in filter dropdown in filter dialog.
     *
     * @param[tasks] the values to add in the dropdown base on the selected value in filter dropdown
     * @param[selected] selected filter dropdown value
     */
    fun updateOptionsDropdown(tasks: List<TaskPartDTO>, selected: String) {
        when (selected) {
            "STATUS" -> updateOptionsFilterDropdown(
                DropDownMultiselect(options = tasks.map { it.status }.toSortedSet().toList())
            )
            "PRIORITY" -> updateOptionsFilterDropdown(
                DropDownMultiselect(options = tasks.map { it.priority }.toSortedSet().toList())
            )
            "DUE" -> updateOptionsFilterDropdown(
                DropDownMultiselect(options = tasks.map { DateUtils.dateTimeToDate(it.due) }.toSortedSet().map { DateUtils.dateToDateString(it) })
            )
            "TYPE" -> updateOptionsFilterDropdown(
                DropDownMultiselect(options = tasks.map { it.type }.toSortedSet().toList())
            )
            "CREATOR" -> updateOptionsFilterDropdown(
                DropDownMultiselect(options = tasks.map { it.creator.name }.toSortedSet().toList())
            )
            else -> throw IllegalStateException()
        }
    }

    fun updateTasks(newTasks: List<TaskPartDTO>) {
        _tasks.value = newTasks
    }

    fun updateCreatedTasks(newTasks: List<TaskPartDTO>) {
        _createdTasks.value = newTasks
    }

    fun updateGroupDropdown(newGroupState: DropDownState) {
        _dashboardState.value = _dashboardState.value.copy(groupDropDown = newGroupState)
    }

    fun updateFilterDropdown(newFilterState: DropDownState) {
        _dashboardState.value = _dashboardState.value.copy(filterDropDown = newFilterState)
    }

    fun updateIsFilterDropdown(newIsFilterState: DropDownState) {
        _dashboardState.value = _dashboardState.value.copy(isFilterDropDown = newIsFilterState)
    }

    fun updateOptionsFilterDropdown(newOptionsFilterState: DropDownMultiselect) {
        _dashboardState.value = _dashboardState.value.copy(optionsFilterDropDown = newOptionsFilterState)
    }

    fun updateSortDropdown(newSortState: DropDownState) {
        _dashboardState.value = _dashboardState.value.copy(sortDropDown = newSortState)
    }

    /**
     * Collapse/Un-collapse one grouped assigned tasks with a group of grouped assigned tasks
     *
     * @param[label] The name of the (header/common value to the grouped tasks)
     * @param[value] collapse or un-collapse
     */
    fun updateTaskCollapsible(label: String, value: Boolean) {
        _modifiedTasks.value = _modifiedTasks.value.map {
            if (it.first.label == label) it.copy(it.first.copy(collapsible = value)) else it
        }
    }

    /**
     * Collapse/Un-collapse one grouped created tasks with a group of grouped created tasks
     *
     * @param[label] The name of the (header/common value to the grouped tasks)
     * @param[value] collapse or un-collapse
     */
    fun updateCreatedTaskCollapsible(label: String, value: Boolean) {
        _modifiedCreatedTasks.value = _modifiedCreatedTasks.value.map {
            if (it.first.label == label) it.copy(it.first.copy(collapsible = value)) else it
        }
    }

    /**
     * Group assigned/created tasks
     *
     * @param[tasks] The tasks to group
     *
     * @return common value to the group tasks and status of collapse as key and a grouped task that have the same value as value
     */
    private fun groupTasks(tasks: List<TaskPartDTO>): Map<LabelAndCollapsible, List<TaskPartDTO>> {
        return when (_dashboardState.value.groupDropDown.selected) {
            "NONE" -> tasks.groupBy { LabelAndCollapsible("NONE", true) }.toSortedMap(comparator())
            "STATUS" -> tasks.groupBy { LabelAndCollapsible(it.status, true) }.toSortedMap(comparator())
            "PRIORITY" -> tasks.groupBy { LabelAndCollapsible(it.priority, true) }.toSortedMap(comparator())
            "DUE" -> tasks.groupBy { LabelAndCollapsible(DateUtils.dateTimeToDateString(it.due), true) }.toSortedMap(comparator())
            "TYPE" -> tasks.groupBy { LabelAndCollapsible(it.type, true) }.toSortedMap(comparator())
            "CREATOR" -> tasks.groupBy { LabelAndCollapsible(it.creator.name, true) }.toSortedMap(comparator())
            else -> throw IllegalStateException()
        }
    }

    /**
     * A comparator to use for sorting the common values of the grouped tasks lexicographically in ascending order
     *
     * @return The comparator to use
     */
    private fun comparator(): Comparator<LabelAndCollapsible> {
        return Comparator { o1, o2 -> o1.label.compareTo(o2.label) }
    }

    /**
     * Filter tasks base on the users selected include/not-include and options dropdown values
     *
     * @param[tasks] The tasks to filter
     * @param[ds] The states for the dashboard page, needs the values user entered on filter, include/not-include and filter options dropdowns
     *
     * @return The filtered tasks
     */
    private fun filterTasks(tasks: List<TaskPartDTO>, ds: DashboardState): List<TaskPartDTO> {
        return when (ds.filterDropDown.selected) {
            "NONE" -> tasks
            "STATUS" -> tasks.filter { checkFilter(it.status, ds) }
            "PRIORITY" -> tasks.filter { checkFilter(it.priority, ds) }
            "DUE" -> tasks.filter { checkFilter(DateUtils.dateTimeToDateString(it.due), ds) }
            "TYPE" -> tasks.filter { checkFilter(it.type, ds) }
            "CREATOR" -> tasks.filter { checkFilter(it.creator.name, ds) }
            else -> throw IllegalStateException()
        }
    }

    /**
     * Base on the include/not-include dropdown value. How to do the filter. Does the data/task removed/added.
     *
     * @param[data] Does the data/task removed/added
     * @param[ds] The states for the dashboard page, needs the values user entered on include/not-include and filter options dropdowns
     *
     * @return true = Data/task is kept, false = Data/task is removed
     */
    private fun checkFilter(data: String, ds: DashboardState): Boolean {
        return when (ds.isFilterDropDown.selected) {
            "IS" -> ds.optionsFilterDropDown.selected.any { it == data }
            "IS NOT" -> ds.optionsFilterDropDown.selected.none { it == data }
            else -> throw IllegalStateException()
        }
    }

    /**
     * Sorts assigned/created tasks
     *
     * @param[tasks] The tasks to sort
     *
     * @return The sorted tasks in ascending order and lexicographically
     */
    private fun sortTasks(tasks: Map<LabelAndCollapsible, List<TaskPartDTO>>): Map<LabelAndCollapsible, List<TaskPartDTO>> {
        return when (_dashboardState.value.sortDropDown.selected) {
            "NAME" -> tasks.mapValues { task -> task.value.sortedBy { it.title } }
            "ASSIGNEE" -> tasks.mapValues { task -> task.value.sortedBy { it.assignees.size } }
            "DUE" -> tasks.mapValues { task -> task.value.sortedBy { it.due } }
            "PRIORITY" -> tasks.mapValues { task -> task.value.sortedBy { it.priority } }
            "STATUS" -> tasks.mapValues { task -> task.value.sortedBy { it.status } }
            "TYPE" -> tasks.mapValues { task -> task.value.sortedBy { it.type } }
            else -> throw IllegalStateException()
        }
    }

    /**
     * Store the task in room database
     *
     * @param[task] The task to store
     */
    private suspend fun storeInStorage(task: List<TaskPartDTO>) {
        dao.dashboardInsertTasks(
            taskParts = task.map { it.toEntity() },
            users = task.flatMap { it.getUsers() }.toSet()
        )
    }
}

typealias DashboardDataState = List<Pair<LabelAndCollapsible, List<TaskPartDTO>>>