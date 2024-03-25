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
    val confirmPassword: String
)

data class Login(
    val email: String,
    val password: String
)

data class TaskPart(
    val taskId: Int = 0,
    val title: String = "Make homework Make homework Make homework",
    val description: String = "Make your mathematics homework you nerd! Make your mathematics homework you nerd! Make your mathematics homework you nerd!", // unused use in larger screen
    val due: LocalDateTime = LocalDateTime.now(),
    val priority: String = "LOW",
    val status: String = "ON HOLD",
    val type: String = "TASK",
    val assignees: List<User> = listOf(User(0, "Danzwomen", imageStr), User(0, "Danzwomen", imageStr), User(0, "Danzwomen", imageStr)),
    val creator: User = User(0, "Danzwomen", imageStr)
)

data class Task(
    val taskId: Int = 0,
    val title: String = "Make homework",
    val description: String = "Make your mathematics homework you nerd! Make your mathematics homework you nerd! Make your mathematics homework you nerd!",
    val due: LocalDateTime = LocalDateTime.now(),
    val priority: String = "LOW",
    val status: String = "ON HOLD",
    val type: String = "TASK",
    val sentDate: LocalDateTime = LocalDateTime.now(),
    val assignees: List<User> = listOf(User(0, "Danzwomen", imageStr), User(0, "Danzwomen", imageStr), User(0, "Danzwomen", imageStr)),
    val creator: User = User(0, "Danzwomen", imageStr),
    val comments: List<Comment> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val checklists: List<Checklist> = emptyList(),
    val attachments: List<Attachment> = emptyList()
)

data class Comment(
    val commentId: Int,
    val description: String,
    val user: User,
    val sentDate: LocalDateTime,
    val likesId: List<Int>
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
    val taskId: Int
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

data class MessagePart(
    val messageId: Int,
    val sentDate: LocalDateTime,
    val other: User,
    val title: String
)

data class Message(
    val messageId: Int = 1,
    val title: String = "Geometry Dash Sexies",
    val description: String = "Vina Rachelle, owner of Killua, a golden retriever dog reportedly killed by an individual in Bato, Camarines Sur, is seeking justice following the untimely and brutal death of her beloved pet. Rachelle, in a Facebook post shared on Sunday, March 17, 2024, expressed that she discovered Killua's lifeless body inside a sack at the suspect's location. Mahal na mahal ko yan. We found his lifeless body inside a sack the owner said. In a viral CCTV footage, the suspect can also be seen chasing after the dog and striking it. For the information of everyone, hindi yan nakalabas kasi bukas ang gate. The gate was locked. Tumaas siya tapos nahulog ata, we don't exactly know what happened. He was probably anxious and stressed, hindi siya sanay sa labas kasi sa loob lang yan ng bahay palagi. Kaya, no, hindi namin binuksan yung pinto kaya nakalabas. And if nangagat man, it is not enough reason to kill my pet. He was asking for apology, but no sorry could ever replace my baby, Rachelle added.",
    val sentDate: LocalDateTime = LocalDateTime.now(),
    val sender: User = User(0, "Aeonsexy", imageStr),
    val receiver: User = User(1, "Danzwomen", imageStr)
)

data class User(
    val id: Int,
    val name: String,
    val image: String
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

data class AttachmentBody(
    val taskId: Int
)

data class Subtask(
    val subtaskId: Int,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val assignees: List<User>,
    val creator: User
)

data class Checklist(
    val checklistId: Int,
    val user: User,
    val description: String,
    val isChecked: Boolean,
    val assignees: List<User>,
    val sentDate: LocalDateTime
)

data class Attachment(
    val user: User,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime
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

data class ProfileData(
    val id: Int = 0,
    val name: String = "EricGirlyWilderman",
    val email: String = "ericgirl@gmail.com",
    val image: String = imageStr,
    val role: String = "Geometry Dash Youtuber"
)

data class UserRoleChange(
    val role: String
)

data class UserNameChange(
    val name: String
)

const val imageStr = "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\nQmCC\n"