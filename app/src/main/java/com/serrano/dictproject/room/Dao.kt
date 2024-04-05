package com.serrano.dictproject.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskParts(taskParts: List<TaskPart>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<Subtask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklists(checklists: List<Checklist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<Attachment>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profileData: ProfileData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageParts(messageParts: List<MessagePart>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<MessageReply>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: Set<User>)

    @Query("SELECT * FROM TaskPart WHERE creatorId = :creatorId")
    fun getCreatedTasks(creatorId: Int): Flow<List<TaskPart>>

    @Query("SELECT * FROM TaskPart WHERE assigneesId LIKE :assigneeId")
    fun getTasks(assigneeId: String): Flow<List<TaskPart>>

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    fun getTask(taskId: Int): Flow<Task?>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    @Query("SELECT * FROM Comment WHERE commentId = :commentId")
    fun getComment(commentId: Int): Flow<Comment>

    @Query("SELECT * FROM Subtask WHERE subtaskId = :subtaskId")
    fun getSubtask(subtaskId: Int): Flow<Subtask>

    @Query("SELECT * FROM Checklist WHERE checklistId = :checklistId")
    fun getChecklist(checklistId: Int): Flow<Checklist>

    @Query("SELECT * FROM Attachment WHERE attachmentId = :attachmentId")
    fun getAttachment(attachmentId: Int): Flow<Attachment>

    @Query("SELECT * FROM MessagePart WHERE tag = :tag")
    fun getMessagePart(tag: String): Flow<List<MessagePart>>

    @Query("SELECT * FROM Message WHERE messageId = :messageId")
    fun getMessage(messageId: Int): Flow<Message?>

    @Query("SELECT * FROM MessageReply WHERE messageReplyId = :messageReplyId")
    fun getMessageReplies(messageReplyId: Int): Flow<MessageReply>

    @Query("SELECT * FROM ProfileData WHERE id = :id")
    fun getProfileData(id: Int): Flow<ProfileData?>

    @Query("SELECT commentsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskCommentsId(taskId: Int): Flow<RoomIsDumb>

    @Query("SELECT subtasksId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskSubtasksId(taskId: Int): Flow<RoomIsDumb>

    @Query("SELECT checklistsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskChecklistsId(taskId: Int): Flow<RoomIsDumb>

    @Query("SELECT attachmentsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskAttachmentsId(taskId: Int): Flow<RoomIsDumb>

    @Query("SELECT replies AS ids FROM Message WHERE messageId = :messageId")
    fun getMessageRepliesId(messageId: Int): Flow<RoomIsDumb>

    @Query("DELETE FROM TaskPart WHERE creatorId = :creatorId")
    suspend fun deleteCreatedTasks(creatorId: Int)

    @Query("DELETE FROM TaskPart WHERE assigneesId LIKE :assigneeId")
    suspend fun deleteTasks(assigneeId: String)

    @Query("DELETE FROM TaskPart WHERE taskId = :taskId")
    suspend fun deleteTaskPart(taskId: Int)

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM Comment WHERE taskId = :taskId")
    suspend fun deleteComments(taskId: Int)

    @Query("DELETE FROM Comment WHERE commentId = :commentId")
    suspend fun deleteComment(commentId: Int)

    @Query("DELETE FROM Subtask WHERE taskId = :taskId")
    suspend fun deleteSubtasks(taskId: Int)

    @Query("DELETE FROM Subtask WHERE subtaskId = :subtaskId")
    suspend fun deleteSubtask(subtaskId: Int)

    @Query("DELETE FROM Checklist WHERE taskId = :taskId")
    suspend fun deleteChecklists(taskId: Int)

    @Query("DELETE FROM Checklist WHERE checklistId = :checklistId")
    suspend fun deleteChecklist(checklistId: Int)

    @Query("DELETE FROM Attachment WHERE taskId = :taskId")
    suspend fun deleteAttachments(taskId: Int)

    @Query("DELETE FROM Attachment WHERE attachmentId = :attachmentId")
    suspend fun deleteAttachment(attachmentId: Int)

    @Query("DELETE FROM MessagePart WHERE tag = :tag")
    suspend fun deleteMessageParts(tag: String)

    @Query("DELETE FROM MessagePart WHERE messageId = :messageId")
    suspend fun deleteMessagePart(messageId: Int)

    @Query("DELETE FROM Message WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("DELETE FROM MessageReply WHERE messageId = :messageId")
    suspend fun deleteReplies(messageId: Int)

    @Query("DELETE FROM MessageReply WHERE messageReplyId = :messageReplyId")
    suspend fun deleteReply(messageReplyId: Int)

    @Query("DELETE FROM ProfileData WHERE id = :id")
    suspend fun deleteProfileData(id: Int)

    @Query("DELETE FROM Task")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM TaskPart")
    suspend fun deleteAllTaskParts()

    @Query("DELETE FROM Comment")
    suspend fun deleteAllComments()

    @Query("DELETE FROM Subtask")
    suspend fun deleteAllSubtasks()

    @Query("DELETE FROM Checklist")
    suspend fun deleteAllChecklists()

    @Query("DELETE FROM Attachment")
    suspend fun deleteAllAttachments()

    @Query("DELETE FROM Message")
    suspend fun deleteAllMessages()

    @Query("DELETE FROM MessagePart")
    suspend fun deleteAllMessageParts()

    @Query("DELETE FROM MessageReply")
    suspend fun deleteAllMessageReplies()

    @Query("DELETE FROM User")
    suspend fun deleteAllUsers()

    @Query("DELETE FROM ProfileData")
    suspend fun deleteAllProfileData()

    @Query("UPDATE Task SET title = :title WHERE taskId = :taskId")
    suspend fun updateTaskTitle(title: String, taskId: Int)

    @Query("UPDATE TaskPart SET title = :title WHERE taskId = :taskId")
    suspend fun updateTaskPartTitle(title: String, taskId: Int)

    @Query("UPDATE Task SET description = :description WHERE taskId = :taskId")
    suspend fun updateTaskDescription(description: String, taskId: Int)

    @Query("UPDATE TaskPart SET description = :description WHERE taskId = :taskId")
    suspend fun updateTaskPartDescription(description: String, taskId: Int)

    @Query("UPDATE Task SET status = :status WHERE taskId = :taskId")
    suspend fun updateTaskStatus(status: String, taskId: Int)

    @Query("UPDATE TaskPart SET status = :status WHERE taskId = :taskId")
    suspend fun updateTaskPartStatus(status: String, taskId: Int)

    @Query("UPDATE Task SET priority = :priority WHERE taskId = :taskId")
    suspend fun updateTaskPriority(priority: String, taskId: Int)

    @Query("UPDATE TaskPart SET priority = :priority WHERE taskId = :taskId")
    suspend fun updateTaskPartPriority(priority: String, taskId: Int)

    @Query("UPDATE Task SET type = :type WHERE taskId = :taskId")
    suspend fun updateTaskType(type: String, taskId: Int)

    @Query("UPDATE TaskPart SET type = :type WHERE taskId = :taskId")
    suspend fun updateTaskPartType(type: String, taskId: Int)

    @Query("UPDATE Task SET due = :due WHERE taskId = :taskId")
    suspend fun updateTaskDue(due: String, taskId: Int)

    @Query("UPDATE TaskPart SET due = :due WHERE taskId = :taskId")
    suspend fun updateTaskPartDue(due: String, taskId: Int)

    @Query("UPDATE Task SET assigneesId = :assigneesId WHERE taskId = :taskId")
    suspend fun updateTaskAssignees(assigneesId: String, taskId: Int)

    @Query("UPDATE TaskPart SET assigneesId = :assigneesId WHERE taskId = :taskId")
    suspend fun updateTaskPartAssignees(assigneesId: String, taskId: Int)

    @Query("UPDATE Task SET commentsId = :commentsId WHERE taskId = :taskId")
    suspend fun updateTaskComments(commentsId: String, taskId: Int)

    @Query("UPDATE Task SET subtasksId = :subtasksId WHERE taskId = :taskId")
    suspend fun updateTaskSubtasks(subtasksId: String, taskId: Int)

    @Query("UPDATE Task SET checklistsId = :checklistsId WHERE taskId = :taskId")
    suspend fun updateTaskChecklists(checklistsId: String, taskId: Int)

    @Query("UPDATE Task SET attachmentsId = :attachmentsId WHERE taskId = :taskId")
    suspend fun updateTaskAttachments(attachmentsId: String, taskId: Int)

    @Query("UPDATE Message SET replies = :replies WHERE messageId = :messageId")
    suspend fun updateMessageReplies(replies: String, messageId: Int)

    @Query("UPDATE Subtask SET description = :description WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskDescription(description: String, subtaskId: Int)

    @Query("UPDATE Subtask SET priority = :priority WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskPriority(priority: String, subtaskId: Int)

    @Query("UPDATE Subtask SET due = :due WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskDue(due: String, subtaskId: Int)

    @Query("UPDATE Subtask SET assigneesId = :assigneesId WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskAssignees(assigneesId: String, subtaskId: Int)

    @Query("UPDATE Subtask SET type = :type WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskType(type: String, subtaskId: Int)

    @Query("UPDATE Subtask SET status = :status WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskStatus(status: String, subtaskId: Int)

    @Query("UPDATE Checklist SET isChecked = :isChecked WHERE checklistId = :checklistId")
    suspend fun toggleChecklist(isChecked: Boolean, checklistId: Int)

    @Query("UPDATE Comment SET likesId = :likesId WHERE commentId = :commentId")
    suspend fun likeComment(likesId: String, commentId: Int)

    @Query("UPDATE ProfileData SET name = :name WHERE id = :id")
    suspend fun updateUserName(name: String, id: Int)

    @Query("UPDATE ProfileData SET role = :role WHERE id = :id")
    suspend fun updateUserRole(role: String, id: Int)

    @Query("UPDATE ProfileData SET image = :image WHERE id = :id")
    suspend fun updateUserImage(image: String, id: Int)

    @Transaction
    suspend fun dashboardInsertTasks(taskParts: List<TaskPart>, users: Set<User>) {
        insertTaskParts(taskParts)
        insertUsers(users)
    }

    @Transaction
    suspend fun aboutTaskInsertTasks(
        task: Task,
        comments: List<Comment>,
        subtasks: List<Subtask>,
        checklists: List<Checklist>,
        attachments: List<Attachment>,
        users: Set<User>
    ) {
        insertTask(task)
        insertComments(comments)
        insertSubtasks(subtasks)
        insertChecklists(checklists)
        insertAttachments(attachments)
        insertUsers(users)
    }

    @Transaction
    suspend fun aboutTaskDeleteTasks(taskId: Int) {
        deleteTask(taskId)
        deleteComments(taskId)
        deleteSubtasks(taskId)
        deleteChecklists(taskId)
        deleteAttachments(taskId)
    }

    @Transaction
    suspend fun inboxInsertMessages(messageParts: List<MessagePart>, users: Set<User>) {
        insertMessageParts(messageParts)
        insertUsers(users)
    }

    @Transaction
    suspend fun aboutMessageInsertMessages(
        message: Message,
        replies: List<MessageReply>,
        users: Set<User>
    ) {
        insertMessage(message)
        insertReplies(replies)
        insertUsers(users)
    }

    @Transaction
    suspend fun aboutMessageDeleteMessages(messageId: Int) {
        deleteMessage(messageId)
        deleteReplies(messageId)
    }

    @Transaction
    suspend fun updateTaskTitles(title: String, taskId: Int) {
        updateTaskTitle(title, taskId)
        updateTaskPartTitle(title, taskId)
    }

    @Transaction
    suspend fun updateTaskDescriptions(description: String, taskId: Int) {
        updateTaskDescription(description, taskId)
        updateTaskPartDescription(description, taskId)
    }

    @Transaction
    suspend fun updateTaskStatuses(status: String, taskId: Int) {
        updateTaskStatus(status, taskId)
        updateTaskPartStatus(status, taskId)
    }

    @Transaction
    suspend fun updateTaskPriorities(priority: String, taskId: Int) {
        updateTaskPriority(priority, taskId)
        updateTaskPartPriority(priority, taskId)
    }

    @Transaction
    suspend fun updateTaskTypes(type: String, taskId: Int) {
        updateTaskType(type, taskId)
        updateTaskPartType(type, taskId)
    }

    @Transaction
    suspend fun updateTaskDues(due: String, taskId: Int) {
        updateTaskDue(due, taskId)
        updateTaskPartDue(due, taskId)
    }

    @Transaction
    suspend fun updateTaskAssigneesId(assigneesId: String, taskId: Int) {
        updateTaskAssignees(assigneesId, taskId)
        updateTaskPartAssignees(assigneesId, taskId)
    }

    @Transaction
    suspend fun addComment(comments: List<Comment>, users: Set<User>) {
        insertComments(comments)
        insertUsers(users)
    }

    @Transaction
    suspend fun addSubtask(subtasks: List<Subtask>, users: Set<User>) {
        insertSubtasks(subtasks)
        insertUsers(users)
    }

    @Transaction
    suspend fun addChecklist(checklists: List<Checklist>, users: Set<User>) {
        insertChecklists(checklists)
        insertUsers(users)
    }

    @Transaction
    suspend fun addAttachment(attachments: List<Attachment>, users: Set<User>) {
        insertAttachments(attachments)
        insertUsers(users)
    }

    @Transaction
    suspend fun updateCommentIdInTask(commentId: Int, taskId: Int, isAdd: Boolean = true) {
        val commentsId = if (isAdd) {
            getTaskCommentsId(taskId).first().ids + commentId
        } else {
            getTaskCommentsId(taskId).first().ids - commentId
        }
        updateTaskComments(commentsId.joinToString(","), taskId)
    }

    @Transaction
    suspend fun updateSubtaskIdInTask(subtaskId: Int, taskId: Int, isAdd: Boolean = true) {
        val subtasksId = if (isAdd) {
            getTaskSubtasksId(taskId).first().ids + subtaskId
        } else {
            getTaskSubtasksId(taskId).first().ids - subtaskId
        }
        updateTaskSubtasks(subtasksId.joinToString(","), taskId)
    }

    @Transaction
    suspend fun updateChecklistIdInTask(checklistId: Int, taskId: Int, isAdd: Boolean = true) {
        val checklistsId = if (isAdd) {
            getTaskChecklistsId(taskId).first().ids + checklistId
        } else {
            getTaskChecklistsId(taskId).first().ids - checklistId
        }
        updateTaskChecklists(checklistsId.joinToString(","), taskId)
    }

    @Transaction
    suspend fun updateAttachmentIdInTask(attachmentId: Int, taskId: Int, isAdd: Boolean = true) {
        val attachmentsId = if (isAdd) {
            getTaskAttachmentsId(taskId).first().ids + attachmentId
        } else {
            getTaskAttachmentsId(taskId).first().ids - attachmentId
        }
        updateTaskAttachments(attachmentsId.joinToString(","), taskId)
    }

    @Transaction
    suspend fun updateReplyIdInMessage(replyId: Int, messageId: Int, isAdd: Boolean = true) {
        val repliesId = if (isAdd) {
            getMessageRepliesId(messageId).first().ids + replyId
        } else {
            getMessageRepliesId(messageId).first().ids - replyId
        }
        updateMessageReplies(repliesId.joinToString(","), messageId)
    }

    @Transaction
    suspend fun logout() {
        deleteAllTasks()
        deleteAllTaskParts()
        deleteAllComments()
        deleteAllSubtasks()
        deleteAllChecklists()
        deleteAllAttachments()
        deleteAllMessages()
        deleteAllMessageParts()
        deleteAllMessageReplies()
        deleteAllUsers()
        deleteAllProfileData()
    }
}