package com.serrano.dictproject.utils

import java.time.LocalDateTime

/**
 * A wrapper class for different responses (success, error, validation error)
 *
 * @param[data] Successful response data
 * @param[clientError] Client error data
 * @param[serverError] Server error data
 * @param[genericError] Other error data
 */
sealed class Resource<T>(val data: T?, val clientError: ClientErrorObj?, val serverError: ServerErrorObj?, val genericError: String?) {
    /**
     * Successful response object wrapper
     */
    class Success<T>(data: T) : Resource<T>(data, null, null, null)

    /**
     * Client error (validation error) response object wrapper
     */
    class ClientError<T>(clientError: ClientErrorObj) : Resource<T>(null, clientError, null, null)

    /**
     * Server error response object wrapper
     */
    class ServerError<T>(serverError: ServerErrorObj) : Resource<T>(null, null, serverError, null)

    /**
     * Other error response object wrapper
     */
    class GenericError<T>(genericError: String): Resource<T>(null, null, null, genericError)
}

/**
 * A class for client error (validation error)
 *
 * @param[type] Type of error (always validation error)
 * @param[message] The description of the error
 */
data class ClientErrorObj(
    val type: String,
    val message: String
)

/**
 * A class for server error
 *
 * @param[error] The description of the error
 */
data class ServerErrorObj(
    val error: String
)

/**
 * Response for successful login/signup
 *
 * @param[message] The successful message
 * @param[token] The authorization token of user (used for request routes that require authorization)
 * @param[id] The unique id of user
 * @param[name] The username of user
 * @param[email] The email address of user
 * @param[password] The non-hashed password of user
 * @param[image] The image of user in base64 encoded string
 */
data class SignUpSuccess(
    val message: String,
    val token: String,
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val image: String
)

/**
 * Response for SOME successful post, put, delete requests. A request that does not need to return any payload/data and only a single message
 *
 * @param[message] The successful message
 */
data class Success(
    val message: String
)

/**
 * Request body that will be sent to backend, upon signing up
 *
 * @param[name] The entered username in sign up field
 * @param[email] The entered email in sign up field
 * @param[password] The entered password in sign up field
 * @param[confirmPassword] The entered confirm password in sign up field
 * @param[notificationToken] FCM token used to send user push notifications
 */
data class Signup(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val notificationToken: String
)

/**
 * Request body used for sending the email to the backend where the mail with code be sent
 *
 * @param[email] A valid email, where the code will be sent
 */
data class ForgotPasswordBody(
    val email: String
)

/**
 * Request body used for changing password through entering code
 *
 * @param[email] The email in sign up field, used to check which account password should be changed
 * @param[code] The code that was sent to the users mail. used for verification
 * @param[password] The new password
 * @param[confirmPassword] Confirm the new password
 */
data class ForgotChangePasswordBody(
    val email: String,
    val code: String,
    val password: String,
    val confirmPassword: String
)

/**
 * Request body that will be sent to backend, upon logging in
 *
 * @param[email] The entered email in log in field
 * @param[password] The entered password in log in field
 * @param[notificationToken] FCM token used to send user push notifications
 */
data class Login(
    val email: String,
    val password: String,
    val notificationToken: String
)

/**
 * Request body used when creating/adding tasks
 *
 * @param[title] The entered title of task, should be 15-100 characters
 * @param[description] The entered description of task, should be 50-1000 characters
 * @param[priority] The entered priority of task
 * @param[due] The entered due date of task, should not be less than the current date
 * @param[type] The entered type of task
 * @param[assignee] The id of selected assignees/user, should range 1-5
 */
data class TaskBody(
    val title: String,
    val description: String,
    val priority: String,
    val due: LocalDateTime,
    val type: String,
    val assignee: List<Int>
)

/**
 * Request body used when sending comments to task
 *
 * @param[description] The entered description of comment
 * @param[taskId] The id that will be used where to add the comment
 * @param[replyId] The comment id that the new comment will reply
 * @param[mentionsId] The user ids that the new comment mentioned
 */
data class CommentBody(
    val description: String,
    val taskId: Int,
    val replyId: List<Int>,
    val mentionsId: List<Int>
)

/**
 * Request body used when sending message to other user. This should be with the form data list with attachments/files
 *
 * @param[receiverId] The id of the recipient
 * @param[title] The entered title in the add message menu
 * @param[description] The entered description in the add message menu
 */
