package com.serrano.dictproject.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.dialog.DateTimePickerDialog
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.FilterDialog
import com.serrano.dictproject.customui.dialog.RadioButtonDialog
import com.serrano.dictproject.customui.dialog.SearchUserDialog
import com.serrano.dictproject.customui.dialog.ViewAssigneeDialog
import com.serrano.dictproject.customui.dropdown.CustomDropDown
import com.serrano.dictproject.customui.dropdown.CustomDropDownNoMenu
import com.serrano.dictproject.customui.menu.Calendar
import com.serrano.dictproject.customui.menu.DashboardGrid
import com.serrano.dictproject.customui.menu.DashboardList
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.DashboardDialogs
import com.serrano.dictproject.utils.DashboardState
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DialogsState
import com.serrano.dictproject.utils.DropDownMultiselect
import com.serrano.dictproject.utils.DropDownState
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.RadioButtonDialogState
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.SharedViewModelState
import com.serrano.dictproject.utils.TaskPartDTO
import com.serrano.dictproject.utils.UserDTO
import com.serrano.dictproject.utils.header
import com.serrano.dictproject.viewmodel.DashboardDataState
import java.time.LocalDateTime

/**
 * This page is where you can see all the assigned tasks and created tasks. You can sort, group or filter them or change how you see them.
 *
 * @param[windowInfo] An object that will be used to determine the size of screen, adapt the contents base on size and become responsive.
 * @param[rawTasks] This assigned tasks come from the server or room database and no organization done on them and not shown in the page.
 * @param[rawCreatedTasks] This created tasks come from the server or room database and no organization done on them and not shown in the page.
 * @param[dashboardDialogs] What dialog to show the default is NONE (do not show)
 * @param[navController] Used for navigating to different page
 * @param[paddingValues] This is used to move contents down (where it should and can see it), there is top bar on page
 * @param[process] The process for the assigned tasks tab (see [ProcessState.Success], [ProcessState.Error], [ProcessState.Loading] for more information)
 * @param[process2] The process for the created tasks tab (see [ProcessState.Success], [ProcessState.Error], [ProcessState.Loading] for more information)
 * @param[dashboardState] The states/values for the page. Used by the dropdowns and swipe refresh components.
 * @param[tasks] This assigned tasks are what shown in page. They are filtered, sorted and grouped and use the [rawTasks].
 * @param[createdTasks] This created tasks are what shown in page. They are filtered, sorted and grouped and use the [rawCreatedTasks].
 * @param[dialogsState] States/values for the different dialogs (see [EditNameDialogState], [SearchUserDialogState], [DateDialogState], [RadioButtonDialogState], [SearchState] for more information)
 * @param[sharedState] [SharedViewModelState.dashboardViewIdx] and [SharedViewModelState.dashboardBottomBarIdx] are used in the page.
 * @param[updateDialogState] Update the value of state responsible for showing dialogs
 * @param[updateSharedState] Update the values of [sharedState]
 * @param[updateRadioDialogState] Update the values of [RadioButtonDialogState] from [dialogsState]
 * @param[updateGroupDropdown] Update the values/states of group dropdown
 * @param[filterTab] Do sorting, filtering, grouping to one tab of tasks. It needs the tab, what tasks have to be organized.
 * @param[filterAllTabs] Do sorting, filtering, grouping to assigned and created tasks.
 * @param[updateFilterDropdown] Update the values/states of filter dropdown
 * @param[updateOptionsDropdown] Update the values/states of only the options in options dropdown in the filter dialog with the tasks value base on the selected value in filter dropdown
 * @param[updateSortDropdown] Update the values/states of sort dropdown
 * @param[updateTaskCollapsible] Collapse/Un-collapse one grouped assigned tasks. It needs the header name of grouped tasks and whether it should collapse or un-collapse.
 * @param[updateCreatedTaskCollapsible] Collapse/Un-collapse one grouped created tasks. It needs the header name of grouped tasks and whether it should collapse or un-collapse.
 * @param[updateEditNameDialogState] Update the values of [EditNameDialogState] from [dialogsState]
 * @param[updateSearchDialogState] Update the values of [SearchUserDialogState] from [dialogsState]
 * @param[updateDateDialogState] Update the values of [DateDialogState] from [dialogsState]
 * @param[updateIsFilterDropdown] Update the values/states of include/not-include dropdown in the filter dialog
 * @param[updateOptionsFilterDropdown] Update the values/states of options dropdown in the filter dialog
 * @param[changeName] Callback function responsible for changing name of task. It needs the id of task to change, the new name and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[changeAssignee] Callback function responsible for changing assignees of task. It needs the id of task to change, the new assignees and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[updateSearchState] Update the values of [SearchState] from [dialogsState]
 * @param[updateViewAssigneeDialogState] Update the values/users in [ViewAssigneeDialog]
 * @param[searchUser] Callback function responsible for searching users/assignees. It needs the search query (text in search bar) and callback function that will be invoked with the result/response users as its argument, should add the data to the state in the callback.
 * @param[changeDue] Callback function responsible for changing due date of task. It needs the id of task to change, the new due date and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[changePriority] Callback function responsible for changing priority of task. It needs the id of task to change, the new priority and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[changeStatus] Callback function responsible for changing status of task. It needs the id of task to change, the new status and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[changeType] Callback function responsible for changing type of task. It needs the id of task to change, the new type and callback function that will invoked when the change was successful e.g. update user interface or data in room database.
 * @param[updateTasks] After successful changes to data in server, update of [rawTasks] are triggered and use [rawTasks] for sorted, filtered and grouped [tasks].
 * @param[updateCreatedTasks] After successful changes to data in server, update of [rawCreatedTasks] are triggered and use [rawCreatedTasks] for sorted, filtered and grouped [createdTasks].
 * @param[refreshTasks] Refresh assigned tasks
 * @param[refreshCreatedTasks] Refresh created tasks
 */
