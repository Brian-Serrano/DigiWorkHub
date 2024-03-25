package com.serrano.dictproject.utils

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.time.LocalDateTime

annotation class Unauthorized

sealed class ProcessState {
    data object Loading : ProcessState()
    data object Success : ProcessState()
    data class Error(val message: String) : ProcessState()
}

data class SignupState(
    val tab: Int = 0,
    val signupName: String = "",
    val signupEmail: String = "",
    val signupPassword: String = "",
    val signupConfirmPassword: String = "",
    val loginEmail: String = "",
    val loginPassword: String = "",
    val errorMessage: String = "",
    val loginPasswordVisibility: Boolean = false,
    val signupPasswordVisibility: Boolean = false,
    val signupConfirmPasswordVisibility: Boolean = false,
    val signupButtonEnabled: Boolean = true,
    val loginButtonEnabled: Boolean = true
)

data class DashboardState(
    val groupDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val filterDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val isFilterDropDown: DropDownState = DropDownState(listOf("IS", "IS NOT"), "IS", false),
    val optionsFilterDropDown: DropDownMultiselect = DropDownMultiselect(),
    val sortDropDown: DropDownState = DropDownState(listOf("NAME", "ASSIGNEE", "DUE", "PRIORITY", "STATUS", "TYPE"), "NAME", false),
    val optionsFilterDropDownValues: List<Map<String, List<String>>> = listOf(emptyMap(), emptyMap()),
    val isCollapsed: List<List<Boolean>> = listOf(emptyList(), emptyList())
)

data class AboutTaskState(
    val addCommentState: AddCommentState = AddCommentState(),
    val addChecklistState: AddChecklistState = AddChecklistState(),
    val addSubtaskState: AddSubtaskState = AddSubtaskState(),
    val addAttachmentState: AddAttachmentState = AddAttachmentState(),
    val tabIndex: Int = 0
)

data class DialogsState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val searchUserDialogState: SearchUserDialogState = SearchUserDialogState(),
    val dateDialogState: DateDialogState = DateDialogState(),
    val radioButtonDialogState: RadioButtonDialogState = RadioButtonDialogState(),
    val searchState: SearchState = SearchState(),
    val viewAssigneeDialogState: List<User> = emptyList()
)

data class AddTaskState(
    val name: String = "",
    val description: String = "",
    val priority: String = "LOW",
    val due: LocalDateTime = LocalDateTime.now(),
    val assignees: List<User> = emptyList(),
    val type: String = "TASK",
    val errorMessage: String = "",
    val buttonEnabled: Boolean = true
)

data class DropDownState(
    val options: List<String> = emptyList(),
    val selected: String = "",
    val expanded: Boolean = false
)

data class DropDownMultiselect(
    val options: List<String> = emptyList(),
    val selected: List<String> = emptyList(),
    val expanded: Boolean = false
)

data class DrawerData(
    val icon: ImageVector,
    val name: String
)

data class Calendar(
    val date: LocalDate,
    val calendarTasks: List<CalendarTask>
)

data class CalendarTask(
    val taskId: Int,
    val name: String
)

data class SearchState(
    val searchQuery: String = "",
    val isActive: Boolean = false,
    val results: List<User> = emptyList()
)

data class RadioButtonDialogState(
    val options: List<String> = emptyList(),
    val selected: String = "",
    val taskId: Int = 0
)

data class DateDialogState(
    val selected: LocalDateTime = LocalDateTime.now(),
    val datePickerEnabled: Boolean = false,
    val timePickerEnabled: Boolean = false,
    val taskId: Int = 0
)

data class SearchUserDialogState(
    val users: List<User> = emptyList(),
    val taskId: Int = 0
)

data class EditNameDialogState(
    val name: String = "",
    val taskId: Int = 0
)

data class SharedViewModelState(
    val dashboardBottomBarIdx: Int = 0,
    val messageBottomBarIdx: Int = 0,
    val dashboardViewIdx: Int = 0,
    val messageViewIdx: Int = 0,
    val calendarTabIdx: Int = 0
)

data class AddSubtaskState(
    val description: String = "",
    val priority: String = "LOW",
    val due: LocalDateTime = LocalDateTime.now(),
    val assignees: List<User> = emptyList(),
    val type: String = "TASK",
    val buttonEnabled: Boolean = true
)

data class AddCommentState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val likeIconsEnabled: List<Boolean> = emptyList()
)

data class AddChecklistState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val assignees: List<User> = emptyList(),
    val buttonsEnabled: List<Boolean> = emptyList()
)

data class AddAttachmentState(
    val fileUri: Uri? = null,
    val buttonEnabled: Boolean = true
)

data class SendMessageState(
    val receiver: User? = null,
    val title: String = "",
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val errorMessage: String = "",
    val searchState: SearchState = SearchState()
)

data class ProfileState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val image: ImageBitmap? = null
)