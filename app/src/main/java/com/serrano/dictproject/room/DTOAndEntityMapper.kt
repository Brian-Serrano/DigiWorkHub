package com.serrano.dictproject.room

import com.serrano.dictproject.utils.AttachmentDTO
import com.serrano.dictproject.utils.ChecklistDTO
import com.serrano.dictproject.utils.CommentDTO
import com.serrano.dictproject.utils.MessageDTO
import com.serrano.dictproject.utils.MessagePartDTO
import com.serrano.dictproject.utils.MessageReplyDTO
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.SubtaskDTO
import com.serrano.dictproject.utils.TaskDTO
import com.serrano.dictproject.utils.TaskPartDTO
import com.serrano.dictproject.utils.UserDTO
import kotlinx.coroutines.flow.first

fun TaskPartDTO.toEntity(): TaskPart {
    return TaskPart(
        taskId = taskId,
        title = title,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        assigneesId = assignees.map { it.id },
        creatorId = creator.id
    )
}

fun TaskPartDTO.getUsers(): List<User> {
    return assignees.map { it.toEntity() } + creator.toEntity()
}

fun TaskDTO.toEntity(): Task {
    return Task(
        taskId = taskId,
        title = title,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        sentDate = sentDate,
        assigneesId = assignees.map { it.id },
        creatorId = creator.id,
        commentsId = comments.map { it.commentId },
        subtasksId = subtasks.map { it.subtaskId },
        checklistsId = checklists.map { it.checklistId },
        attachmentsId = attachments.map { it.attachmentId }
    )
}

fun TaskDTO.getUsers(): List<User> {
    return assignees.map { it.toEntity() } + creator.toEntity()
}

fun CommentDTO.toEntity(): Comment {
    return Comment(
        commentId = commentId,
        taskId = taskId,
        description = description,
        replyId = replyId,
        mentionsName = mentionsName,
        userId = user.id,
        sentDate = sentDate,
        likesId = likesId
    )
}

fun CommentDTO.getUsers(): List<User> {
    return listOf(user.toEntity())
}

fun SubtaskDTO.toEntity(): Subtask {
    return Subtask(
        subtaskId = subtaskId,
        taskId = taskId,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        assigneesId = assignees.map { it.id },
        creatorId = creator.id
    )
}

fun SubtaskDTO.getUsers(): List<User> {
    return assignees.map { it.toEntity() } + creator.toEntity()
}

fun ChecklistDTO.toEntity(): Checklist {
    return Checklist(
        checklistId = checklistId,
        taskId = taskId,
        userId = user.id,
        description = description,
        isChecked = isChecked,
        assigneesId = assignees.map { it.id },
        sentDate = sentDate
    )
}

fun ChecklistDTO.getUsers(): List<User> {
    return assignees.map { it.toEntity() } + user.toEntity()
}

fun AttachmentDTO.toEntity(): Attachment {
    return Attachment(
        attachmentId = attachmentId,
        taskId = taskId,
        userId = user.id,
        attachmentPath = attachmentPath,
        fileName = fileName,
        sentDate = sentDate
    )
}

fun AttachmentDTO.getUsers(): List<User> {
    return listOf(user.toEntity())
}

fun MessagePartDTO.toEntity(tag: String): MessagePart {
    return MessagePart(
        messageId = messageId,
        sentDate = sentDate,
        otherId = other.id,
        title = title,
        tag = tag
    )
}

fun MessageDTO.toEntity(): Message {
    return Message(
        messageId = messageId,
        title = title,
        description = description,
        sentDate = sentDate,
        senderId = sender.id,
        receiverId = receiver.id,
        attachmentPaths = attachmentPaths,
        fileNames = fileNames,
        replies = replies.map { it.messageReplyId }
    )
}

fun MessageReplyDTO.toEntity(): MessageReply {
    return MessageReply(
        messageReplyId = messageReplyId,
        messageId = messageId,
        sentDate = sentDate,
        description = description,
        fromId = fromId,
        attachmentPaths = attachmentPaths,
        fileNames = fileNames
    )
}