@Composable
fun Dashboard(
    windowInfo: WindowInfo,
    rawTasks: List<TaskPartDTO>,
    rawCreatedTasks: List<TaskPartDTO>,
    dashboardDialogs: DashboardDialogs,
    navController: NavController,
    paddingValues: PaddingValues,
    process: ProcessState,
    process2: ProcessState,
    dashboardState: DashboardState,
    tasks: DashboardDataState,
    createdTasks: DashboardDataState,
    dialogsState: DialogsState,
    sharedState: SharedViewModelState,
    updateDialogState: (DashboardDialogs) -> Unit,
    updateSharedState: (SharedViewModelState) -> Unit,
    updateRadioDialogState: (RadioButtonDialogState) -> Unit,
    updateGroupDropdown: (DropDownState) -> Unit,
    filterTab: (Int) -> Unit,
    filterAllTabs: () -> Unit,
    updateFilterDropdown: (DropDownState) -> Unit,
    updateOptionsDropdown: (List<TaskPartDTO>, String) -> Unit,
    updateSortDropdown: (DropDownState) -> Unit,
    updateTaskCollapsible: (String, Boolean) -> Unit,
    updateCreatedTaskCollapsible: (String, Boolean) -> Unit,
    updateEditNameDialogState: (EditNameDialogState) -> Unit,
    updateSearchDialogState: (SearchUserDialogState) -> Unit,
    updateDateDialogState: (DateDialogState) -> Unit,
    updateIsFilterDropdown: (DropDownState) -> Unit,
    updateOptionsFilterDropdown: (DropDownMultiselect) -> Unit,
    changeName: (Int, String, () -> Unit) -> Unit,
    changeAssignee: (Int, List<UserDTO>, () -> Unit) -> Unit,
    updateSearchState: (SearchState) -> Unit,
    updateViewAssigneeDialogState: (List<UserDTO>) -> Unit,
    searchUser: (String, (List<UserDTO>) -> Unit) -> Unit,
    changeDue: (Int, LocalDateTime, () -> Unit) -> Unit,
    changePriority: (Int, String, () -> Unit) -> Unit,
    changeStatus: (Int, String, () -> Unit) -> Unit,
    changeType: (Int, String, () -> Unit) -> Unit,
    updateTasks: (List<TaskPartDTO>) -> Unit,
    updateCreatedTasks: (List<TaskPartDTO>) -> Unit,
    refreshTasks: () -> Unit,
    refreshCreatedTasks: () -> Unit
) {
    val removeDialog: () -> Unit = { updateDialogState(DashboardDialogs.NONE) }
    val radioSelect: (String) -> Unit = { updateRadioDialogState(dialogsState.radioButtonDialogState.copy(selected = it)) }
    val openDialog: (DashboardDialogs) -> Unit = { updateDialogState(it) }

    val refreshTaskState = rememberSwipeRefreshState(isRefreshing = dashboardState.isTaskRefreshing)
    val refreshCreatedTaskState = rememberSwipeRefreshState(isRefreshing = dashboardState.isCreatedTaskRefreshing)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (windowInfo.screenWidthInfo) {
                is WindowInfo.WindowType.Compact, is WindowInfo.WindowType.Medium -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CustomDropDownNoMenu(text = "LIST", icon = Icons.AutoMirrored.Filled.List) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 0))
                        }
                        CustomDropDownNoMenu(text = "GRID", icon = Icons.Filled.GridOn) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 1))
                        }
                        CustomDropDownNoMenu(text = "CALENDAR", icon = Icons.Filled.CalendarMonth) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 2))
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CustomDropDown(
                            prefix = "GROUP",
                            dropDownState = dashboardState.groupDropDown,
                            icon = Icons.Filled.Category,
                            onArrowClick = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(selected = it, expanded = false))
                                filterAllTabs()
                            }
                        )
                        CustomDropDown(
                            prefix = "FILTER",
                            dropDownState = dashboardState.filterDropDown,
                            icon = Icons.Filled.FilterList,
                            onArrowClick = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(selected = it, expanded = false))
                                try {
                                    updateOptionsDropdown(if (sharedState.dashboardBottomBarIdx == 0) rawTasks else rawCreatedTasks, it)
                                    openDialog(DashboardDialogs.FILTER)
                                } catch (e: IllegalStateException) {
                                    filterAllTabs()
                                }
                            }
                        )
                        CustomDropDown(
                            prefix = "SORT",
                            dropDownState = dashboardState.sortDropDown,
                            icon = Icons.AutoMirrored.Filled.Sort,
                            onArrowClick = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(selected = it, expanded = false))
                                filterAllTabs()
                            }
                        )
                    }
                }
                is WindowInfo.WindowType.Expanded -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CustomDropDownNoMenu(text = "LIST", icon = Icons.AutoMirrored.Filled.List) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 0))
                        }
                        CustomDropDownNoMenu(text = "GRID", icon = Icons.Filled.GridOn) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 1))
                        }
                        CustomDropDownNoMenu(text = "CALENDAR", icon = Icons.Filled.CalendarMonth) {
                            updateSharedState(sharedState.copy(dashboardViewIdx = 2))
                        }
                        CustomDropDown(
                            prefix = "GROUP",
                            dropDownState = dashboardState.groupDropDown,
                            icon = Icons.Filled.Category,
                            onArrowClick = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateGroupDropdown(dashboardState.groupDropDown.copy(selected = it, expanded = false))
                                filterAllTabs()
                            }
                        )
                        CustomDropDown(
                            prefix = "FILTER",
                            dropDownState = dashboardState.filterDropDown,
                            icon = Icons.Filled.FilterList,
                            onArrowClick = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateFilterDropdown(dashboardState.filterDropDown.copy(selected = it, expanded = false))
                                try {
                                    updateOptionsDropdown(if (sharedState.dashboardBottomBarIdx == 0) rawTasks else rawCreatedTasks, it)
                                    openDialog(DashboardDialogs.FILTER)
                                } catch (e: IllegalStateException) {
                                    filterAllTabs()
                                }
                            }
                        )
                        CustomDropDown(
                            prefix = "SORT",
                            dropDownState = dashboardState.sortDropDown,
                            icon = Icons.AutoMirrored.Filled.Sort,
                            onArrowClick = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(expanded = true))
                            },
                            onDismissRequest = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(expanded = false))
                            },
                            onItemSelect = {
                                updateSortDropdown(dashboardState.sortDropDown.copy(selected = it, expanded = false))
                                filterAllTabs()
                            }
                        )
                    }
                }
            }
            when (sharedState.dashboardBottomBarIdx) {
                0 -> {
                    DashboardMenu(
                        windowInfo = windowInfo,
                        isCreator = false,
                        process = process,
                        tasks = tasks,
                        rawTasks = rawTasks,
                        navController = navController,
                        sharedState = sharedState,
                        updateEditNameDialogState = updateEditNameDialogState,
                        updateRadioDialogState = updateRadioDialogState,
                        updateSearchDialogState = updateSearchDialogState,
                        updateDateDialogState = updateDateDialogState,
                        updateSharedState = updateSharedState,
                        openDialog = openDialog,
                        updateViewAssigneeDialogState = updateViewAssigneeDialogState,
                        swipeRefreshState = refreshTaskState,
                        onRefresh = refreshTasks,
                        updateTaskCollapsible = updateTaskCollapsible
                    )
                }
                1 -> {
                    DashboardMenu(
                        windowInfo = windowInfo,
                        isCreator = true,
                        process = process2,
                        tasks = createdTasks,
                        rawTasks = rawCreatedTasks,
                        navController = navController,
                        sharedState = sharedState,
                        updateEditNameDialogState = updateEditNameDialogState,
                        updateRadioDialogState = updateRadioDialogState,
                        updateSearchDialogState = updateSearchDialogState,
                        updateDateDialogState = updateDateDialogState,
                        updateSharedState = updateSharedState,
                        openDialog = openDialog,
                        updateViewAssigneeDialogState = updateViewAssigneeDialogState,
                        swipeRefreshState = refreshCreatedTaskState,
                        onRefresh = refreshCreatedTasks,
                        updateTaskCollapsible = updateCreatedTaskCollapsible
                    )
                }
            }
        }
        when (dashboardDialogs) {
            DashboardDialogs.NONE -> {  }
            DashboardDialogs.NAME -> {
                EditNameDialog(
                    text = "Name",
                    editNameDialogState = dialogsState.editNameDialogState,
                    onDismissRequest = removeDialog,
                    onApplyClick = { task, name ->
                        changeName(task, name) {
                            updateCreatedTasks(
                                rawCreatedTasks.map {
                                    if (it.taskId == task) it.copy(title = name) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    onTextChange = { updateEditNameDialogState(dialogsState.editNameDialogState.copy(name = it)) }
                )
            }
            DashboardDialogs.ASSIGNEE -> {
                SearchUserDialog(
                    text = "Assignees",
                    searchUserDialogState = dialogsState.searchUserDialogState,
                    searchState = dialogsState.searchState,
                    onUserClick = { navController.navigate("${Routes.PROFILE}/$it") },
                    onDismissRequest = removeDialog,
                    onApplyClick = { task, assignees ->
                        changeAssignee(task, assignees) {
                            updateCreatedTasks(
                                rawCreatedTasks.map {
                                    if (it.taskId == task) it.copy(assignees = assignees) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    onSearch = { query ->
                        searchUser(query) { updateSearchState(dialogsState.searchState.copy(results = it)) }
                    },
                    onQueryChange = { updateSearchState(dialogsState.searchState.copy(searchQuery = it)) },
                    onActiveChange = { updateSearchState(dialogsState.searchState.copy(isActive = it)) },
                    onTrailingIconClick = {
                        if (dialogsState.searchState.searchQuery.isEmpty()) {
                            updateSearchState(dialogsState.searchState.copy(isActive = false))
                        } else {
                            updateSearchState(dialogsState.searchState.copy(searchQuery = ""))
                        }
                    },
                    onUserAdd = { user ->
                        updateSearchDialogState(
                            dialogsState.searchUserDialogState.copy(
                                users = if (user in dialogsState.searchUserDialogState.users) {
                                    dialogsState.searchUserDialogState.users
                                } else dialogsState.searchUserDialogState.users + user
                            )
                        )
                    },
                    onUserRemove = { user ->
                        updateSearchDialogState(
                            dialogsState.searchUserDialogState.copy(
                                users = if (user in dialogsState.searchUserDialogState.users) {
                                    dialogsState.searchUserDialogState.users - user
                                } else dialogsState.searchUserDialogState.users
                            )
                        )
                    }
                )
            }
            DashboardDialogs.DUE -> {
                DateTimePickerDialog(
                    text = "Due Date",
                    dateDialogState = dialogsState.dateDialogState,
                    onDismissRequest = removeDialog,
                    onApplyClick = { task, due ->
                        changeDue(task, due) {
                            updateCreatedTasks(
                                rawCreatedTasks.map {
                                    if (it.taskId == task) it.copy(due = due) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    datePicker = { updateDateDialogState(dialogsState.dateDialogState.copy(datePickerEnabled = it)) },
                    timePicker = { updateDateDialogState(dialogsState.dateDialogState.copy(timePickerEnabled = it)) },
                    selected = { updateDateDialogState(dialogsState.dateDialogState.copy(selected = it, datePickerEnabled = false, timePickerEnabled = false)) }
                )
            }
            DashboardDialogs.PRIORITY -> {
                RadioButtonDialog(
                    text = "Priority",
                    radioButtonDialogState = dialogsState.radioButtonDialogState,
                    onApplyClick = { task, priority ->
                        changePriority(task, priority) {
                            updateCreatedTasks(
                                rawCreatedTasks.map {
                                    if (it.taskId == task) it.copy(priority = priority) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    onDismissRequest = removeDialog,
                    onRadioSelect = radioSelect
                )
            }
            DashboardDialogs.STATUS -> {
                RadioButtonDialog(
                    text = "Status",
                    radioButtonDialogState = dialogsState.radioButtonDialogState,
                    onApplyClick = { task, status ->
                        changeStatus(task, status) {
                            updateTasks(
                                rawTasks.map {
                                    if (it.taskId == task) it.copy(status = status) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    onDismissRequest = removeDialog,
                    onRadioSelect = radioSelect
                )
            }
            DashboardDialogs.TYPE -> {
                RadioButtonDialog(
                    text = "Type",
                    radioButtonDialogState = dialogsState.radioButtonDialogState,
                    onApplyClick = { task, type ->
                        changeType(task, type) {
                            updateCreatedTasks(
                                rawCreatedTasks.map {
                                    if (it.taskId == task) it.copy(type = type) else it
                                }
                            )
                            filterTab(sharedState.dashboardBottomBarIdx)
                        }
                    },
                    onDismissRequest = removeDialog,
                    onRadioSelect = radioSelect
                )
            }
            DashboardDialogs.FILTER -> {
                FilterDialog(
                    dashboardState = dashboardState,
                    removeDialog = removeDialog,
                    updateIsFilterDropdown = updateIsFilterDropdown,
                    updateOptionsFilterDropdown = updateOptionsFilterDropdown,
                    onApplyClick = filterAllTabs
                )
            }
            DashboardDialogs.VIEW -> {
                ViewAssigneeDialog(
                    assignees = dialogsState.viewAssigneeDialogState,
                    onUserClick = { navController.navigate("${Routes.PROFILE}/$it") },
                    onDismissRequest = removeDialog
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardMenu(
    windowInfo: WindowInfo,
    isCreator: Boolean,
    process: ProcessState,
    tasks: DashboardDataState,
    rawTasks: List<TaskPartDTO>,
    navController: NavController,
    sharedState: SharedViewModelState,
    updateEditNameDialogState: (EditNameDialogState) -> Unit,
    updateRadioDialogState: (RadioButtonDialogState) -> Unit,
    updateSearchDialogState: (SearchUserDialogState) -> Unit,
    updateDateDialogState: (DateDialogState) -> Unit,
    updateSharedState: (SharedViewModelState) -> Unit,
    openDialog: (DashboardDialogs) -> Unit,
    updateViewAssigneeDialogState: (List<UserDTO>) -> Unit,
    swipeRefreshState: SwipeRefreshState,
    onRefresh: () -> Unit,
    updateTaskCollapsible: (String, Boolean) -> Unit
) {
    when (process) {
        is ProcessState.Loading -> {
            Loading(PaddingValues(0.dp))
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, PaddingValues(0.dp), process.message, swipeRefreshState, onRefresh)
        }
        is ProcessState.Success -> {
            SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
                when (sharedState.dashboardViewIdx) {
                    0 -> {
                        if (tasks.isNotEmpty()) {
                            LazyColumn {
                                tasks.forEach { pair ->
                                    stickyHeader {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OneLineText(
                                                text = pair.first.label,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                            IconButton(
                                                onClick = {
                                                    updateTaskCollapsible(pair.first.label, !pair.first.collapsible)
                                                },
                                                modifier = Modifier.padding(5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (pair.first.collapsible) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                    items(items = pair.second) { task ->
                                        AnimatedVisibility(pair.first.collapsible) {
                                            DashboardList(
                                                windowInfo = windowInfo,
                                                task = task,
                                                navigateToProfile = { navController.navigate("${Routes.PROFILE}/$it") },
                                                openViewDialog = {
                                                    updateViewAssigneeDialogState(task.assignees)
                                                    openDialog(DashboardDialogs.VIEW)
                                                },
                                                onTitleClick = {
                                                    if (isCreator) {
                                                        updateEditNameDialogState(
                                                            EditNameDialogState(
                                                                task.title,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.NAME)
                                                    }
                                                },
                                                onStatusClick = {
                                                    if (!isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("OPEN", "IN PROGRESS", "ON HOLD", "COMPLETE"),
                                                                task.status,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.STATUS)
                                                    }
                                                },
                                                onAssigneeClick = {
                                                    if (isCreator) {
                                                        updateSearchDialogState(
                                                            SearchUserDialogState(
                                                                task.assignees,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.ASSIGNEE)
                                                    }
                                                },
                                                onPriorityClick = {
                                                    if (isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                                                                task.priority,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.PRIORITY)
                                                    }
                                                },
                                                onDueClick = {
                                                    if (isCreator) {
                                                        updateDateDialogState(
                                                            DateDialogState(
                                                                selected = task.due,
                                                                datePickerEnabled = false,
                                                                timePickerEnabled = false,
                                                                taskId = task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.DUE)
                                                    }
                                                },
                                                onTypeClick = {
                                                    if (isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("TASK", "MILESTONE"),
                                                                task.type,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.TYPE)
                                                    }
                                                },
                                                onViewClick = { navController.navigate("${Routes.ABOUT_TASK}/${task.taskId}") }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Spacer(modifier = Modifier.padding(100.dp))
                            }
                        }
                    }
                    1 -> {
                        if (tasks.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(
                                    when (windowInfo.screenWidthInfo) {
                                        is WindowInfo.WindowType.Compact -> 2
                                        is WindowInfo.WindowType.Medium -> 3
                                        is WindowInfo.WindowType.Expanded -> 6
                                    }
                                )
                            ) {
                                tasks.forEach { pair ->
                                    header {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OneLineText(
                                                text = pair.first.label,
                                                modifier = Modifier.padding(10.dp)
                                            )
                                            IconButton(
                                                onClick = {
                                                    updateTaskCollapsible(pair.first.label, !pair.first.collapsible)
                                                },
                                                modifier = Modifier.padding(5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (pair.first.collapsible) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                    items(items = pair.second) { task ->
                                        AnimatedVisibility(pair.first.collapsible) {
                                            DashboardGrid(
                                                task = task,
                                                navigateToProfile = { navController.navigate("${Routes.PROFILE}/$it") },
                                                openViewDialog = {
                                                    updateViewAssigneeDialogState(task.assignees)
                                                    openDialog(DashboardDialogs.VIEW)
                                                },
                                                onTitleClick = {
                                                    if (isCreator) {
                                                        updateEditNameDialogState(
                                                            EditNameDialogState(
                                                                task.title,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.NAME)
                                                    }
                                                },
                                                onStatusClick = {
                                                    if (!isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("OPEN", "IN PROGRESS", "ON HOLD", "COMPLETE"),
                                                                task.status,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.STATUS)
                                                    }
                                                },
                                                onAssigneeClick = {
                                                    if (isCreator) {
                                                        updateSearchDialogState(
                                                            SearchUserDialogState(
                                                                task.assignees,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.ASSIGNEE)
                                                    }
                                                },
                                                onPriorityClick = {
                                                    if (isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                                                                task.priority,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.PRIORITY)
                                                    }
                                                },
                                                onDueClick = {
                                                    if (isCreator) {
                                                        updateDateDialogState(
                                                            DateDialogState(
                                                                selected = task.due,
                                                                datePickerEnabled = false,
                                                                timePickerEnabled = false,
                                                                taskId = task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.DUE)
                                                    }
                                                },
                                                onTypeClick = {
                                                    if (isCreator) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("TASK", "MILESTONE"),
                                                                task.type,
                                                                task.taskId
                                                            )
                                                        )
                                                        openDialog(DashboardDialogs.TYPE)
                                                    }
                                                },
                                                onViewClick = { navController.navigate("${Routes.ABOUT_TASK}/${task.taskId}") }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Spacer(modifier = Modifier.padding(100.dp))
                            }
                        }
                    }
                    2 -> {
                        Calendar(
                            tasks = rawTasks,
                            calendarTabIdx = sharedState.calendarTabIdx,
                            updateCalendarIdx = { updateSharedState(sharedState.copy(calendarTabIdx = it)) },
                            navigate = { navController.navigate("${Routes.ABOUT_TASK}/$it") }
                        )
                    }
                }
            }
        }
    }
}