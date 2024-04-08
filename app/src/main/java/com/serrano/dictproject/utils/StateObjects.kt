package com.serrano.dictproject.utils

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
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
    val signupNameError: String = "",
    val signupEmail: String = "",
    val signupEmailError: String = "",
    val signupPassword: String = "",
    val signupPasswordError: String = "",
    val signupConfirmPassword: String = "",
    val signupConfirmPasswordError: String = "",
    val loginEmail: String = "",
    val loginEmailError: String = "",
    val loginPassword: String = "",
    val loginPasswordError: String = "",
    val errorMessage: String = "",
    val loginPasswordVisibility: Boolean = false,
    val signupPasswordVisibility: Boolean = false,
    val signupConfirmPasswordVisibility: Boolean = false,
    val signupButtonEnabled: Boolean = true,
    val loginButtonEnabled: Boolean = true,
    val forgotCode: String = "",
    val forgotCodeReceived: String = "",
    val forgotNewPassword: String = "",
    val forgotConfirmPassword: String = "",
    val forgotNewPasswordVisibility: Boolean = false,
    val forgotConfirmPasswordVisibility: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

data class DashboardState(
    val groupDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val filterDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val isFilterDropDown: DropDownState = DropDownState(listOf("IS", "IS NOT"), "IS", false),
    val optionsFilterDropDown: DropDownMultiselect = DropDownMultiselect(),
    val sortDropDown: DropDownState = DropDownState(listOf("NAME", "ASSIGNEE", "DUE", "PRIORITY", "STATUS", "TYPE"), "NAME", false),
    val optionsFilterDropDownValues: List<Map<String, List<String>>> = listOf(emptyMap(), emptyMap()),
    val isTaskRefreshing: Boolean = false,
    val isCreatedTaskRefreshing: Boolean = false
)

data class LabelAndCollapsible(
    val label: String,
    val collapsible: Boolean
)

data class AboutTaskState(
    val addCommentState: AddCommentState = AddCommentState(),
    val addChecklistState: AddChecklistState = AddChecklistState(),
    val addSubtaskState: AddSubtaskState = AddSubtaskState(),
    val addAttachmentState: AddAttachmentState = AddAttachmentState(),
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

data class ConfirmDialogState(
    val id: Int = 0,
    val placeholder: String = "",
    val onYesClick: (Int) -> Unit = {},
    val onCancelClick: () -> Unit = {}
)

data class DialogsState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val searchUserDialogState: SearchUserDialogState = SearchUserDialogState(),
    val dateDialogState: DateDialogState = DateDialogState(),
    val radioButtonDialogState: RadioButtonDialogState = RadioButtonDialogState(),
    val searchState: SearchState = SearchState(),
    val viewAssigneeDialogState: List<UserDTO> = emptyList()
)

data class AddTaskState(
    val name: String = "",
    val description: String = "",
    val priority: String = "LOW",
    val due: LocalDateTime = LocalDateTime.now(),
    val assignees: List<UserDTO> = emptyList(),
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

data class SearchState(
    val searchQuery: String = "",
    val isActive: Boolean = false,
    val results: List<UserDTO> = emptyList()
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
    val users: List<UserDTO> = emptyList(),
    val taskId: Int = 0
)

data class EditNameDialogState(
    val name: String = "",
    val taskId: Int = 0
)

data class PasswordDialogState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordVisibility: Boolean = false,
    val newPasswordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false
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
    val assignees: List<UserDTO> = emptyList(),
    val type: String = "TASK",
    val buttonEnabled: Boolean = true
)

data class AddCommentState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val mentions: List<UserDTO> = emptyList(),
    val reply: List<Int> = emptyList()
)

data class AddChecklistState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val assignees: List<UserDTO> = emptyList()
)

data class AddAttachmentState(
    val fileUri: Uri? = null,
    val buttonEnabled: Boolean = true
)

data class SendMessageState(
    val receiver: UserDTO? = null,
    val title: String = "",
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val errorMessage: String = "",
    val searchState: SearchState = SearchState(),
    val fileUris: List<Uri> = emptyList(),
    val dialogUri: Uri? = null
)

data class ProfileState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val image: ImageBitmap? = null,
    val isRefreshing: Boolean = false
)

data class SettingsState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState(),
    val passwordDialogState: PasswordDialogState = PasswordDialogState(),
    val image: ImageBitmap? = null,
    val isRefreshing: Boolean = false,
    val deleteButtonEnabled: Boolean = true
)

data class AboutMessageState(
    val fileUris: List<Uri> = emptyList(),
    val dialogUri: Uri? = null,
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val isRefreshing: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

data class InboxState(
    val isSentRefreshing: Boolean = false,
    val isReceivedRefreshing: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

data class TaskState(
    val taskId: Int = 0,
    val title: String = dummyTitle,
    val description: String = loremIpsum,
    val due: LocalDateTime = LocalDateTime.now(),
    val priority: String = "LOW",
    val status: String = "ON HOLD",
    val type: String = "TASK",
    val sentDate: LocalDateTime = LocalDateTime.now(),
    val assignees: List<UserDTO> = listOf(userDTO, userDTO, userDTO),
    val creator: UserDTO = userDTO,
    val comments: List<CommentState> = emptyList(),
    val subtasks: List<SubtaskState> = emptyList(),
    val checklists: List<ChecklistState> = emptyList(),
    val attachments: List<AttachmentState> = emptyList(),
    val tabIndex: Int = 0,
    val isRefreshing: Boolean = false,
    val deleteButtonEnabled: Boolean = true
)

data class CommentState(
    val commentId: Int,
    val taskId: Int,
    val description: String,
    val replyId: List<Int>,
    val mentionsName: List<String>,
    val user: UserDTO,
    val sentDate: LocalDateTime,
    val likesId: List<Int>,
    val likeIconEnabled: Boolean,
    val deleteIconEnabled: Boolean
)

data class SubtaskState(
    val subtaskId: Int,
    val taskId: Int,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val assignees: List<UserDTO>,
    val creator: UserDTO,
    val deleteIconEnabled: Boolean
)

data class ChecklistState(
    val checklistId: Int,
    val taskId: Int,
    val user: UserDTO,
    val description: String,
    val isChecked: Boolean,
    val assignees: List<UserDTO>,
    val sentDate: LocalDateTime,
    val deleteIconEnabled: Boolean,
    val checkButtonEnabled: Boolean
)

data class AttachmentState(
    val attachmentId: Int,
    val taskId: Int,
    val user: UserDTO,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime,
    val deleteIconEnabled: Boolean
)

data class MessageState(
    val messageId: Int = 1,
    val title: String = dummyTitle,
    val description: String = loremIpsum,
    val sentDate: LocalDateTime = LocalDateTime.now(),
    val sender: UserDTO = userDTO,
    val receiver: UserDTO = userDTO,
    val attachmentPaths: List<String> = emptyList(),
    val fileNames: List<String> = emptyList(),
    val replies: List<MessageReplyState> = emptyList(),
    val deleteButtonEnabled: Boolean = true,
    val deleteForUserButtonEnabled: Boolean = true
)

data class MessageReplyState(
    val messageReplyId: Int,
    val messageId: Int,
    val sentDate: LocalDateTime,
    val description: String,
    val fromId: Int,
    val attachmentPaths: List<String>,
    val fileNames: List<String>,
    val deleteIconEnabled: Boolean
)

data class MessagePartState(
    val messageId: Int,
    val sentDate: LocalDateTime,
    val other: UserDTO,
    val title: String,
    val deleteButtonEnabled: Boolean
)