fun ProfileDataDTO.toEntity(): ProfileData {
    return ProfileData(
        id = id,
        name = name,
        email = email,
        image = image,
        role = role
    )
}

fun UserDTO.toEntity(): User {
    return User(
        id = id,
        name = name,
        image = image
    )
}

suspend fun TaskPart.toDTO(dao: Dao): TaskPartDTO {
    return TaskPartDTO(
        taskId = taskId,
        title = title,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        assignees = assigneesId.map { id -> dao.getUser(id).first().toDTO() },
        creator = dao.getUser(creatorId).first().toDTO()
    )
}

suspend fun Task.toDTO(dao: Dao): TaskDTO {
    return TaskDTO(
        taskId = taskId,
        title = title,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        sentDate = sentDate,
        assignees = assigneesId.map { id -> dao.getUser(id).first().toDTO() },
        creator = dao.getUser(creatorId).first().toDTO(),
        comments = commentsId.map { id -> dao.getComment(id).first().toDTO(dao) },
        subtasks = subtasksId.map { id -> dao.getSubtask(id).first().toDTO(dao) },
        checklists = checklistsId.map { id -> dao.getChecklist(id).first().toDTO(dao) },
        attachments = attachmentsId.map { id -> dao.getAttachment(id).first().toDTO(dao) }
    )
}

suspend fun Comment.toDTO(dao: Dao): CommentDTO {
    return CommentDTO(
        commentId = commentId,
        taskId = taskId,
        description = description,
        replyId = replyId,
        mentionsName = mentionsName,
        user = dao.getUser(userId).first().toDTO(),
        sentDate = sentDate,
        likesId = likesId
    )
}

suspend fun Subtask.toDTO(dao: Dao): SubtaskDTO {
    return SubtaskDTO(
        subtaskId = subtaskId,
        taskId = taskId,
        description = description,
        due = due,
        priority = priority,
        status = status,
        type = type,
        assignees = assigneesId.map { id -> dao.getUser(id).first().toDTO() },
        creator = dao.getUser(creatorId).first().toDTO()
    )
}

suspend fun Checklist.toDTO(dao: Dao): ChecklistDTO {
    return ChecklistDTO(
        checklistId = checklistId,
        taskId = taskId,
        user = dao.getUser(userId).first().toDTO(),
        description = description,
        isChecked = isChecked,
        assignees = assigneesId.map { id -> dao.getUser(id).first().toDTO() },
        sentDate = sentDate
    )
}

suspend fun Attachment.toDTO(dao: Dao): AttachmentDTO {
    return AttachmentDTO(
        attachmentId = attachmentId,
        taskId = taskId,
        user = dao.getUser(userId).first().toDTO(),
        attachmentPath = attachmentPath,
        fileName = fileName,
        sentDate = sentDate
    )
}

suspend fun MessagePart.toDTO(dao: Dao): MessagePartDTO {
    return MessagePartDTO(
        messageId = messageId,
        sentDate = sentDate,
        other = dao.getUser(otherId).first().toDTO(),
        title = title
    )
}

suspend fun Message.toDTO(dao: Dao): MessageDTO {
    return MessageDTO(
        messageId = messageId,
        title = title,
        description = description,
        sentDate = sentDate,
        sender = dao.getUser(senderId).first().toDTO(),
        receiver = dao.getUser(receiverId).first().toDTO(),
        attachmentPaths = attachmentPaths,
        fileNames = fileNames,
        replies = replies.map { id -> dao.getMessageReplies(id).first().toDTO() }
    )
}

fun MessageReply.toDTO(): MessageReplyDTO {
    return MessageReplyDTO(
        messageReplyId = messageReplyId,
        messageId = messageId,
        sentDate = sentDate,
        description = description,
        fromId = fromId,
        attachmentPaths = attachmentPaths,
        fileNames = fileNames
    )
}

fun ProfileData.toDTO(): ProfileDataDTO {
    return ProfileDataDTO(
        id = id,
        name = name,
        email = email,
        image = image,
        role = role
    )
}

fun User.toDTO(): UserDTO {
    return UserDTO(
        id = id,
        name = name,
        image = image
    )
}