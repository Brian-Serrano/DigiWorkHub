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

    private val _modifiedTasks = MutableStateFlow<List<Pair<String, List<TaskPartDTO>>>>(emptyList())
    val modifiedTasks: StateFlow<List<Pair<String, List<TaskPartDTO>>>> = _modifiedTasks.asStateFlow()

    private val _modifiedCreatedTasks = MutableStateFlow<List<Pair<String, List<TaskPartDTO>>>>(emptyList())
    val modifiedCreatedTasks: StateFlow<List<Pair<String, List<TaskPartDTO>>>> = _modifiedCreatedTasks.asStateFlow()

    private val _processState2 = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState2: StateFlow<ProcessState> = _processState2.asStateFlow()

    private val _dialogState = MutableStateFlow(DashboardDialogs.NONE)
    val dialogState: StateFlow<DashboardDialogs> = _dialogState.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskPartDTO>>(emptyList())
    val tasks: StateFlow<List<TaskPartDTO>> = _tasks.asStateFlow()

    private val _createdTasks = MutableStateFlow<List<TaskPartDTO>>(emptyList())
    val createdTasks: StateFlow<List<TaskPartDTO>> = _createdTasks.asStateFlow()

    fun getTasks() {
        viewModelScope.launch {
            try {
                val localTasks = dao.getTasks("%${preferencesRepository.getData().first().id}%").first()

                // if there are no tasks in the local storage load it from api
                if (localTasks.isEmpty()) {
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val tasks = apiRepository.getTasks()) {
                        is Resource.Success -> {
                            // assign response to the state
                            _tasks.value = tasks.data!!

                            // convert fetched data to entity and save locally
                            storeInStorage(tasks.data)

                            // apply the sorting, grouping and filtering to data and add the collapsible
                            filterTab(0)

                            // add values to the filter dropdown with the data from response
                            updateFilterDropDownValues(tasks.data, 0)

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
                    // if tasks exist convert them to DTO and assign to the state
                    val tasks = localTasks.map { it.toDTO(dao) }

                    _tasks.value = tasks

                    // apply the sorting, grouping and filtering to data and add the collapsible
                    filterTab(0)

                    // add values to the filter dropdown with the data from response
                    updateFilterDropDownValues(tasks, 0)

                    mutableProcessState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun getCreatedTasks() {
        viewModelScope.launch {
            try {
                val localCreatedTasks = dao.getCreatedTasks(preferencesRepository.getData().first().id).first()

                // if there are no tasks in the local storage load it from api
                if (localCreatedTasks.isEmpty()) {
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val createdTasks = apiRepository.getCreatedTasks()) {
                        is Resource.Success -> {
                            // assign response to the state
                            _createdTasks.value = createdTasks.data!!

                            // convert fetched data to entity and save locally
                            storeInStorage(createdTasks.data)

                            // apply the sorting, grouping and filtering to data and add the collapsible
                            filterTab(1)

                            // add values to the filter dropdown with the data from response
                            updateFilterDropDownValues(createdTasks.data, 1)

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
                    // if tasks exist convert them to DTO and assign to the state
                    val tasks = localCreatedTasks.map { it.toDTO(dao) }

                    _createdTasks.value = tasks

                    // apply the sorting, grouping and filtering to data and add the collapsible
                    filterTab(1)

                    // add values to the filter dropdown with the data from response
                    updateFilterDropDownValues(tasks, 1)

                    _processState2.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState2.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isTaskRefreshing = true)

            MiscUtils.apiAddWrapper(
                response = apiRepository.getTasks(),
                onSuccess = { task ->
                    _tasks.value = task

                    // delete the data previously save
                    dao.deleteTasks("%${preferencesRepository.getData().first().id}%")

                    // convert fetched data to entity and save locally
                    storeInStorage(task)

                    // apply the sorting, grouping and filtering to data and add the collapsible
                    filterTab(0)

                    // add values to the filter dropdown with the data from response
                    updateFilterDropDownValues(task, 0)

                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    mutableProcessState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            _dashboardState.value = _dashboardState.value.copy(isTaskRefreshing = false)
        }
    }

    fun refreshCreatedTasks() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(isCreatedTaskRefreshing = true)

            MiscUtils.apiAddWrapper(
                response = apiRepository.getCreatedTasks(),
                onSuccess = { task ->
                    _createdTasks.value = task

                    // delete the data previously save
                    dao.deleteCreatedTasks(preferencesRepository.getData().first().id)

                    // convert fetched data to entity and save locally
                    storeInStorage(task)

                    // apply the sorting, grouping and filtering to data and add the collapsible
                    filterTab(1)

                    // add values to the filter dropdown with the data from response
                    updateFilterDropDownValues(task, 1)

                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    _processState2.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            _dashboardState.value = _dashboardState.value.copy(isCreatedTaskRefreshing = false)
        }
    }

    fun filterTab(bottomBarIdx: Int) {
        when (bottomBarIdx) {
            0 -> {
                _modifiedTasks.value = sortTasks(groupTasks(filterTasks(_tasks.value, _dashboardState.value))).toList()
                updateCollapsible(List(_modifiedTasks.value.size) { false }, bottomBarIdx)
            }
            1 -> {
                _modifiedCreatedTasks.value = sortTasks(groupTasks(filterTasks(_createdTasks.value, _dashboardState.value))).toList()
                updateCollapsible(List(_modifiedCreatedTasks.value.size) { false }, bottomBarIdx)
            }
        }
    }

    fun filterAllTabs() {
        filterTab(0)
        filterTab(1)
    }

    fun updateDialogState(newDialogState: DashboardDialogs) {
        _dialogState.value = newDialogState
    }

    fun updateOptionsDropdown(selected: String, bottomBarIdx: Int): Boolean {
        val options = _dashboardState.value.optionsFilterDropDownValues[bottomBarIdx][selected]
        if (!options.isNullOrEmpty()) {
            updateOptionsFilterDropdown(DropDownMultiselect(options, emptyList(), false))
            return true
        }
        return false
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

    fun updateCollapsible(newCollapsible: List<Boolean>, bottomBarIdx: Int) {
        _dashboardState.value = _dashboardState.value.copy(
            isCollapsed = _dashboardState.value.isCollapsed.mapIndexed { idx, booleans ->
                if (idx == bottomBarIdx) newCollapsible else booleans
            }
        )
    }

    private fun updateOptionsFilterDropDownValues(newValues: Map<String, List<String>>, bottomBarIdx: Int) {
        _dashboardState.value = _dashboardState.value.copy(
            optionsFilterDropDownValues = _dashboardState.value.optionsFilterDropDownValues.mapIndexed { idx, map ->
                if (idx == bottomBarIdx) newValues else map
            }
        )
    }

    private fun groupTasks(tasks: List<TaskPartDTO>): Map<String, List<TaskPartDTO>> {
        return when (_dashboardState.value.groupDropDown.selected) {
            "NONE" -> tasks.groupBy { "NONE" }.toSortedMap()
            "STATUS" -> tasks.groupBy { it.status }.toSortedMap()
            "PRIORITY" -> tasks.groupBy { it.priority }.toSortedMap()
            "DUE" -> tasks.groupBy { DateUtils.dateTimeToDateString(it.due) }.toSortedMap()
            "TYPE" -> tasks.groupBy { it.type }.toSortedMap()
            "CREATOR" -> tasks.groupBy { it.creator.name }.toSortedMap()
            else -> throw IllegalStateException()
        }
    }

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

    private fun checkFilter(data: String, ds: DashboardState): Boolean {
        return when (ds.isFilterDropDown.selected) {
            "IS" -> ds.optionsFilterDropDown.selected.any { it == data }
            "IS NOT" -> ds.optionsFilterDropDown.selected.none { it == data }
            else -> throw IllegalStateException()
        }
    }

    private fun sortTasks(tasks: Map<String, List<TaskPartDTO>>): Map<String, List<TaskPartDTO>> {
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

    private fun updateFilterDropDownValues(task: List<TaskPartDTO>, bottomBarIdx: Int) {
        updateOptionsFilterDropDownValues(
            mapOf(
                "STATUS" to task.map { it.status }.toSet().toList().sorted(),
                "PRIORITY" to task.map { it.priority }.toSet().toList().sorted(),
                "DUE" to task.map { DateUtils.dateTimeToDate(it.due) }.toSet().map { DateUtils.dateToDateString(it) }.sorted(),
                "TYPE" to task.map { it.type }.toSet().toList().sorted(),
                "CREATOR" to task.map { it.creator.name }.toSet().toList().sorted()
            ),
            bottomBarIdx
        )
    }

    private suspend fun storeInStorage(task: List<TaskPartDTO>) {
        dao.dashboardInsertTasks(
            taskParts = task.map { it.toEntity() },
            users = task.flatMap { it.getUsers() }.toSet()
        )
    }
}