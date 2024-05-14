package com.serrano.dictproject.utils

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import java.time.LocalDateTime

/**
 * An annotation for api/backend call that do not need authorization (do not need to pass auth header)
 */
annotation class Unauthorized

/**
 * User Interface Process.
 */
sealed class ProcessState {
    /**
     * This is the starting process state of user interface. This process is used when the page is calling api or fetching data in internal storage (room) and no available data can be shown in user interface.
     */
    data object Loading : ProcessState()

    /**
     * This process is used when there are data available to be shown in user interface (successful api call/fetch local database).
     */
    data object Success : ProcessState()

    /**
     * This process is used when the api call or fetching local database fail (for some reason like no internet connection, internal server error, etc.) and no data can be shown in user interface.
     *
     * @param[message] The information about the error
     */
    data class Error(val message: String) : ProcessState()
}

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the signup/login page.
 *
 * @param[tab] Current tab in signup/login page. Signup and login are not separate pages/activity. When tab is 0 the user is in the signup menu. When tab is 1 the user is in the login menu.
 * @param[signupName] The text in username input field in signup menu.
 * @param[signupNameError] A label below signup username input field that will show when the username is invalid.
 * @param[signupEmail] The text in email input field in signup menu.
 * @param[signupEmailError] A label below signup email input field that will show when the email is invalid.
 * @param[signupPassword] The text in password input field in signup menu.
 * @param[signupPasswordError] A label below signup password input field that will show when the password is invalid or do not match with confirm password.
 * @param[signupConfirmPassword] The text in confirm password input field in signup menu.
 * @param[signupConfirmPasswordError] A label below signup confirm password input field that will show when the confirm password is invalid or do not match with password.
 * @param[loginEmail] The text in email input field in login menu.
 * @param[loginEmailError] A label below login email input field that will show when the email is invalid.
 * @param[loginPassword] The text in password input field in login menu.
 * @param[loginPasswordError] A label below login password input field that will show when the password is invalid.
 * @param[errorMessage] An error shown when the signup-login fails with message.
 * @param[loginPasswordVisibility] The login password input field should show the text (true) or show dots (false).
 * @param[signupPasswordVisibility] The signup password input field should show the text (true) or show dots (false).
 * @param[signupConfirmPasswordVisibility] The signup confirm password input field should show the text (true) or show dots (false).
 * @param[signupButtonEnabled] The status of signup button. When the user clicks the signup button it will call the server for signup request and might delay. This will prevent the user from clicking the button again when it is in calling server state by disabling the button temporarily. Clicking the button more than once and calling server more than once with same request will cause data inconsistencies.
 * @param[loginButtonEnabled] The status of login button. When the user clicks the login button it will call the server for login request and might delay. This will prevent the user from clicking the button again when it is in calling server state by disabling the button temporarily. Clicking the button more than once and calling server more than once with same request will cause data inconsistencies.
 * @param[forgotCode] The code input field in forgot password dialog.
 * @param[forgotNewPassword] The new password input field in forgot password dialog.
 * @param[forgotConfirmPassword] The confirm password input field in forgot password dialog.
 * @param[forgotNewPasswordVisibility] The forgot new password input field should show the text (true) or show dots (false).
 * @param[forgotConfirmPasswordVisibility] The forgot confirm password input field should show the text (true) or show dots (false).
 * @param[confirmDialogState] The confirm dialog state. Used in login for confirm sending code to mail.
 */
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
    val forgotNewPassword: String = "",
    val forgotConfirmPassword: String = "",
    val forgotNewPasswordVisibility: Boolean = false,
    val forgotConfirmPasswordVisibility: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the dashboard page.
 *
 * @param[groupDropDown] group drop down
 * @param[filterDropDown] filter drop down, will open dialog if there are possible value in the option base on the results
 * @param[isFilterDropDown] drop down that have two options indicating if it filter the result by the selected options or filter the result by everything except the selected options.
 * @param[optionsFilterDropDown] drop down that have values base on the result and base on the selected filter drop down value. Example if the user selected priority on filter drop down and all the results do not include URGENT the values of this will be LOW, NORMAL, HIGH.
 * @param[sortDropDown] sort drop down
 * @param[isTaskRefreshing] state for the swipe refresh in assigned task tab
 * @param[isCreatedTaskRefreshing] state for the swipe refresh in created task tab
 */
