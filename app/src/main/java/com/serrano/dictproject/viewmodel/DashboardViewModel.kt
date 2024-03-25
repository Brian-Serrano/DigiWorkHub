package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.DashboardDialogs
import com.serrano.dictproject.utils.DashboardState
import com.serrano.dictproject.utils.DropDownMultiselect
import com.serrano.dictproject.utils.DropDownState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.TaskPart
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, application) {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _modifiedTasks = MutableStateFlow<List<Pair<String, List<TaskPart>>>>(emptyList())
    val modifiedTasks: StateFlow<List<Pair<String, List<TaskPart>>>> = _modifiedTasks.asStateFlow()

    private val _modifiedCreatedTasks = MutableStateFlow<List<Pair<String, List<TaskPart>>>>(emptyList())
    val modifiedCreatedTasks: StateFlow<List<Pair<String, List<TaskPart>>>> = _modifiedCreatedTasks.asStateFlow()

    private val _processState2 = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState2: StateFlow<ProcessState> = _processState2.asStateFlow()

    private val _dialogState = MutableStateFlow(DashboardDialogs.NONE)
    val dialogState: StateFlow<DashboardDialogs> = _dialogState.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskPart>>(emptyList())
    val tasks: StateFlow<List<TaskPart>> = _tasks.asStateFlow()

    private val _createdTasks = MutableStateFlow<List<TaskPart>>(emptyList())
    val createdTasks: StateFlow<List<TaskPart>> = _createdTasks.asStateFlow()

    fun getTasks() {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val tasks = apiRepository.getTasks()) {
                    is Resource.Success -> {
                        _tasks.value = tasks.data!!
                        filterTab(0)
                        updateFilterDropDownValues(_tasks.value, 0)
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
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun getCreatedTasks() {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val createdTasks = apiRepository.getCreatedTasks()) {
                    is Resource.Success -> {
                        _createdTasks.value = createdTasks.data!!
                        filterTab(1)
                        updateFilterDropDownValues(_createdTasks.value, 1)
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
            } catch (e: Exception) {
                _processState2.value = ProcessState.Error(e.message ?: "")
            }
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

    fun updateTasks(newTasks: List<TaskPart>) {
        _tasks.value = newTasks
    }

    fun updateCreatedTasks(newTasks: List<TaskPart>) {
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

    private fun groupTasks(tasks: List<TaskPart>): Map<String, List<TaskPart>> {
        return when (_dashboardState.value.groupDropDown.selected) {
            "NONE" -> tasks.groupBy { "NONE" }.toSortedMap()
            "STATUS" -> tasks.groupBy { it.status }.toSortedMap()
            "PRIORITY" -> tasks.groupBy { it.priority }.toSortedMap()
            "DUE" -> tasks.groupBy { Utils.dateTimeToDateString(it.due) }.toSortedMap()
            "TYPE" -> tasks.groupBy { it.type }.toSortedMap()
            "CREATOR" -> tasks.groupBy { it.creator.name }.toSortedMap()
            else -> throw IllegalStateException()
        }
    }

    private fun filterTasks(tasks: List<TaskPart>, ds: DashboardState): List<TaskPart> {
        return when (ds.filterDropDown.selected) {
            "NONE" -> tasks
            "STATUS" -> tasks.filter { checkFilter(it.status, ds) }
            "PRIORITY" -> tasks.filter { checkFilter(it.priority, ds) }
            "DUE" -> tasks.filter { checkFilter(Utils.dateTimeToDateString(it.due), ds) }
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

    private fun sortTasks(tasks: Map<String, List<TaskPart>>): Map<String, List<TaskPart>> {
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

    private fun updateFilterDropDownValues(task: List<TaskPart>, bottomBarIdx: Int) {
        updateOptionsFilterDropDownValues(
            mapOf(
                "STATUS" to task.map { it.status }.toSet().toList().sorted(),
                "PRIORITY" to task.map { it.priority }.toSet().toList().sorted(),
                "DUE" to task.map { Utils.dateTimeToDate(it.due) }.toSet().map { Utils.dateToDateString(it) }.sorted(),
                "TYPE" to task.map { it.type }.toSet().toList().sorted(),
                "CREATOR" to task.map { it.creator.name }.toSet().toList().sorted()
            ),
            bottomBarIdx
        )
    }
}