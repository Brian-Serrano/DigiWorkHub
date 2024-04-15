package com.serrano.dictproject.activity

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.CustomTab
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.button.TextWithEditButton
import com.serrano.dictproject.customui.dialog.ConfirmDialog
import com.serrano.dictproject.customui.dialog.DateTimePickerDialog
import com.serrano.dictproject.customui.dialog.EditAssigneeDialog
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.RadioButtonDialog
import com.serrano.dictproject.customui.dialog.SearchUserDialog
import com.serrano.dictproject.customui.dialog.ViewAssigneeDialog
import com.serrano.dictproject.customui.dropdown.DueDropDown
import com.serrano.dictproject.customui.dropdown.PriorityDropDown
import com.serrano.dictproject.customui.dropdown.StatusDropDown
import com.serrano.dictproject.customui.menu.AddAttachmentMenu
import com.serrano.dictproject.customui.menu.AddChecklistMenu
import com.serrano.dictproject.customui.menu.AddCommentMenu
import com.serrano.dictproject.customui.menu.AddSubtaskMenu
import com.serrano.dictproject.customui.menu.AttachmentBox
import com.serrano.dictproject.customui.menu.ChecklistBox
import com.serrano.dictproject.customui.menu.CommentBox
import com.serrano.dictproject.customui.menu.SubtaskBox
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.utils.AboutTaskDialogs
import com.serrano.dictproject.utils.AboutTaskState
import com.serrano.dictproject.utils.AddAttachmentState
import com.serrano.dictproject.utils.AddChecklistState
import com.serrano.dictproject.utils.AddCommentState
import com.serrano.dictproject.utils.AddSubtaskState
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.DialogsState
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.RadioButtonDialogState
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.TaskState
import com.serrano.dictproject.utils.UserDTO
import java.time.LocalDateTime

