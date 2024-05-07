package com.serrano.dictproject.utils

import java.time.LocalDateTime

sealed class Resource<T>(val data: T?, val clientError: ClientErrorObj?, val serverError: ServerErrorObj?, val genericError: String?) {
    class Success<T>(data: T) : Resource<T>(data, null, null, null)
    class ClientError<T>(clientError: ClientErrorObj) : Resource<T>(null, clientError, null, null)
    class ServerError<T>(serverError: ServerErrorObj) : Resource<T>(null, null, serverError, null)
    class GenericError<T>(genericError: String): Resource<T>(null, null, null, genericError)
}

data class ClientErrorObj(
    val type: String,
    val message: String
)

data class ServerErrorObj(
    val error: String
)

data class SignUpSuccess(
    val message: String,
    val token: String,
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val image: String
)

data class Success(
    val message: String
)

data class Signup(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val notificationToken: String
)

data class ForgotPasswordBody(
    val email: String
)

data class ForgotChangePasswordBody(
    val email: String,
    val code: String,
    val password: String,
    val confirmPassword: String
)

data class Login(
    val email: String,
    val password: String,
    val notificationToken: String
)

data class TaskBody(
    val title: String,
    val description: String,
    val priority: String,
    val due: LocalDateTime,
    val type: String,
    val assignee: List<Int>
)

data class CommentBody(
    val description: String,
    val taskId: Int,
    val replyId: List<Int>,
    val mentionsId: List<Int>
)

data class MessageBody(
    val receiverId: Int,
    val title: String,
    val description: String
)

data class StatusChange(
    val taskId: Int,
    val status: String
)

data class AssigneeEdit(
    val taskId: Int,
    val assignee: List<Int>
)

data class DueChange(
    val taskId: Int,
    val due: LocalDateTime
)

data class PriorityChange(
    val taskId: Int,
    val priority: String
)

data class TypeChange(
    val taskId: Int,
    val type: String
)

data class NameChange(
    val taskId: Int,
    val title: String
)

data class DescriptionChange(
    val taskId: Int,
    val description: String
)

data class SubtaskBody(
    val taskId: Int,
    val description: String,
    val priority: String,
    val due: LocalDateTime,
    val type: String,
    val assignee: List<Int>
)

data class ChecklistBody(
    val taskId: Int,
    val description: String,
    val assignee: List<Int>
)

data class SubtaskDescriptionChange(
    val subtaskId: Int,
    val description: String
)

data class SubtaskPriorityChange(
    val subtaskId: Int,
    val priority: String
)

data class SubtaskTypeChange(
    val subtaskId: Int,
    val type: String
)

data class SubtaskDueChange(
    val subtaskId: Int,
    val due: LocalDateTime
)

data class SubtaskStatusChange(
    val subtaskId: Int,
    val status: String
)

data class SubtaskAssigneeEdit(
    val subtaskId: Int,
    val assignee: List<Int>
)

data class ToggleChecklist(
    val checklistId: Int,
    val check: Boolean
)

data class LikeComment(
    val commentId: Int
)

data class UserRoleChange(
    val role: String
)

data class UserNameChange(
    val name: String
)

data class ReplyBody(
    val messageId: Int,
    val description: String
)

data class MessageIdBody(
    val messageId: Int
)

data class PasswordBody(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

data class NotificationTokenBody(
    val token: String
)

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

data class MessagePartDTO(
    val messageId: Int,
    val sentDate: LocalDateTime,
    val other: UserDTO,
    val title: String
)

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

data class MessageReplyDTO(
    val messageReplyId: Int,
    val messageId: Int,
    val sentDate: LocalDateTime,
    val description: String,
    val fromId: Int,
    val attachmentPaths: List<String>,
    val fileNames: List<String>
)

data class UserDTO(
    val id: Int,
    val name: String,
    val image: String
)

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

data class ChecklistDTO(
    val checklistId: Int,
    val taskId: Int,
    val user: UserDTO,
    val description: String,
    val isChecked: Boolean,
    val assignees: List<UserDTO>,
    val sentDate: LocalDateTime
)

data class AttachmentDTO(
    val attachmentId: Int,
    val taskId: Int,
    val user: UserDTO,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime
)

data class ProfileDataDTO(
    val id: Int = 0,
    val name: String = "EricGirlyWilderman",
    val email: String = "ericgirl@gmail.com",
    val image: String = imageStr,
    val role: String = "Geometry Dash Youtuber"
)