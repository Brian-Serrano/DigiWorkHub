package com.serrano.dictproject.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class TaskPart(
    @PrimaryKey
    val taskId: Int,
    val title: String,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val assigneesId: List<Int>,
    val creatorId: Int
)

@Entity
data class Task(
    @PrimaryKey
    val taskId: Int,
    val title: String,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val sentDate: LocalDateTime,
    val assigneesId: List<Int>,
    val creatorId: Int,
    val commentsId: List<Int>,
    val subtasksId: List<Int>,
    val checklistsId: List<Int>,
    val attachmentsId: List<Int>
)

@Entity
data class Comment(
    @PrimaryKey
    val commentId: Int,
    val taskId: Int,
    val description: String,
    val replyId: List<Int>,
    val mentionsName: List<String>,
    val userId: Int,
    val sentDate: LocalDateTime,
    val likesId: List<Int>
)

@Entity
data class Subtask(
    @PrimaryKey
    val subtaskId: Int,
    val taskId: Int,
    val description: String,
    val due: LocalDateTime,
    val priority: String,
    val status: String,
    val type: String,
    val assigneesId: List<Int>,
    val creatorId: Int
)

@Entity
data class Checklist(
    @PrimaryKey
    val checklistId: Int,
    val taskId: Int,
    val userId: Int,
    val description: String,
    val isChecked: Boolean,
    val assigneesId: List<Int>,
    val sentDate: LocalDateTime
)

@Entity
data class Attachment(
    @PrimaryKey
    val attachmentId: Int,
    val taskId: Int,
    val userId: Int,
    val attachmentPath: String,
    val fileName: String,
    val sentDate: LocalDateTime
)

@Entity
data class ProfileData(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val image: String,
    val role: String
)

@Entity
data class MessagePart(
    @PrimaryKey
    val messageId: Int,
    val sentDate: LocalDateTime,
    val otherId: Int,
    val title: String,
    val tag: String
)

@Entity
data class Message(
    @PrimaryKey
    val messageId: Int,
    val title: String,
    val description: String,
    val sentDate: LocalDateTime,
    val senderId: Int,
    val receiverId: Int,
    val attachmentPaths: List<String>,
    val fileNames: List<String>,
    val replies: List<Int>
)

@Entity
data class MessageReply(
    @PrimaryKey
    val messageReplyId: Int,
    val messageId: Int,
    val sentDate: LocalDateTime,
    val description: String,
    val fromId: Int,
    val attachmentPaths: List<String>,
    val fileNames: List<String>
)

@Entity
data class User(
    @PrimaryKey
    val id: Int,
    val name: String,
    val image: String
)

data class RoomIsDumb(
    val ids: List<Int>
)