data class MessageBody(
    val receiverId: Int,
    val title: String,
    val description: String
)

/**
 * Request body used when an assignee edited/changed the status of task
 *
 * @param[taskId] The id that will be used what task should be changed the status
 * @param[status] The new status
 */
data class StatusChange(
    val taskId: Int,
    val status: String
)

/**
 * Request body used when the creator edited/changed the assignees of task
 *
 * @param[taskId] The id that will be used what task should be changed the assignees
 * @param[assignee] The id of the new assignees
 */
data class AssigneeEdit(
    val taskId: Int,
    val assignee: List<Int>
)

/**
 * Request body used when the creator edited/changed the due date of task
 *
 * @param[taskId] The id that will be used what task should be changed the due date
 * @param[due] The new due date
 */
data class DueChange(
    val taskId: Int,
    val due: LocalDateTime
)

/**
 * Request body used when the creator edited/changed the priority of task
 *
 * @param[taskId] The id that will be used what task should be changed the priority
 * @param[priority] The new priority
 */
data class PriorityChange(
    val taskId: Int,
    val priority: String
)

/**
 * Request body used when the creator edited/changed the type of task
 *
 * @param[taskId] The id that will be used what task should be changed the type
 * @param[type] The new type
 */
data class TypeChange(
    val taskId: Int,
    val type: String
)

/**
 * Request body used when the creator edited/changed the name of task
 *
 * @param[taskId] The id that will be used what task should be changed the name
 * @param[title] The new name of task
 */
data class NameChange(
    val taskId: Int,
    val title: String
)

/**
 * Request body used when the creator edited/changed the description of task
 *
 * @param[taskId] The id that will be used what task should be changed the description
 * @param[description] The new description of task
 */
data class DescriptionChange(
    val taskId: Int,
    val description: String
)

/**
 * Request body used when adding subtasks to task
 *
 * @param[taskId] The id that will be used where to add the subtask
 * @param[description] The entered description of subtask
 * @param[priority] The entered priority of subtask
 * @param[due] The entered due date of subtask
 * @param[type] The entered type of subtask
 * @param[assignee] The id of selected assignees of subtask
 */
data class SubtaskBody(
    val taskId: Int,
    val description: String,
    val priority: String,
    val due: LocalDateTime,
    val type: String,
    val assignee: List<Int>
)

/**
 * Request body used when adding checklists to task
 *
 * @param[taskId] The id that will be used where to add the checklist
 * @param[description] The entered description of checklist
 * @param[assignee] The id of selected assignees of checklist
 */
data class ChecklistBody(
    val taskId: Int,
    val description: String,
    val assignee: List<Int>
)

/**
 * Request body used when the creator edited/changed the description of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the description
 * @param[description] The new description of subtask
 */
data class SubtaskDescriptionChange(
    val subtaskId: Int,
    val description: String
)

/**
 * Request body used when the creator edited/changed the priority of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the priority
 * @param[priority] The new priority of subtask
 */
data class SubtaskPriorityChange(
    val subtaskId: Int,
    val priority: String
)

/**
 * Request body used when the creator edited/changed the type of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the type
 * @param[type] The new type of subtask
 */
data class SubtaskTypeChange(
    val subtaskId: Int,
    val type: String
)

/**
 * Request body used when the creator edited/changed the due date of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the due date
 * @param[due] The new due date of subtask
 */
data class SubtaskDueChange(
    val subtaskId: Int,
    val due: LocalDateTime
)

/**
 * Request body used when the assignee edited/changed the status of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the status
 * @param[status] The new status of subtask
 */
data class SubtaskStatusChange(
    val subtaskId: Int,
    val status: String
)

/**
 * Request body used when the creator edited/changed the assignees of subtask
 *
 * @param[subtaskId] The id that will be used what subtask should be changed the assignees
 * @param[assignee] The id of the new assignees of subtask
 */
data class SubtaskAssigneeEdit(
    val subtaskId: Int,
    val assignee: List<Int>
)

/**
 * Request body used when the assignee check/uncheck checklist
 *
 * @param[checklistId] The id that will be used what checklist should be checked/unchecked
 * @param[check] The value check = true, uncheck = false
 */
data class ToggleChecklist(
    val checklistId: Int,
    val check: Boolean
)

