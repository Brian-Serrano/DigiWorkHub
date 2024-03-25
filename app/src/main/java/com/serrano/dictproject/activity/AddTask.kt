package com.serrano.dictproject.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.dictproject.customui.CustomButton
import com.serrano.dictproject.customui.OneLineText
import com.serrano.dictproject.customui.RememberWindowInfo
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.dialog.DateTimePickerDialog
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.RadioButtonDialog
import com.serrano.dictproject.customui.dialog.SearchUserDialog
import com.serrano.dictproject.customui.dialog.ViewAssigneeDialog
import com.serrano.dictproject.customui.dropdown.CustomDropDown2
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.AddTaskDialogs
import com.serrano.dictproject.utils.AddTaskState
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DialogsState
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.RadioButtonDialogState
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils

@Composable
fun AddTask(
    windowInfo: WindowInfo,
    addTaskDialogs: AddTaskDialogs,
    navController: NavController,
    paddingValues: PaddingValues,
    dialogsState: DialogsState,
    addTaskState: AddTaskState,
    updateDialogState: (AddTaskDialogs) -> Unit,
    updateRadioDialogState: (RadioButtonDialogState) -> Unit,
    updateEditNameDialogState: (EditNameDialogState) -> Unit,
    updateDateDialogState: (DateDialogState) -> Unit,
    updateSearchDialogState: (SearchUserDialogState) -> Unit,
    updateViewAssigneeDialogState: (List<User>) -> Unit,
    updateTaskState: (AddTaskState) -> Unit,
    updateSearchState: (SearchState) -> Unit,
    searchUser: (String, (List<User>) -> Unit) -> Unit,
    addTask: (() -> Unit) -> Unit
) {

    val removeDialog: () -> Unit = { updateDialogState(AddTaskDialogs.NONE) }
    val radioSelect: (String) -> Unit = { updateRadioDialogState(dialogsState.radioButtonDialogState.copy(selected = it)) }
    val openDialog: (AddTaskDialogs) -> Unit = { updateDialogState(it) }

    val nameComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "NAME",
            selected = addTaskState.name,
            onArrowClick = {
                updateEditNameDialogState(
                    EditNameDialogState(
                        addTaskState.name,
                        0
                    )
                )
                openDialog(AddTaskDialogs.NAME)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isOneLine = false
        )
    }
    val descriptionComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "DESCRIPTION",
            selected = addTaskState.description,
            onArrowClick = {
                updateEditNameDialogState(
                    EditNameDialogState(
                        addTaskState.description,
                        0
                    )
                )
                openDialog(AddTaskDialogs.DESCRIPTION)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isOneLine = false
        )
    }
    val priorityComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "PRIORITY",
            selected = addTaskState.priority,
            onArrowClick = {
                updateRadioDialogState(
                    RadioButtonDialogState(
                        listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                        addTaskState.priority,
                        0
                    )
                )
                openDialog(AddTaskDialogs.PRIORITY)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
    val typeComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "TYPE",
            selected = addTaskState.type,
            onArrowClick = {
                updateRadioDialogState(
                    RadioButtonDialogState(
                        listOf("TASK", "MILESTONE"),
                        addTaskState.type,
                        0
                    )
                )
                openDialog(AddTaskDialogs.TYPE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
    val dueComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "DUE DATE",
            selected = Utils.dateTimeToDateTimeString(addTaskState.due),
            onArrowClick = {
                updateDateDialogState(
                    DateDialogState(
                        selected = addTaskState.due,
                        datePickerEnabled = false,
                        timePickerEnabled = false,
                        taskId = 0
                    )
                )
                openDialog(AddTaskDialogs.DUE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
    val assigneesComposable: @Composable RowScope.() -> Unit = {
        CustomDropDown2(
            text = "ASSIGNEE",
            selected = addTaskState.assignees.joinToString(separator = "\n") { it.name },
            onArrowClick = {
                updateSearchDialogState(
                    SearchUserDialogState(
                        addTaskState.assignees,
                        0
                    )
                )
                openDialog(AddTaskDialogs.ASSIGNEE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isOneLine = false
        )
    }
    val assigneesIconComposable: @Composable RowScope.() -> Unit = {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-25).dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            addTaskState.assignees.forEach {
                IconButton(
                    onClick = {
                        updateViewAssigneeDialogState(addTaskState.assignees)
                        openDialog(AddTaskDialogs.VIEW)
                    }
                ) {
                    Icon(
                        bitmap = Utils.encodedStringToImage(it.image),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
    val buttonComposable: @Composable RowScope.() -> Unit = {
        CustomButton(
            text = "ADD TASK",
            onClick = { addTask { navController.navigate("Dashboard") } },
            enabled = addTaskState.buttonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp)
        )
    }

    val r: @Composable (@Composable RowScope.() -> Unit) -> Unit = { content ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OneLineText(
                text = "CREATE TASK",
                style = MaterialTheme.typography.titleLarge
            )
            when (windowInfo.screenWidthInfo) {
                is WindowInfo.WindowType.Compact -> {
                    r { nameComposable() }
                    r { descriptionComposable() }
                    r {
                        priorityComposable()
                        typeComposable()
                    }
                    r { dueComposable() }
                    r { assigneesComposable() }
                    r { assigneesIconComposable() }
                    r { buttonComposable() }
                }
                is WindowInfo.WindowType.Medium -> {
                    r { nameComposable() }
                    r { descriptionComposable() }
                    r {
                        priorityComposable()
                        typeComposable()
                        dueComposable()
                    }
                    r {
                        assigneesComposable()
                        assigneesIconComposable()
                    }
                    r { buttonComposable() }
                }
                is WindowInfo.WindowType.Expanded -> {
                    r {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)) {
                            r { nameComposable() }
                            r {
                                priorityComposable()
                                typeComposable()
                                dueComposable()
                            }
                            r { buttonComposable() }
                        }
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)) {
                            r { descriptionComposable() }
                            r {
                                assigneesComposable()
                                assigneesIconComposable()
                            }
                        }
                    }
                }
            }
            Text(
                text = addTaskState.errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(5.dp),
                color = MaterialTheme.colorScheme.error
            )
        }
        when (addTaskDialogs) {
            AddTaskDialogs.NONE -> {  }
            AddTaskDialogs.NAME -> {
                EditNameDialog(
                    text = "Name",
                    editNameDialogState = dialogsState.editNameDialogState,
                    onDismissRequest = removeDialog,
                    onApplyClick = { _, name -> updateTaskState(addTaskState.copy(name = name)) },
                    onTextChange = { updateEditNameDialogState(dialogsState.editNameDialogState.copy(name = it)) }
                )
            }
            AddTaskDialogs.ASSIGNEE -> {
                SearchUserDialog(
                    text = "Assignees",
                    searchUserDialogState = dialogsState.searchUserDialogState,
                    searchState = dialogsState.searchState,
                    onUserClick = { navController.navigate("Profile/$it") },
                    onDismissRequest = removeDialog,
                    onApplyClick = { _, assignees -> updateTaskState(addTaskState.copy(assignees = assignees)) },
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
            AddTaskDialogs.DUE -> {
                DateTimePickerDialog(
                    text = "Due Date",
                    dateDialogState = dialogsState.dateDialogState,
                    onDismissRequest = removeDialog,
                    onApplyClick = { _, due -> updateTaskState(addTaskState.copy(due = due)) },
                    datePicker = { updateDateDialogState(dialogsState.dateDialogState.copy(datePickerEnabled = it)) },
                    timePicker = { updateDateDialogState(dialogsState.dateDialogState.copy(timePickerEnabled = it)) },
                    selected = { updateDateDialogState(dialogsState.dateDialogState.copy(selected = it, datePickerEnabled = false, timePickerEnabled = false)) }
                )
            }
            AddTaskDialogs.PRIORITY -> {
                RadioButtonDialog(
                    text = "Priority",
                    radioButtonDialogState = dialogsState.radioButtonDialogState,
                    onApplyClick = { _, priority -> updateTaskState(addTaskState.copy(priority = priority)) },
                    onDismissRequest = removeDialog,
                    onRadioSelect = radioSelect
                )
            }
            AddTaskDialogs.TYPE -> {
                RadioButtonDialog(
                    text = "Type",
                    radioButtonDialogState = dialogsState.radioButtonDialogState,
                    onApplyClick = { _, type -> updateTaskState(addTaskState.copy(type = type)) },
                    onDismissRequest = removeDialog,
                    onRadioSelect = radioSelect
                )
            }
            AddTaskDialogs.DESCRIPTION -> {
                EditNameDialog(
                    text = "Description",
                    editNameDialogState = dialogsState.editNameDialogState,
                    onDismissRequest = removeDialog,
                    onApplyClick = { _, description -> updateTaskState(addTaskState.copy(description = description)) },
                    onTextChange = { updateEditNameDialogState(dialogsState.editNameDialogState.copy(name = it)) }
                )
            }
            AddTaskDialogs.VIEW -> {
                ViewAssigneeDialog(
                    assignees = dialogsState.viewAssigneeDialogState,
                    onUserClick = { navController.navigate("Profile/$it") },
                    onDismissRequest = removeDialog
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun AddTaskPrev() {
    DICTProjectTheme {
        AddTask(
            windowInfo = RememberWindowInfo(),
            addTaskDialogs = AddTaskDialogs.NONE,
            navController = rememberNavController(),
            paddingValues = PaddingValues(0.dp),
            dialogsState = DialogsState(
                EditNameDialogState(),
                SearchUserDialogState(),
                DateDialogState(),
                RadioButtonDialogState(),
                SearchState(),
                emptyList()
            ),
            addTaskState = AddTaskState(),
            updateDialogState = {},
            updateRadioDialogState = {},
            updateEditNameDialogState = {},
            updateDateDialogState = {},
            updateSearchDialogState = {},
            updateViewAssigneeDialogState = {},
            updateTaskState = {},
            updateSearchState = {},
            searchUser = { _, _ -> },
            addTask = { _ ->  }
        )
    }
}