@Composable
fun AboutTask(
    windowInfo: WindowInfo,
    aboutTaskDialogs: AboutTaskDialogs,
    preferences: Preferences,
    navController: NavController,
    paddingValues: PaddingValues,
    context: Context,
    process: ProcessState,
    task: TaskState,
    dialogsState: DialogsState,
    aboutTaskState: AboutTaskState,
    updateAddSubtaskState: (AddSubtaskState) -> Unit,
    updateAddCommentState: (AddCommentState) -> Unit,
    updateAddChecklistState: (AddChecklistState) -> Unit,
    updateAddAttachmentState: (AddAttachmentState) -> Unit,
    updateConfirmDialogState: (ConfirmDialogState) -> Unit,
    updateDialogState: (AboutTaskDialogs) -> Unit,
    updateTask: (TaskState) -> Unit,
    updateRadioDialogState: (RadioButtonDialogState) -> Unit,
    updateEditNameDialogState: (EditNameDialogState) -> Unit,
    updateDateDialogState: (DateDialogState) -> Unit,
    updateSearchDialogState: (SearchUserDialogState) -> Unit,
    updateSearchState: (SearchState) -> Unit,
    updateViewAssigneeDialogState: (List<UserDTO>) -> Unit,
    changeAssignee: (Int, List<UserDTO>, () -> Unit) -> Unit,
    changeName: (Int, String, () -> Unit) -> Unit,
    searchUser: (String, (List<UserDTO>) -> Unit) -> Unit,
    changeDue: (Int, LocalDateTime, () -> Unit) -> Unit,
    changePriority: (Int, String, () -> Unit) -> Unit,
    changeStatus: (Int, String, () -> Unit) -> Unit,
    changeType: (Int, String, () -> Unit) -> Unit,
    changeDescription: (Int, String, () -> Unit) -> Unit,
    sendComment: () -> Unit,
    addChecklist: () -> Unit,
    addSubtask: () -> Unit,
    uploadAttachment: () -> Unit,
    changeSubtaskDescription: (Int, String) -> Unit,
    changeSubtaskPriority: (Int, String) -> Unit,
    changeSubtaskDueDate: (Int, LocalDateTime) -> Unit,
    editSubtaskAssignees: (Int, List<UserDTO>) -> Unit,
    changeSubtaskType: (Int, String) -> Unit,
    changeSubtaskStatus: (Int, String) -> Unit,
    toggleChecklist: (Int, Boolean) -> Unit,
    likeComment: (Int, Int) -> Unit,
    downloadAttachment: (String, String) -> Unit,
    refreshTaskInfo: () -> Unit,
    deleteTask: (Int, () -> Unit) -> Unit,
    deleteComment: (Int) -> Unit,
    deleteSubtask: (Int) -> Unit,
    deleteChecklist: (Int) -> Unit,
    deleteAttachment: (Int) -> Unit
) {
    val refreshState = rememberSwipeRefreshState(isRefreshing = task.isRefreshing)
    val removeDialog: () -> Unit = { updateDialogState(AboutTaskDialogs.NONE) }
    val radioSelect: (String) -> Unit = { updateRadioDialogState(dialogsState.radioButtonDialogState.copy(selected = it)) }
    val openDialog: (AboutTaskDialogs) -> Unit = { updateDialogState(it) }
    val updateDatePicker: (Boolean) -> Unit = { updateDateDialogState(dialogsState.dateDialogState.copy(datePickerEnabled = it)) }
    val updateTimePicker: (Boolean) -> Unit = { updateDateDialogState(dialogsState.dateDialogState.copy(timePickerEnabled = it)) }
    val selectDateTime: (LocalDateTime) -> Unit = { updateDateDialogState(dialogsState.dateDialogState.copy(selected = it, datePickerEnabled = false, timePickerEnabled = false)) }
    val navigateToProfile: (Int) -> Unit = { navController.navigate("${Routes.PROFILE}/$it") }
    val editTextDialog: (String) -> Unit = { updateEditNameDialogState(dialogsState.editNameDialogState.copy(name = it)) }


    val onUserAdd: (UserDTO) -> Unit = { user ->
        updateSearchDialogState(
            dialogsState.searchUserDialogState.copy(
                users = if (user in dialogsState.searchUserDialogState.users) {
                    dialogsState.searchUserDialogState.users
                } else dialogsState.searchUserDialogState.users + user
            )
        )
    }
    val onUserRemove: (UserDTO) -> Unit = { user ->
        updateSearchDialogState(
            dialogsState.searchUserDialogState.copy(
                users = if (user in dialogsState.searchUserDialogState.users) {
                    dialogsState.searchUserDialogState.users - user
                } else dialogsState.searchUserDialogState.users
            )
        )
    }


    val statusComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Status: ")
            StatusDropDown(text = task.status) {
                if (task.assignees.any { it.id == preferences.id }) {
                    updateRadioDialogState(
                        RadioButtonDialogState(
                            listOf("OPEN", "IN PROGRESS", "ON HOLD", "COMPLETE"),
                            task.status,
                            task.taskId
                        )
                    )
                    openDialog(AboutTaskDialogs.TASK_STATUS)
                } else {
                    MiscUtils.toast(context, "Only task assignees can edit status.")
                }
            }
        }
    }
    val priorityComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Priority: ")
            PriorityDropDown(text = task.priority) {
                if (preferences.id == task.creator.id) {
                    updateRadioDialogState(
                        RadioButtonDialogState(
                            listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                            task.priority,
                            task.taskId
                        )
                    )
                    openDialog(AboutTaskDialogs.TASK_PRIORITY)
                } else {
                    MiscUtils.toast(context, "Only task creator can edit priority.")
                }
            }
        }
    }
    val dueComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Due: ")
            DueDropDown(
                date = task.due,
                onClick = {
                    if (preferences.id == task.creator.id) {
                        updateDateDialogState(
                            DateDialogState(
                                selected = task.due,
                                datePickerEnabled = false,
                                timePickerEnabled = false,
                                taskId = task.taskId
                            )
                        )
                        openDialog(AboutTaskDialogs.TASK_DUE)
                    } else {
                        MiscUtils.toast(context, "Only task creator can edit due date.")
                    }
                },
                includeTime = true
            )
        }
    }
    val sentDateComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Task Started: ")
            OneLineText(text = DateUtils.dateTimeToDateTimeString(task.sentDate))
        }
    }
    val assigneesComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Assignees: ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
                    task.assignees.forEach {
                        IconButton(
                            onClick = {
                                updateViewAssigneeDialogState(task.assignees)
                                openDialog(AboutTaskDialogs.VIEW)
                            }
                        ) {
                            Icon(
                                bitmap = FileUtils.encodedStringToImage(it.image),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
                IconButton(
                    onClick = {
                        if (preferences.id == task.creator.id) {
                            updateSearchDialogState(
                                SearchUserDialogState(
                                    task.assignees,
                                    task.taskId
                                )
                            )
                            openDialog(AboutTaskDialogs.TASK_ASSIGNEE)
                        } else {
                            MiscUtils.toast(context, "Only task creator can edit assignees.")
                        }
                    },
                    modifier = Modifier
                        .size(25.dp)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
    val creatorComposable: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OneLineText(text = "Created by: ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                OneLineText(text = task.creator.name)
                IconButton(onClick = { navigateToProfile(task.creator.id) }) {
                    Icon(
                        bitmap = FileUtils.encodedStringToImage(task.creator.image),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }

    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message, refreshState, refreshTaskInfo)
        }
        is ProcessState.Success -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SwipeRefresh(state = refreshState, onRefresh = refreshTaskInfo) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .border(
                                    BorderStroke(width = 2.dp, Color.Black),
                                    MaterialTheme.shapes.extraSmall
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OneLineText(
                                    text = "ID: ${task.taskId}",
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            MaterialTheme.shapes.small
                                        )
                                )
                                OneLineText(
                                    text = "TYPE: ${task.type}",
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(
                                            BorderStroke(
                                                2.dp,
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            MaterialTheme.shapes.small
                                        )
                                        .clickable {
                                            if (preferences.id == task.creator.id) {
                                                updateRadioDialogState(
                                                    RadioButtonDialogState(
                                                        listOf("TASK", "MILESTONE"),
                                                        task.type,
                                                        task.taskId
                                                    )
                                                )
                                                openDialog(AboutTaskDialogs.TASK_TYPE)
                                            } else {
                                                MiscUtils.toast(
                                                    context,
                                                    "Only task creator can edit type."
                                                )
                                            }
                                        }
                                )
                            }
                            Column(modifier = Modifier.padding(5.dp)) {
                                TextWithEditButton(
                                    text = task.title,
                                    onEditButtonClick = {
                                        if (preferences.id == task.creator.id) {
                                            updateEditNameDialogState(
                                                EditNameDialogState(
                                                    task.title,
                                                    task.taskId
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.TASK_NAME)
                                        } else {
                                            MiscUtils.toast(context, "Only task creator can edit name.")
                                        }
                                    },
                                    style = MaterialTheme.typography.headlineMedium,
                                    isOneLine = false
                                )
                            }
                            when (windowInfo.screenWidthInfo) {
                                is WindowInfo.WindowType.Compact -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        statusComposable()
                                        priorityComposable()
                                        dueComposable()
                                        sentDateComposable()
                                        assigneesComposable()
                                        creatorComposable()
                                    }
                                }
                                is WindowInfo.WindowType.Medium -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            statusComposable()
                                            priorityComposable()
                                            dueComposable()
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            sentDateComposable()
                                            assigneesComposable()
                                            creatorComposable()
                                        }
                                    }
                                }
                                is WindowInfo.WindowType.Expanded -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            statusComposable()
                                            priorityComposable()
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            dueComposable()
                                            sentDateComposable()
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            assigneesComposable()
                                            creatorComposable()
                                        }
                                    }
                                }
                            }
                            Column(modifier = Modifier.padding(5.dp)) {
                                TextWithEditButton(
                                    text = task.description,
                                    onEditButtonClick = {
                                        if (preferences.id == task.creator.id) {
                                            updateEditNameDialogState(
                                                EditNameDialogState(
                                                    task.description,
                                                    task.taskId
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.TASK_DESCRIPTION)
                                        } else {
                                            MiscUtils.toast(context, "Only task creator can edit description.")
                                        }
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    isOneLine = false,
                                    isJustify = true
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomButton(
                                text = "DASHBOARD",
                                onClick = { navController.navigate(Routes.DASHBOARD) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            if (preferences.id == task.creator.id) {
                                CustomButton(
                                    text = "DELETE TASK",
                                    onClick = {
                                        updateConfirmDialogState(
                                            aboutTaskState.confirmDialogState.copy(
                                                id = task.taskId,
                                                placeholder = "Are you sure you want to delete this task?",
                                                onYesClick = { id ->
                                                    deleteTask(id) {
                                                        navController.navigate(Routes.DASHBOARD)
                                                    }
                                                },
                                                onCancelClick = removeDialog
                                            )
                                        )
                                        openDialog(AboutTaskDialogs.CONFIRM)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    enabled = task.deleteButtonEnabled
                                )
                            }
                        }
                        CustomTab(
                            tabIndex = task.tabIndex,
                            tabs = listOf("Comment", "Checklist", "Subtask", "Attachment"),
                            onTabClick = { updateTask(task.copy(tabIndex = it)) }
                        )
                        when (task.tabIndex) {
                            0 -> {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AddCommentMenu(
                                        commentInput = aboutTaskState.addCommentState.description,
                                        buttonEnabled = aboutTaskState.addCommentState.buttonEnabled,
                                        onCommentInputChange = {
                                            updateAddCommentState(
                                                aboutTaskState.addCommentState.copy(
                                                    description = it
                                                )
                                            )
                                        },
                                        sendComment = sendComment,
                                        commentReply = task.comments
                                            .filter { comment -> aboutTaskState.addCommentState.reply.any { it == comment.commentId } }
                                            .map { it.description },
                                        mentions = aboutTaskState.addCommentState.mentions,
                                        onAddMentionsMenu = {
                                            updateSearchDialogState(
                                                SearchUserDialogState(
                                                    aboutTaskState.addCommentState.mentions,
                                                    0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.COMMENT_MENTIONS)
                                        },
                                        onRemoveReply = { updateAddCommentState(aboutTaskState.addCommentState.copy(reply = emptyList())) }
                                    )
                                    if (task.comments.isNotEmpty()) {
                                        OneLineText(
                                            text = "COMMENTS",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        task.comments.forEach { comment ->
                                            CommentBox(
                                                currentUserId = preferences.id,
                                                comment = comment,
                                                onUserClick = navigateToProfile,
                                                onReplyClick = {
                                                    updateAddCommentState(
                                                        aboutTaskState.addCommentState.copy(
                                                            mentions = listOf(it.user),
                                                            reply = listOf(it.commentId)
                                                        )
                                                    )
                                                },
                                                onLikeClick = { commentId ->
                                                    likeComment(preferences.id, commentId)
                                                },
                                                onDeleteClick = { commentId ->
                                                    updateConfirmDialogState(
                                                        aboutTaskState.confirmDialogState.copy(
                                                            id = commentId,
                                                            placeholder = "Are you sure you want to delete this comment?",
                                                            onYesClick = deleteComment,
                                                            onCancelClick = removeDialog
                                                        )
                                                    )
                                                    openDialog(AboutTaskDialogs.CONFIRM)
                                                },
                                                commentReply = task.comments
                                                    .filter { com -> comment.replyId.any { com.commentId == it } }
                                                    .map { it.description }
                                            )
                                        }
                                    }
                                }
                            }
                            1 -> {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AddChecklistMenu(
                                        checklistInput = aboutTaskState.addChecklistState.description,
                                        buttonEnabled = aboutTaskState.addChecklistState.buttonEnabled,
                                        assigneesAdded = aboutTaskState.addChecklistState.assignees,
                                        updateChecklistInput = {
                                            updateAddChecklistState(
                                                aboutTaskState.addChecklistState.copy(
                                                    description = it
                                                )
                                            )
                                        },
                                        onUserClick = {
                                            updateViewAssigneeDialogState(aboutTaskState.addChecklistState.assignees)
                                            openDialog(AboutTaskDialogs.VIEW)
                                        },
                                        onPersonAddClick = {
                                            updateSearchDialogState(
                                                SearchUserDialogState(
                                                    aboutTaskState.addChecklistState.assignees,
                                                    0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.CHECKLIST_ASSIGNEE)
                                        },
                                        addChecklist = addChecklist
                                    )
                                    if (task.checklists.isNotEmpty()) {
                                        OneLineText(
                                            text = "CHECKLISTS",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        task.checklists.forEach { checklist ->
                                            ChecklistBox(
                                                currentUserId = preferences.id,
                                                checklist = checklist,
                                                onChecklistChange = { id, check ->
                                                    if (checklist.assignees.any { it.id == preferences.id }) {
                                                        toggleChecklist(id, check)
                                                    } else {
                                                        MiscUtils.toast(context, "Only checklist assignees can edit the checklist.")
                                                    }
                                                },
                                                openViewDialog = {
                                                    updateViewAssigneeDialogState(checklist.assignees)
                                                    openDialog(AboutTaskDialogs.VIEW)
                                                },
                                                navigateToProfile = navigateToProfile,
                                                onDeleteClick = { checklistId ->
                                                    updateConfirmDialogState(
                                                        aboutTaskState.confirmDialogState.copy(
                                                            id = checklistId,
                                                            placeholder = "Are you sure you want to delete this checklist?",
                                                            onYesClick = deleteChecklist,
                                                            onCancelClick = removeDialog
                                                        )
                                                    )
                                                    openDialog(AboutTaskDialogs.CONFIRM)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            2 -> {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AddSubtaskMenu(
                                        windowInfo = windowInfo,
                                        addSubtaskState = aboutTaskState.addSubtaskState,
                                        onDescriptionChange = { updateAddSubtaskState(aboutTaskState.addSubtaskState.copy(description = it)) },
                                        onOpenDueDialog = {
                                            updateDateDialogState(
                                                DateDialogState(
                                                    selected = aboutTaskState.addSubtaskState.due,
                                                    datePickerEnabled = false,
                                                    timePickerEnabled = false,
                                                    taskId = 0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.SUB_ADD_DUE)
                                        },
                                        onOpenPriorityDialog = {
                                            updateRadioDialogState(
                                                RadioButtonDialogState(
                                                    listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                                                    aboutTaskState.addSubtaskState.priority,
                                                    0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.SUB_ADD_PRIORITY)
                                        },
                                        onOpenTypeDialog = {
                                            updateRadioDialogState(
                                                RadioButtonDialogState(
                                                    listOf("TASK", "MILESTONE"),
                                                    aboutTaskState.addSubtaskState.type,
                                                    0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.SUB_ADD_TYPE)
                                        },
                                        onOpenAssigneeDialog = {
                                            updateSearchDialogState(
                                                SearchUserDialogState(
                                                    aboutTaskState.addSubtaskState.assignees,
                                                    0
                                                )
                                            )
                                            openDialog(AboutTaskDialogs.SUB_ADD_ASSIGNEE)
                                        },
                                        onUserClick = {
                                            updateViewAssigneeDialogState(aboutTaskState.addSubtaskState.assignees)
                                            openDialog(AboutTaskDialogs.VIEW)
                                        },
                                        addSubtask = addSubtask
                                    )
                                    if (task.subtasks.isNotEmpty()) {
                                        OneLineText(
                                            text = "SUBTASKS",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        task.subtasks.forEach { subtask ->
                                            SubtaskBox(
                                                currentUserId = preferences.id,
                                                windowInfo = windowInfo,
                                                task = subtask,
                                                navigateToProfile = navigateToProfile,
                                                openViewDialog = {
                                                    updateViewAssigneeDialogState(subtask.assignees)
                                                    openDialog(AboutTaskDialogs.VIEW)
                                                },
                                                onDescriptionClick = {
                                                    if (subtask.creator.id == preferences.id) {
                                                        updateEditNameDialogState(
                                                            EditNameDialogState(
                                                                subtask.description,
                                                                subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_DESCRIPTION)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask creator can edit description.")
                                                    }
                                                },
                                                onStatusClick = {
                                                    if (subtask.assignees.any { it.id == preferences.id }) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("OPEN", "IN PROGRESS", "ON HOLD", "COMPLETE"),
                                                                subtask.status,
                                                                subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_STATUS)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask assignees can edit status.")
                                                    }
                                                },
                                                onAssigneeClick = {
                                                    if (subtask.creator.id == preferences.id) {
                                                        updateSearchDialogState(
                                                            SearchUserDialogState(
                                                                subtask.assignees,
                                                                subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_ASSIGNEE)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask creator can edit assignees.")
                                                    }
                                                },
                                                onPriorityClick = {
                                                    if (subtask.creator.id == preferences.id) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("LOW", "NORMAL", "HIGH", "URGENT"),
                                                                subtask.priority,
                                                                subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_PRIORITY)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask creator can edit priority.")
                                                    }
                                                },
                                                onDueClick = {
                                                    if (subtask.creator.id == preferences.id) {
                                                        updateDateDialogState(
                                                            DateDialogState(
                                                                selected = subtask.due,
                                                                datePickerEnabled = false,
                                                                timePickerEnabled = false,
                                                                taskId = subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_DUE)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask creator can edit due date.")
                                                    }
                                                },
                                                onTypeClick = {
                                                    if (subtask.creator.id == preferences.id) {
                                                        updateRadioDialogState(
                                                            RadioButtonDialogState(
                                                                listOf("TASK", "MILESTONE"),
                                                                subtask.type,
                                                                subtask.subtaskId
                                                            )
                                                        )
                                                        openDialog(AboutTaskDialogs.SUB_EDIT_TYPE)
                                                    } else {
                                                        MiscUtils.toast(context, "Only subtask creator can edit type.")
                                                    }
                                                },
                                                onDeleteClick = { subtaskId ->
                                                    updateConfirmDialogState(
                                                        aboutTaskState.confirmDialogState.copy(
                                                            id = subtaskId,
                                                            placeholder = "Are you sure you want to delete this subtask?",
                                                            onYesClick = deleteSubtask,
                                                            onCancelClick = removeDialog
                                                        )
                                                    )
                                                    openDialog(AboutTaskDialogs.CONFIRM)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            3 -> {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AddAttachmentMenu(
                                        attachmentState = aboutTaskState.addAttachmentState,
                                        onFilePicked = {
                                            if (it != null) {
                                                updateAddAttachmentState(
                                                    aboutTaskState.addAttachmentState.copy(fileUri = it)
                                                )
                                            }
                                        },
                                        onFileUpload = uploadAttachment,
                                        context = context
                                    )
                                    if (task.attachments.isNotEmpty()) {
                                        OneLineText(
                                            text = "ATTACHMENTS",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        task.attachments.forEach { attachment ->
                                            AttachmentBox(
                                                currentUserId = preferences.id,
                                                attachment = attachment,
                                                onUserClick = navigateToProfile,
                                                onDownload = {
                                                    downloadAttachment(attachment.fileName, attachment.attachmentPath)
                                                },
                                                onDeleteClick = { attachmentId ->
                                                    updateConfirmDialogState(
                                                        aboutTaskState.confirmDialogState.copy(
                                                            id = attachmentId,
                                                            placeholder = "Are you sure you want to delete this attachment?",
                                                            onYesClick = deleteAttachment,
                                                            onCancelClick = removeDialog
                                                        )
                                                    )
                                                    openDialog(AboutTaskDialogs.CONFIRM)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                when (aboutTaskDialogs) {
                    AboutTaskDialogs.NONE -> {  }
                    AboutTaskDialogs.TASK_NAME -> {
                        EditNameDialog(
                            text = "Name",
                            editNameDialogState = dialogsState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { taskId, name ->
                                changeName(taskId, name) {
                                    updateTask(task.copy(title = name))
                                }
                            },
                            onTextChange = editTextDialog
                        )
                    }
                    AboutTaskDialogs.TASK_ASSIGNEE -> {
                        SearchUserDialog(
                            text = "Assignees",
                            searchUserDialogState = dialogsState.searchUserDialogState,
                            searchState = dialogsState.searchState,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog,
                            onApplyClick = { taskId, assignees ->
                                changeAssignee(taskId, assignees) {
                                    updateTask(task.copy(assignees = assignees))
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
                            onUserAdd = onUserAdd,
                            onUserRemove = onUserRemove
                        )
                    }
                    AboutTaskDialogs.TASK_DUE -> {
                        DateTimePickerDialog(
                            text = "Due Date",
                            dateDialogState = dialogsState.dateDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { taskId, due ->
                                changeDue(taskId, due) {
                                    updateTask(task.copy(due = due))
                                }
                            },
                            datePicker = updateDatePicker,
                            timePicker = updateTimePicker,
                            selected = selectDateTime
                        )
                    }
                    AboutTaskDialogs.TASK_PRIORITY -> {
                        RadioButtonDialog(
                            text = "Priority",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = { taskId, priority ->
                                changePriority(taskId, priority) {
                                    updateTask(task.copy(priority = priority))
                                }
                            },
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.TASK_STATUS -> {
                        RadioButtonDialog(
                            text = "Status",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = { taskId, status ->
                                changeStatus(taskId, status) {
                                    updateTask(task.copy(status = status))
                                }
                            },
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.TASK_TYPE -> {
                        RadioButtonDialog(
                            text = "Type",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = { taskId, type ->
                                changeType(taskId, type) {
                                    updateTask(task.copy(type = type))
                                }
                            },
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.TASK_DESCRIPTION -> {
                        EditNameDialog(
                            text = "Description",
                            editNameDialogState = dialogsState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { taskId, description ->
                                changeDescription(taskId, description) {
                                    updateTask(task.copy(description = description))
                                }
                            },
                            onTextChange = editTextDialog
                        )
                    }
                    AboutTaskDialogs.COMMENT_MENTIONS -> {
                        EditAssigneeDialog(
                            taskAssignees = task.assignees,
                            searchUserDialogState = dialogsState.searchUserDialogState,
                            onUserAdd = onUserAdd,
                            onUserRemove = onUserRemove,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, mentions -> updateAddCommentState(aboutTaskState.addCommentState.copy(mentions = mentions)) }
                        )
                    }
                    AboutTaskDialogs.CHECKLIST_ASSIGNEE -> {
                        EditAssigneeDialog(
                            taskAssignees = task.assignees,
                            searchUserDialogState = dialogsState.searchUserDialogState,
                            onUserAdd = onUserAdd,
                            onUserRemove = onUserRemove,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, assignee -> updateAddChecklistState(aboutTaskState.addChecklistState.copy(assignees = assignee)) }
                        )
                    }
                    AboutTaskDialogs.SUB_ADD_DUE -> {
                        DateTimePickerDialog(
                            text = "Due Date",
                            dateDialogState = dialogsState.dateDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, due -> updateAddSubtaskState(aboutTaskState.addSubtaskState.copy(due = due)) },
                            datePicker = updateDatePicker,
                            timePicker = updateTimePicker,
                            selected = selectDateTime
                        )
                    }
                    AboutTaskDialogs.SUB_ADD_PRIORITY -> {
                        RadioButtonDialog(
                            text = "Priority",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = { _, priority -> updateAddSubtaskState(aboutTaskState.addSubtaskState.copy(priority = priority)) },
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.SUB_ADD_TYPE -> {
                        RadioButtonDialog(
                            text = "Type",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = { _, type -> updateAddSubtaskState(aboutTaskState.addSubtaskState.copy(type = type)) },
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.SUB_ADD_ASSIGNEE -> {
                        EditAssigneeDialog(
                            taskAssignees = task.assignees,
                            searchUserDialogState = dialogsState.searchUserDialogState,
                            onUserAdd = onUserAdd,
                            onUserRemove = onUserRemove,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, assignee -> updateAddSubtaskState(aboutTaskState.addSubtaskState.copy(assignees = assignee)) }
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_DESCRIPTION -> {
                        EditNameDialog(
                            text = "Description",
                            editNameDialogState = dialogsState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = changeSubtaskDescription,
                            onTextChange = editTextDialog
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_STATUS -> {
                        RadioButtonDialog(
                            text = "Status",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = changeSubtaskStatus,
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_ASSIGNEE -> {
                        EditAssigneeDialog(
                            taskAssignees = task.assignees,
                            searchUserDialogState = dialogsState.searchUserDialogState,
                            onUserAdd = onUserAdd,
                            onUserRemove = onUserRemove,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog,
                            onApplyClick = editSubtaskAssignees
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_PRIORITY -> {
                        RadioButtonDialog(
                            text = "Priority",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = changeSubtaskPriority,
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_DUE -> {
                        DateTimePickerDialog(
                            text = "Due Date",
                            dateDialogState = dialogsState.dateDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = changeSubtaskDueDate,
                            datePicker = updateDatePicker,
                            timePicker = updateTimePicker,
                            selected = selectDateTime
                        )
                    }
                    AboutTaskDialogs.SUB_EDIT_TYPE -> {
                        RadioButtonDialog(
                            text = "Type",
                            radioButtonDialogState = dialogsState.radioButtonDialogState,
                            onApplyClick = changeSubtaskType,
                            onDismissRequest = removeDialog,
                            onRadioSelect = radioSelect
                        )
                    }
                    AboutTaskDialogs.VIEW -> {
                        ViewAssigneeDialog(
                            assignees = dialogsState.viewAssigneeDialogState,
                            onUserClick = navigateToProfile,
                            onDismissRequest = removeDialog
                        )
                    }
                    AboutTaskDialogs.CONFIRM -> {
                        ConfirmDialog(
                            id = aboutTaskState.confirmDialogState.id,
                            placeholder = aboutTaskState.confirmDialogState.placeholder,
                            onYesClick = aboutTaskState.confirmDialogState.onYesClick,
                            onCancelClick = aboutTaskState.confirmDialogState.onCancelClick
                        )
                    }
                }
            }
        }
    }
}