/**
 * Request body used when an assignee/creator like/unlike a task comment
 *
 * @param[commentId] The id that will be used what comment should be liked/unliked
 */
data class LikeComment(
    val commentId: Int
)

/**
 * Request body used when the user change his/her role
 *
 * @param[role] The new role of user
 */
data class UserRoleChange(
    val role: String
)

/**
 * Request body used when the user change his/her username
 *
 * @param[name] The new username of user
 */
data class UserNameChange(
    val name: String
)

/**
 * Request body used when the user replied to a message
 *
 * @param[messageId] The message id that will be used where the reply should be added
 * @param[description] The description of the reply
 */
data class ReplyBody(
    val messageId: Int,
    val description: String
)

/**
 * Request body used when the user delete the message only on his/her inbox
 *
 * @param[messageId] The message to be deleted
 */
data class MessageIdBody(
    val messageId: Int
)

/**
 * Request body used when the user changes his/her password
 *
 * @param[currentPassword] The entered current password of user
 * @param[newPassword] The entered new password of user
 * @param[confirmPassword] The entered confirm password of user
 */
data class PasswordBody(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

/**
 * Request body used that will be sent to the backend when a new token has been generated by FCM
 *
 * @param[token] The new generated token to be saved in backend
 */
data class NotificationTokenBody(
    val token: String
)

/**
 * This response is used in dashboard page in a form of list transformed to TaskPartState
 *
 * @param[taskId] Id of the task
 * @param[title] Title of the task
 * @param[description] Description of the task
 * @param[due] Due date of the task
 * @param[priority] Priority of the task
 * @param[status] Status of the task
 * @param[type] Type of the task
 * @param[assignees] Assignees of the task
 * @param[creator] Creator of the task
 */
data class TaskPartDTO(
    val taskId: Int = 0,
    val title: String = dummyTitle,
    val description: String = loremIpsum,
    val due: LocalDateTime = LocalDateTime.now(),
    val priority: String = "LOW",
    val status: String = "ON HOLD",
    val type: String = "TASK",
    val assignees: List<UserDTO> = listOf(userDTO, userDTO, userDTO),
    val creator: UserDTO = userDTO
)

/**
 * This response is used in about task page transformed to TaskState
 *
 * @param[taskId] Id of the task
 * @param[title] Title of the task
 * @param[description] Description of the task
 * @param[due] Due date of the task
 * @param[priority] Priority of the task
 * @param[status] Status of the task
 * @param[type] Type of the task
 * @param[sentDate] The date when the task created
 * @param[assignees] Assignees of the task
 * @param[creator] Creator of the task
 * @param[comments] Comments in the task
 * @param[subtasks] Subtasks in the task
 * @param[checklists] Checklists in the task
 * @param[attachments] Attachments in the task
 */
data class TaskDTO(
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
    val comments: List<CommentDTO> = emptyList(),
    val subtasks: List<SubtaskDTO> = emptyList(),
    val checklists: List<ChecklistDTO> = emptyList(),
    val attachments: List<AttachmentDTO> = emptyList()
)

/**
 * This class is a property of TaskDTO in a form of list and used in about task page in comment tab transformed to CommentState
 *
 * @param[commentId] Id of the comment
 * @param[taskId] Task id where the comment should be attached
 * @param[description] Description of the comment
 * @param[replyId] The id of other comment that this comment replied
 * @param[mentionsName] The names of user that this comment mentioned
 * @param[user] The user sent this comment
 * @param[sentDate] The date comment sent
 * @param[likesId] The id of users that like this comment
 */
data class CommentDTO(
    val commentId: Int,
    val taskId: Int,
    val description: String,
    val replyId: List<Int>,
    val mentionsName: List<String>,
    val user: UserDTO,
    val sentDate: LocalDateTime,
    val likesId: List<Int>
)

/**
 * This response is used in inbox page in a form of list transformed to MessagePartState
 *
 * @param[messageId] The id of message
 * @param[sentDate] The date the message sent
 * @param[other] The other user. If current user is the sender, the other user is the receiver, vice versa.
 * @param[title] The subject/title of message
 */
data class MessagePartDTO(
    val messageId: Int,
    val sentDate: LocalDateTime,
    val other: UserDTO,
    val title: String
)

/**
 * This response is used in about message page transformed to MessageState
 *
 * @param[messageId] The id of message
 * @param[title] The subject/title of message
 * @param[description] The description of message
 * @param[sentDate] The date the message sent
 * @param[sender] The user send the message
 * @param[receiver] The user receive the message
 * @param[attachmentPaths] The path of this attachments/files in the server. Not shown in user interface. Used to know what file should be downloaded.
 * @param[fileNames] The name of the files when it was uploaded. This is what shown in user interface as the file name.
 * @param[replies] Replies in the message
 */
data class MessageDTO(
    val messageId: Int = 1,
    val title: String = dummyTitle,
    val description: String = loremIpsum,
    val sentDate: LocalDateTime = LocalDateTime.now(),
    val sender: UserDTO = userDTO,
    val receiver: UserDTO = userDTO,
    val attachmentPaths: List<String> = emptyList(),
    val fileNames: List<String> = emptyList(),
    val replies: List<MessageReplyDTO> = emptyList()
)

/**
 * This class is a property of MessageDTO in a form of list and used in about message page in the reply section transformed to MessageReplyState
 *
 * @param[messageReplyId] Id of the reply
 * @param[messageId] Message id where this reply should be attached
 * @param[sentDate] The date the reply sent
 * @param[description] Description of the reply
 * @param[fromId] Id of the sender
 * @param[attachmentPaths] The path of this attachments/files in the server. Not shown in user interface. Used to know what file should be downloaded.
 * @param[fileNames] The name of the files when it was uploaded. This is what shown in user interface as the file name.
 */
data class MessageReplyDTO(
    val messageReplyId: Int,
    val messageId: Int,
    val sentDate: LocalDateTime,
    val description: String,
    val fromId: Int,
    val attachmentPaths: List<String>,
    val fileNames: List<String>
)

/**
 * This class represents the information of user
 *
 * @param[id] The id of user
 * @param[name] The name of user
 * @param[image] The image of user in base64 encoded string
 */
data class UserDTO(
    val id: Int,
    val name: String,
    val image: String
)

/**
 * This class is a property of TaskDTO in a form of list and used in about task page in subtask tab transformed to SubtaskState
 *
 * @param[subtaskId] Id of subtask
 * @param[taskId] Task id where this subtask should be attached
 * @param[description] Description of subtask
 * @param[due] Due date of subtask
 * @param[priority] Priority of subtask
 * @param[status] Status of subtask
 * @param[type] Type of subtask
 * @param[assignees] Assignees of subtask
 * @param[creator] Creator of subtask
 */
data class SubtaskDTO(
    val subtaskId: Int,
    val taskId: Int,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val assignees: List<UserDTO>,
    val creator: UserDTO
)

/**
 * This class is a property of TaskDTO in a form of list and used in about task page in checklist tab transformed to ChecklistState
 *
 * @param[checklistId] Id of checklist
 * @param[taskId] Task id where this checklist should be attached
 * @param[user] The user created this checklist
 * @param[description] Description of checklist
 * @param[isChecked] Status of checklist (checked/unchecked)
 * @param[assignees] Assignees of checklist
 * @param[sentDate] Date when the checklist created
 */
data class ChecklistDTO(
    val checklistId: Int,
    val taskId: Int,
    val user: UserDTO,
    val description: String,
    val isChecked: Boolean,
    val assignees: List<UserDTO>,
    val sentDate: LocalDateTime
)

/**
 * This class is a property of TaskDTO in a form of list and used in about task page in attachment tab transformed to AttachmentState
 *
 * @param[attachmentId] Id of attachment
 * @param[taskId] Task id where this attachment should be attached
 * @param[user] The user uploaded the attachment
 * @param[attachmentPath] The path of this attachment/file in the server. Not shown in user interface. Used to know what file should be downloaded.
 * @param[fileName] The name of the file when it was uploaded. This is what shown in user interface as the file name.
 * @param[sentDate] The date the attachment uploaded
 */
data class AttachmentDTO(
    val attachmentId: Int,
    val taskId: Int,
    val user: UserDTO,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime
)

/**
 * The class used in profile page. Contains more information about the user.
 *
 * @param[id] The id of user
 * @param[name] The name of user
 * @param[email] The email address of user
 * @param[image] The image of user in base64 encoded string
 * @param[role] The role of user
 */
data class ProfileDataDTO(
    val id: Int = 0,
    val name: String = "TestUser",
    val email: String = "test@gmail.com",
    val image: String = imageStr,
    val role: String = "DICT Worker"
)