data class DashboardState(
    val groupDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val filterDropDown: DropDownState = DropDownState(listOf("NONE", "STATUS", "PRIORITY", "DUE", "TYPE", "CREATOR"), "NONE", false),
    val isFilterDropDown: DropDownState = DropDownState(listOf("IS", "IS NOT"), "IS", false),
    val optionsFilterDropDown: DropDownMultiselect = DropDownMultiselect(),
    val sortDropDown: DropDownState = DropDownState(listOf("NAME", "ASSIGNEE", "DUE", "PRIORITY", "STATUS", "TYPE"), "NAME", false),
    val isTaskRefreshing: Boolean = false,
    val isCreatedTaskRefreshing: Boolean = false
)

/**
 * Used in the header of list/grid when the task/data is grouped
 *
 * @param[label] The common value for the grouped task/data
 * @param[collapsible] The collapse state of the task/data group. If false the data/user interface on that group will not be shown.
 */
data class LabelAndCollapsible(
    val label: String,
    val collapsible: Boolean
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the about task page.
 *
 * @param[addCommentState] The states in the comment menu
 * @param[addChecklistState] The states in the checklist menu
 * @param[addSubtaskState] The states in the subtask menu
 * @param[addAttachmentState] The states in the attachment menu
 * @param[confirmDialogState] The states for the confirm dialog
 */
data class AboutTaskState(
    val addCommentState: AddCommentState = AddCommentState(),
    val addChecklistState: AddChecklistState = AddChecklistState(),
    val addSubtaskState: AddSubtaskState = AddSubtaskState(),
    val addAttachmentState: AddAttachmentState = AddAttachmentState(),
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

/**
 * The states for the confirm dialog
 *
 * @param[id] Any id, it will be passed on onYesClick as argument
 * @param[placeholder] A message/text shown in confirm dialog
 * @param[onYesClick] Do the action the user entered. The id on argument can be used base on the action entered. Example confirm deletion of task, then pass the task id as its argument, then after the user click yes the id passed will be use to know what task should be deleted.
 * @param[onCancelClick] Close the dialog
 */
data class ConfirmDialogState(
    val id: Int = 0,
    val placeholder: String = "",
    val onYesClick: (Int) -> Unit = {},
    val onCancelClick: () -> Unit = {}
)

/**
 * States of multiple dialogs. Used in the dashboard, add task and about task page.
 *
 * @param[editNameDialogState] State for the dialogs that edit texts, example task title or description.
 * @param[searchUserDialogState] State for the dialogs that add/remove user, commonly used when adding/editing assignees.
 * @param[dateDialogState] State for the dialogs that pick date and time, commonly used when setting due date.
 * @param[radioButtonDialogState] State for the dialogs that selecting one from multiple options, example priority choose one from LOW, NORMAL, HIGH, URGENT.
 * @param[searchState] State for searching user/assignees.
 * @param[viewAssigneeDialogState] State for the dialogs that shows the assignees/users. No editing in this dialog, but if the user choose different assignees the contents of dialog should also change.
 */
data class DialogsState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val searchUserDialogState: SearchUserDialogState = SearchUserDialogState(),
    val dateDialogState: DateDialogState = DateDialogState(),
    val radioButtonDialogState: RadioButtonDialogState = RadioButtonDialogState(),
    val searchState: SearchState = SearchState(),
    val viewAssigneeDialogState: List<UserDTO> = emptyList()
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the add task page.
 *
 * @param[name] Entered name on the field
 * @param[description] Entered description on the field
 * @param[priority] Entered priority on the field
 * @param[due] Entered due date on the field
 * @param[assignees] Selected assignees
 * @param[type] Entered type on the field
 * @param[errorMessage] A message shown when adding task fails
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 */
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

/**
 * State for drop down components
 *
 * @param[options] options for the drop down
 * @param[selected] selected option
 * @param[expanded] status of drop down, if true, show box with the options
 */
data class DropDownState(
    val options: List<String> = emptyList(),
    val selected: String = "",
    val expanded: Boolean = false
)

/**
 * State for drop down components that can select multiple options. Used in filtering tasks in dashboard page.
 *
 * @param[options] options for the drop down
 * @param[selected] selected options
 * @param[expanded] status of drop down, if true, show box with the options
 */
data class DropDownMultiselect(
    val options: List<String> = emptyList(),
    val selected: List<String> = emptyList(),
    val expanded: Boolean = false
)

/**
 * State for searching users
 *
 * @param[searchQuery] The text of search bar
 * @param[isActive] Whether this search bar active
 * @param[results] The result data/user filtered by the search query
 */
data class SearchState(
    val searchQuery: String = "",
    val isActive: Boolean = false,
    val results: List<UserDTO> = emptyList()
)

/**
 * State for the dialogs that selecting one from multiple options, example priority choose one from LOW, NORMAL, HIGH, URGENT.
 *
 * @param[options] Options in the dialog
 * @param[selected] Selected Option
 * @param[taskId] Any id, this can be used when applying changes/clicking apply button. This id will be used base on the action. Example change the priority of a task, the id will be used what task will be changed.
 */
data class RadioButtonDialogState(
    val options: List<String> = emptyList(),
    val selected: String = "",
    val taskId: Int = 0
)

/**
 * State for the dialogs that pick date and time, commonly used when setting due date.
 *
 * @param[selected] Current/Selected date.
 * @param[datePickerEnabled] Visibility of date picker
 * @param[timePickerEnabled] Visibility of time picker
 * @param[taskId] Any id, this can be used when applying changes/clicking apply button. This id will be used base on the action. Example change the due date of a subtask, the id will be used what subtask will be changed.
 */
data class DateDialogState(
    val selected: LocalDateTime = LocalDateTime.now(),
    val datePickerEnabled: Boolean = false,
    val timePickerEnabled: Boolean = false,
    val taskId: Int = 0
)

/**
 * State for the dialogs that add/remove user, commonly used when adding/editing assignees.
 *
 * @param[users] The selected users
 * @param[taskId] Any id, this can be used when applying changes/clicking apply button. This id will be used base on the action. Example change the assignees of a checklist, the id will be used what checklist will be changed.
 */
data class SearchUserDialogState(
    val users: List<UserDTO> = emptyList(),
    val taskId: Int = 0
)

/**
 * State for the dialogs that edit texts, example task title or description.
 *
 * @param[name] The text on the field
 * @param[taskId] Any id, this can be used when applying changes/clicking apply button. This id will be used base on the action. Example change the description of a task, the id will be used what task will be changed.
 */
data class EditNameDialogState(
    val name: String = "",
    val taskId: Int = 0
)

/**
 * Change password state used in change password dialog in settings
 *
 * @param[currentPassword] Entered current password
 * @param[newPassword] Entered new password
 * @param[confirmPassword] Entered confirm password
 * @param[currentPasswordVisibility] Current password input field should show the text (true) or show dots (false).
 * @param[newPasswordVisibility] New password input field should show the text (true) or show dots (false).
 * @param[confirmPasswordVisibility] Confirm password input field should show the text (true) or show dots (false).
 */
data class PasswordDialogState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordVisibility: Boolean = false,
    val newPasswordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false
)

/**
 * State shared to all pages/activity
 *
 * @param[dashboardBottomBarIdx] 0 = Assigned, 1 = Created
 * @param[messageBottomBarIdx] 0 = Received, 1 = Sent
 * @param[dashboardViewIdx] 0 = List, 1 = Grid, 2 = Calendar
 * @param[calendarTabIdx] ..., -1 = Previous Month, 0 = Current Month, 1 = Next Month, ...
 */
data class SharedViewModelState(
    val dashboardBottomBarIdx: Int = 0,
    val messageBottomBarIdx: Int = 0,
    val dashboardViewIdx: Int = 0,
    val calendarTabIdx: Int = 0
)

/**
 * State for adding subtask menu
 *
 * @param[description] Entered description
 * @param[priority] Entered priority
 * @param[due] Entered due date
 * @param[assignees] Selected assignees
 * @param[type] Entered type
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 */
data class AddSubtaskState(
    val description: String = "",
    val priority: String = "LOW",
    val due: LocalDateTime = LocalDateTime.now(),
    val assignees: List<UserDTO> = emptyList(),
    val type: String = "TASK",
    val buttonEnabled: Boolean = true
)

/**
 * State for adding comment menu
 *
 * @param[description] Entered description
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 * @param[mentions] A list of users mentioned
 * @param[reply] Id of comments replying
 */
data class AddCommentState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val mentions: List<UserDTO> = emptyList(),
    val reply: List<Int> = emptyList()
)

/**
 * State for adding checklist menu
 *
 * @param[description] Entered description
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 * @param[assignees] Selected assignees
 */
data class AddChecklistState(
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val assignees: List<UserDTO> = emptyList()
)

/**
 * State for uploading attachment menu
 *
 * @param[fileUri] The file uri. Convert to file object before sending to server.
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 */
data class AddAttachmentState(
    val fileUri: Uri? = null,
    val buttonEnabled: Boolean = true
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the send message page.
 *
 * @param[receiver] Recipient
 * @param[title] Subject/Title entered
 * @param[description] Description entered
 * @param[buttonEnabled] A status for button. Disable button temporarily if it is in calling server state.
 * @param[errorMessage] An error shown when sending the message fails
 * @param[searchState] Used when searching for recipient.
 * @param[fileUris] Attachments with the message.
 * @param[dialogUri] The uri when selecting attachments in upload file dialog
 */
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

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the profile page.
 *
 * @param[editNameDialogState] Used when the user edit his/her name or role
 * @param[image] Used when user upload/change his/her image
 * @param[isRefreshing] Used for swipe refresh state when the user attempts to refresh the page
 */
data class ProfileState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val image: ImageBitmap? = null,
    val isRefreshing: Boolean = false
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the settings page.
 *
 * @param[editNameDialogState] Used when user edit his/her name or role
 * @param[confirmDialogState] Confirms deletion of account
 * @param[passwordDialogState] Used for changing the user password
 * @param[image] User when the user change/upload his/her image
 * @param[isRefreshing] Used for swipe refresh state when the user attempts to refresh the page
 * @param[deleteButtonEnabled] A status for delete button. Disable button temporarily if it is in calling server state.
 */
data class SettingsState(
    val editNameDialogState: EditNameDialogState = EditNameDialogState(),
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState(),
    val passwordDialogState: PasswordDialogState = PasswordDialogState(),
    val image: ImageBitmap? = null,
    val isRefreshing: Boolean = false,
    val deleteButtonEnabled: Boolean = true
)

/**
 * The states (values that can change and need to refresh/recompose user interface with the new value) in the about message page.
 *
 * @param[fileUris] Attachments in sending reply menu
 * @param[dialogUri] Attachment in the upload file dialog
 * @param[description] The reply text
 * @param[buttonEnabled] A status for send reply button. Disable button temporarily if it is in calling server state.
 * @param[isRefreshing] Used for swipe refresh state when the user attempts to refresh the page
 * @param[confirmDialogState] Confirms deletion of replies, messages
 */
data class AboutMessageState(
    val fileUris: List<Uri> = emptyList(),
    val dialogUri: Uri? = null,
    val description: String = "",
    val buttonEnabled: Boolean = true,
    val isRefreshing: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

/**
 *  The states (values that can change and need to refresh/recompose user interface with the new value) in the inbox page.
 *
 *  @param[isSentRefreshing] Used for swipe refresh state in the sent messages tab
 *  @param[isReceivedRefreshing] Used for swipe refresh state in the received messages tab
 *  @param[confirmDialogState] Confirm deletion of message
 */
data class InboxState(
    val isSentRefreshing: Boolean = false,
    val isReceivedRefreshing: Boolean = false,
    val confirmDialogState: ConfirmDialogState = ConfirmDialogState()
)

/**
 * Data in about task page. Received from API as TaskDTO Object transformed to this state
 *
 * @param[taskId] Id of task
 * @param[title] Title of task
 * @param[description] Description of task
 * @param[due] Due of task
 * @param[priority] Priority of task
 * @param[status] Status of task
 * @param[type] Type of task
 * @param[sentDate] Date the task created
 * @param[assignees] Assignees of task
 * @param[creator] Creator of task
 * @param[comments] Comments in the task
 * @param[subtasks] Subtasks in the task
 * @param[checklists] Checklists in the task
 * @param[attachments] Attachments in the task
 * @param[tabIndex] 0 = Comment, 1 = Checklist, 2 = Subtask, 3 = Attachment
 * @param[isRefreshing] Used for swipe refresh state when the user attempts to refresh the page
 * @param[deleteButtonEnabled] A status for delete button. Disable button temporarily if it is in calling server state.
 */
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

/**
 * Data in about task page in comment menu. Received from API as CommentDTO Object transformed to this state
 *
 * @param[commentId] Id of comment
 * @param[taskId] Task id where the comment should be attached
 * @param[description] Description of comment
 * @param[replyId] Other comment id the comment replied
 * @param[mentionsName] The names of users mentioned
 * @param[user] The user sent the comment
 * @param[sentDate] Date the comment sent
 * @param[likesId] The id of users that like the comment
 * @param[likeIconEnabled] A status for the like icon. Disable icon temporarily if it is in calling server state.
 * @param[deleteIconEnabled] A status for the delete icon. Disable icon temporarily if it is in calling server state.
 */
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

/**
 * Data in about task page in subtask menu. Received from API as SubtaskDTO Object transformed to this state
 *
 * @param[subtaskId] Id of subtask
 * @param[taskId] Task id where subtask should be attached
 * @param[description] Description of subtask
 * @param[due] Due date of subtask
 * @param[priority] Priority of subtask
 * @param[status] Status of subtask
 * @param[type] Type of subtask
 * @param[assignees] Assignees of subtask
 * @param[creator] Creator of subtask
 * @param[deleteIconEnabled] A status for the delete icon. Disable icon temporarily if it is in calling server state.
 */
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

/**
 * Data in about task page in checklist menu. Received from API as ChecklistDTO Object transformed to this state
 *
 * @param[checklistId] Id of checklist
 * @param[taskId] Task id where the checklist should be attached
 * @param[user] Creator of checklist
 * @param[description] Description of checklist
 * @param[isChecked] Whether checklist is checked
 * @param[assignees] Assignees of checklist
 * @param[sentDate] Date the checklist created
 * @param[deleteIconEnabled] A status for the delete icon. Disable icon temporarily if it is in calling server state.
 * @param[checkButtonEnabled] A status for check button. Disable button temporarily if it is in calling server state.
 */
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

/**
 * Data in about task page in attachment menu. Received from API as AttachmentDTO Object transformed to this state
 *
 * @param[attachmentId] Id of attachment
 * @param[taskId] Task id where the attachment should be attached
 * @param[user] The user that uploaded attachment
 * @param[attachmentPath] The path of attachment in server
 * @param[fileName] The file name of attachment
 * @param[sentDate] Date the attachment uploaded
 * @param[deleteIconEnabled] A status for the delete icon. Disable icon temporarily if it is in calling server state.
 */
data class AttachmentState(
    val attachmentId: Int,
    val taskId: Int,
    val user: UserDTO,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime,
    val deleteIconEnabled: Boolean
)

/**
 * Data in about message page. Received from API as MessageDTO Object transformed to this state
 *
 * @param[messageId] Id of message
 * @param[title] Title of message
 * @param[description] Description of message
 * @param[sentDate] Date the message sent
 * @param[sender] User sent the message
 * @param[receiver] User received the message
 * @param[attachmentPaths] The path of this attachment/file in the server. Not shown in user interface. Used to know what file should be downloaded.
 * @param[fileNames] The name of the file when it was uploaded. This is what shown in user interface as the file name.
 * @param[replies] The replies in the message
 * @param[deleteButtonEnabled] A status for delete button. Disable button temporarily if it is in calling server state.
 * @param[deleteForUserButtonEnabled] A status for delete message for only the user button. Disable button temporarily if it is in calling server state.
 */
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

/**
 * Data in about message page in reply menu. Received from API as MessageReplyDTO Object transformed to this state
 *
 * @param[messageReplyId] Id of message reply
 * @param[messageId] Message id where the reply should be attachment
 * @param[sentDate] Date the reply sent
 * @param[description] Description of reply
 * @param[fromId] Id of sender
 * @param[attachmentPaths] The path of this attachment/file in the server. Not shown in user interface. Used to know what file should be downloaded.
 * @param[fileNames] The name of the file when it was uploaded. This is what shown in user interface as the file name.
 * @param[deleteIconEnabled] A status for the delete icon. Disable icon temporarily if it is in calling server state.
 */
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

/**
 * Data in inbox page. Received from API as MessagePartDTO Object transformed to this state
 *
 * @param[messageId] Id of message
 * @param[sentDate] Date the message sent
 * @param[other] The other user. If current user is the sender, the other user is the receiver, vice versa.
 * @param[title] Subject/title of message
 * @param[deleteButtonEnabled] A status for delete button. Disable button temporarily if it is in calling server state.
 */
data class MessagePartState(
    val messageId: Int,
    val sentDate: LocalDateTime,
    val other: UserDTO,
    val title: String,
    val deleteButtonEnabled: Boolean
)