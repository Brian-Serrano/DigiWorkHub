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

    /**
     * Used in [dashboardInsertTasks]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskParts(taskParts: List<TaskPart>)

    /**
     * Used in [aboutTaskInsertTasks]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Used in [aboutTaskInsertTasks], [addComment]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<Comment>)

    /**
     * Used in [aboutTaskInsertTasks], [addSubtask]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<Subtask>)

    /**
     * Used in [aboutTaskInsertTasks], [addChecklist]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklists(checklists: List<Checklist>)

    /**
     * Used in [aboutTaskInsertTasks], [addAttachment]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachments: List<Attachment>)

    /**
     * Usages: Profile, Settings. Add the user information in ProfileData table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profileData: ProfileData)

    /**
     * Used in [inboxInsertMessages]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageParts(messageParts: List<MessagePart>)

    /**
     * Used in [aboutMessageInsertMessages]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    /**
     * Usages: About Message. Used in [aboutMessageInsertMessages]. Add replies in MessageReply table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<MessageReply>)

    /**
     * Usages: All Authorized Pages. Add the users (creator, assignee, sender, receiver) in User table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: Set<User>)

    /**
     * Usages: Dashboard. Get all created tasks
     */
    @Query("SELECT * FROM TaskPart WHERE creatorId = :creatorId")
    fun getCreatedTasks(creatorId: Int): Flow<List<TaskPart>>

    /**
     * Usages: Dashboard. Get all assigned tasks
     */
    @Query("SELECT * FROM TaskPart WHERE assigneesId LIKE :assigneeId")
    fun getTasks(assigneeId: String): Flow<List<TaskPart>>

    /**
     * Usages: About Task. Get the task to show
     */
    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    fun getTask(taskId: Int): Flow<Task?>

    /**
     * Usages: All Authorized Pages. Used for getting the assignees, creator, sender, receiver
     */
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    /**
     * Usages: About Task. Get a comment
     */
    @Query("SELECT * FROM Comment WHERE commentId = :commentId")
    fun getComment(commentId: Int): Flow<Comment>

    /**
     * Usages: About Task. Get a subtask
     */
    @Query("SELECT * FROM Subtask WHERE subtaskId = :subtaskId")
    fun getSubtask(subtaskId: Int): Flow<Subtask>

    /**
     * Usages: About Task. Get a checklist
     */
    @Query("SELECT * FROM Checklist WHERE checklistId = :checklistId")
    fun getChecklist(checklistId: Int): Flow<Checklist>

    /**
     * Usages: About Task. Get an attachment
     */
    @Query("SELECT * FROM Attachment WHERE attachmentId = :attachmentId")
    fun getAttachment(attachmentId: Int): Flow<Attachment>

    /**
     * Usages: Inbox. Get the partial messages information to show
     */
    @Query("SELECT * FROM MessagePart WHERE tag = :tag")
    fun getMessagePart(tag: String): Flow<List<MessagePart>>

    /**
     * Usages: About Message. Get a message
     */
    @Query("SELECT * FROM Message WHERE messageId = :messageId")
    fun getMessage(messageId: Int): Flow<Message?>

    /**
     * Usages: About Message. Get a message reply
     */
    @Query("SELECT * FROM MessageReply WHERE messageReplyId = :messageReplyId")
    fun getMessageReplies(messageReplyId: Int): Flow<MessageReply>

    /**
     * Usages: Settings, Profile. Get the user to show
     */
    @Query("SELECT * FROM ProfileData WHERE id = :id")
    fun getProfileData(id: Int): Flow<ProfileData?>

    /**
     * Used in [updateCommentIdInTask]
     */
    @Query("SELECT commentsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskCommentsId(taskId: Int): Flow<RoomIsDumb>

    /**
     * Used in [updateSubtaskIdInTask]
     */
    @Query("SELECT subtasksId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskSubtasksId(taskId: Int): Flow<RoomIsDumb>

    /**
     * Used in [updateChecklistIdInTask]
     */
    @Query("SELECT checklistsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskChecklistsId(taskId: Int): Flow<RoomIsDumb>

    /**
     * Used in [updateAttachmentIdInTask]
     */
    @Query("SELECT attachmentsId AS ids FROM Task WHERE taskId = :taskId")
    fun getTaskAttachmentsId(taskId: Int): Flow<RoomIsDumb>

    /**
     * Used in [updateReplyIdInMessage]
     */
    @Query("SELECT replies AS ids FROM Message WHERE messageId = :messageId")
    fun getMessageRepliesId(messageId: Int): Flow<RoomIsDumb>

    /**
     * Usages: Dashboard. Delete all the created tasks in TaskPart table
     */
    @Query("DELETE FROM TaskPart WHERE creatorId = :creatorId")
    suspend fun deleteCreatedTasks(creatorId: Int)

    /**
     * Usages: Dashboard. Delete all the assigned tasks in TaskPart table
     */
    @Query("DELETE FROM TaskPart WHERE assigneesId LIKE :assigneeId")
    suspend fun deleteTasks(assigneeId: String)

    /**
     * Usages: About Task. Delete task in TaskPart table
     */
    @Query("DELETE FROM TaskPart WHERE taskId = :taskId")
    suspend fun deleteTaskPart(taskId: Int)

    /**
     * Used in [aboutTaskDeleteTasks]
     */
    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    /**
     * Used in [aboutTaskDeleteTasks]
     */
    @Query("DELETE FROM Comment WHERE taskId = :taskId")
    suspend fun deleteComments(taskId: Int)

    /**
     * Usages: About Task. Delete comment in Comment table
     */
    @Query("DELETE FROM Comment WHERE commentId = :commentId")
    suspend fun deleteComment(commentId: Int)

    /**
     * Used in [aboutTaskDeleteTasks]
     */
    @Query("DELETE FROM Subtask WHERE taskId = :taskId")
    suspend fun deleteSubtasks(taskId: Int)

    /**
     * Usages: About Task. Delete subtask in Subtask table
     */
    @Query("DELETE FROM Subtask WHERE subtaskId = :subtaskId")
    suspend fun deleteSubtask(subtaskId: Int)

    /**
     * Used in [aboutTaskDeleteTasks]
     */
    @Query("DELETE FROM Checklist WHERE taskId = :taskId")
    suspend fun deleteChecklists(taskId: Int)

    /**
     * Usages: About Task. Delete checklist in Checklist table
     */
    @Query("DELETE FROM Checklist WHERE checklistId = :checklistId")
    suspend fun deleteChecklist(checklistId: Int)

    /**
     * Used in [aboutTaskDeleteTasks]
     */
    @Query("DELETE FROM Attachment WHERE taskId = :taskId")
    suspend fun deleteAttachments(taskId: Int)

    /**
     * Usages: About Task. Delete attachment in Attachment table
     */
    @Query("DELETE FROM Attachment WHERE attachmentId = :attachmentId")
    suspend fun deleteAttachment(attachmentId: Int)

    /**
     * Usages: Inbox. Delete messages in MessagePart table
     */
    @Query("DELETE FROM MessagePart WHERE tag = :tag")
    suspend fun deleteMessageParts(tag: String)

    /**
     * Usages: About Message, Inbox. Delete the message in MessagePart table
     */
    @Query("DELETE FROM MessagePart WHERE messageId = :messageId")
    suspend fun deleteMessagePart(messageId: Int)

    /**
     * Used in [aboutMessageDeleteMessages]
     */
    @Query("DELETE FROM Message WHERE messageId = :messageId")
    suspend fun deleteMessage(messageId: Int)

    /**
     * Used in [aboutMessageDeleteMessages]
     */
    @Query("DELETE FROM MessageReply WHERE messageId = :messageId")
    suspend fun deleteReplies(messageId: Int)

    /**
     * Usages: About Message. Delete the reply in MessageReply table
     */
    @Query("DELETE FROM MessageReply WHERE messageReplyId = :messageReplyId")
    suspend fun deleteReply(messageReplyId: Int)

    /**
     * Usages: Profile, Settings. Delete the user in ProfileData table
     */
    @Query("DELETE FROM ProfileData WHERE id = :id")
    suspend fun deleteProfileData(id: Int)

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Task")
    suspend fun deleteAllTasks()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM TaskPart")
    suspend fun deleteAllTaskParts()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Comment")
    suspend fun deleteAllComments()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Subtask")
    suspend fun deleteAllSubtasks()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Checklist")
    suspend fun deleteAllChecklists()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Attachment")
    suspend fun deleteAllAttachments()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM Message")
    suspend fun deleteAllMessages()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM MessagePart")
    suspend fun deleteAllMessageParts()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM MessageReply")
    suspend fun deleteAllMessageReplies()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM User")
    suspend fun deleteAllUsers()

    /**
     * Used in [logout]
     */
    @Query("DELETE FROM ProfileData")
    suspend fun deleteAllProfileData()

    /**
     * Used in [updateTaskTitles]
     */
    @Query("UPDATE Task SET title = :title WHERE taskId = :taskId")
    suspend fun updateTaskTitle(title: String, taskId: Int)

    /**
     * Used in [updateTaskTitles]
     */
    @Query("UPDATE TaskPart SET title = :title WHERE taskId = :taskId")
    suspend fun updateTaskPartTitle(title: String, taskId: Int)

    /**
     * Used in [updateTaskDescriptions]
     */
    @Query("UPDATE Task SET description = :description WHERE taskId = :taskId")
    suspend fun updateTaskDescription(description: String, taskId: Int)

    /**
     * Used in [updateTaskDescriptions]
     */
    @Query("UPDATE TaskPart SET description = :description WHERE taskId = :taskId")
    suspend fun updateTaskPartDescription(description: String, taskId: Int)

    /**
     * Used in [updateTaskStatuses]
     */
    @Query("UPDATE Task SET status = :status WHERE taskId = :taskId")
    suspend fun updateTaskStatus(status: String, taskId: Int)

    /**
     * Used in [updateTaskStatuses]
     */
    @Query("UPDATE TaskPart SET status = :status WHERE taskId = :taskId")
    suspend fun updateTaskPartStatus(status: String, taskId: Int)

    /**
     * Used in [updateTaskPriorities]
     */
    @Query("UPDATE Task SET priority = :priority WHERE taskId = :taskId")
    suspend fun updateTaskPriority(priority: String, taskId: Int)

    /**
     * Used in [updateTaskPriorities]
     */
    @Query("UPDATE TaskPart SET priority = :priority WHERE taskId = :taskId")
    suspend fun updateTaskPartPriority(priority: String, taskId: Int)

    /**
     * Used in [updateTaskTypes]
     */
    @Query("UPDATE Task SET type = :type WHERE taskId = :taskId")
    suspend fun updateTaskType(type: String, taskId: Int)

    /**
     * Used in [updateTaskTypes]
     */
    @Query("UPDATE TaskPart SET type = :type WHERE taskId = :taskId")
    suspend fun updateTaskPartType(type: String, taskId: Int)

    /**
     * Used in [updateTaskDues]
     */
    @Query("UPDATE Task SET due = :due WHERE taskId = :taskId")
    suspend fun updateTaskDue(due: String, taskId: Int)

    /**
     * Used in [updateTaskDues]
     */
    @Query("UPDATE TaskPart SET due = :due WHERE taskId = :taskId")
    suspend fun updateTaskPartDue(due: String, taskId: Int)

    /**
     * Used in [updateTaskAssigneesId]
     */
    @Query("UPDATE Task SET assigneesId = :assigneesId WHERE taskId = :taskId")
    suspend fun updateTaskAssignees(assigneesId: String, taskId: Int)

    /**
     * Used in [updateTaskAssigneesId]
     */
    @Query("UPDATE TaskPart SET assigneesId = :assigneesId WHERE taskId = :taskId")
    suspend fun updateTaskPartAssignees(assigneesId: String, taskId: Int)

    /**
     * Used in [updateCommentIdInTask]
     */
    @Query("UPDATE Task SET commentsId = :commentsId WHERE taskId = :taskId")
    suspend fun updateTaskComments(commentsId: String, taskId: Int)

    /**
     * Used in [updateSubtaskIdInTask]
     */
    @Query("UPDATE Task SET subtasksId = :subtasksId WHERE taskId = :taskId")
    suspend fun updateTaskSubtasks(subtasksId: String, taskId: Int)

    /**
     * Used in [updateChecklistIdInTask]
     */
    @Query("UPDATE Task SET checklistsId = :checklistsId WHERE taskId = :taskId")
    suspend fun updateTaskChecklists(checklistsId: String, taskId: Int)

    /**
     * Used in [updateAttachmentIdInTask]
     */
    @Query("UPDATE Task SET attachmentsId = :attachmentsId WHERE taskId = :taskId")
    suspend fun updateTaskAttachments(attachmentsId: String, taskId: Int)

    /**
     * Used in [updateReplyIdInMessage]
     */
    @Query("UPDATE Message SET replies = :replies WHERE messageId = :messageId")
    suspend fun updateMessageReplies(replies: String, messageId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET description = :description WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskDescription(description: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET priority = :priority WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskPriority(priority: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET due = :due WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskDue(due: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET assigneesId = :assigneesId WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskAssignees(assigneesId: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET type = :type WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskType(type: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Subtask SET status = :status WHERE subtaskId = :subtaskId")
    suspend fun updateSubtaskStatus(status: String, subtaskId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Checklist SET isChecked = :isChecked WHERE checklistId = :checklistId")
    suspend fun toggleChecklist(isChecked: Boolean, checklistId: Int)

    /**
     * Usages: About Task
     */
    @Query("UPDATE Comment SET likesId = :likesId WHERE commentId = :commentId")
    suspend fun likeComment(likesId: String, commentId: Int)

    /**
     * Usages: Settings, Profile. Change the name of user in ProfileData table
     */
    @Query("UPDATE ProfileData SET name = :name WHERE id = :id")
    suspend fun updateUserName(name: String, id: Int)

    /**
     * Usages: Settings, Profile. Change the role of user in ProfileData table
     */
    @Query("UPDATE ProfileData SET role = :role WHERE id = :id")
    suspend fun updateUserRole(role: String, id: Int)

    /**
     * Usages: Settings, Profile. Change the image of user in ProfileData table.
     */
    @Query("UPDATE ProfileData SET image = :image WHERE id = :id")
    suspend fun updateUserImage(image: String, id: Int)

    /**
     * Usages: About Task, Dashboard. Add partial task information (its assignees and creator) in TaskPart and User table
     */
    @Transaction
    suspend fun dashboardInsertTasks(taskParts: List<TaskPart>, users: Set<User>) {
        insertTaskParts(taskParts)
        insertUsers(users)
    }

    /**
     * Usages: About Task. Add the entire task information (including comments, subtasks, checklists and attachments) in User, Task, Comment, Subtask, Checklist and Attachment table
     */
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

    /**
     * Usages: About Task. Delete the task, its comments, subtasks, checklists and attachments in Task, Comment, Subtask, Checklist and Attachment table
     */
    @Transaction
    suspend fun aboutTaskDeleteTasks(taskId: Int) {
        deleteTask(taskId)
        deleteComments(taskId)
        deleteSubtasks(taskId)
        deleteChecklists(taskId)
        deleteAttachments(taskId)
    }

    /**
     * Usages: About Message, Inbox. Add partial message information (its users) in MessagePart and User table
     */
    @Transaction
    suspend fun inboxInsertMessages(messageParts: List<MessagePart>, users: Set<User>) {
        insertMessageParts(messageParts)
        insertUsers(users)
    }

    /**
     * Usages: About Message. Insert message, its replies and sender and receiver in Message, MessageReply and User table
     */
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

    /**
     * Usages: About Message, Inbox. Delete message and its replies in Message and MessageReply table
     */
    @Transaction
    suspend fun aboutMessageDeleteMessages(messageId: Int) {
        deleteMessage(messageId)
        deleteReplies(messageId)
    }

    /**
     * Usages: About Task, Dashboard. Update task title in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskTitles(title: String, taskId: Int) {
        updateTaskTitle(title, taskId)
        updateTaskPartTitle(title, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task description in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskDescriptions(description: String, taskId: Int) {
        updateTaskDescription(description, taskId)
        updateTaskPartDescription(description, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task status in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskStatuses(status: String, taskId: Int) {
        updateTaskStatus(status, taskId)
        updateTaskPartStatus(status, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task priority in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskPriorities(priority: String, taskId: Int) {
        updateTaskPriority(priority, taskId)
        updateTaskPartPriority(priority, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task type in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskTypes(type: String, taskId: Int) {
        updateTaskType(type, taskId)
        updateTaskPartType(type, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task due date in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskDues(due: String, taskId: Int) {
        updateTaskDue(due, taskId)
        updateTaskPartDue(due, taskId)
    }

    /**
     * Usages: About Task, Dashboard. Update task assignees in Task and TaskPart table
     */
    @Transaction
    suspend fun updateTaskAssigneesId(assigneesId: String, taskId: Int) {
        updateTaskAssignees(assigneesId, taskId)
        updateTaskPartAssignees(assigneesId, taskId)
    }

    /**
     * Usages: About Task. Add comments (its senders) to Comment and User table
     */
    @Transaction
    suspend fun addComment(comments: List<Comment>, users: Set<User>) {
        insertComments(comments)
        insertUsers(users)
    }

    /**
     * Usages: About Task. Add subtasks (its assignees and creator) to Subtask and User table
     */
    @Transaction
    suspend fun addSubtask(subtasks: List<Subtask>, users: Set<User>) {
        insertSubtasks(subtasks)
        insertUsers(users)
    }

    /**
     * Usages: About Task. Add checklists (its assignees and creator) to Checklist and User table
     */
    @Transaction
    suspend fun addChecklist(checklists: List<Checklist>, users: Set<User>) {
        insertChecklists(checklists)
        insertUsers(users)
    }

    /**
     * Usages: About Task. Add attachments (its uploader) to Attachment and User table
     */
    @Transaction
    suspend fun addAttachment(attachments: List<Attachment>, users: Set<User>) {
        insertAttachments(attachments)
        insertUsers(users)
    }

    /**
     * Usages: About Task. Update comment ids in task in Task table
     */
    @Transaction
    suspend fun updateCommentIdInTask(commentId: Int, taskId: Int, isAdd: Boolean = true) {
        val commentsId = if (isAdd) {
            getTaskCommentsId(taskId).first().ids + commentId
        } else {
            getTaskCommentsId(taskId).first().ids - commentId
        }
        updateTaskComments(commentsId.joinToString(","), taskId)
    }

    /**
     * Usages: About Task. Update subtask ids in task in Task table
     */
    @Transaction
    suspend fun updateSubtaskIdInTask(subtaskId: Int, taskId: Int, isAdd: Boolean = true) {
        val subtasksId = if (isAdd) {
            getTaskSubtasksId(taskId).first().ids + subtaskId
        } else {
            getTaskSubtasksId(taskId).first().ids - subtaskId
        }
        updateTaskSubtasks(subtasksId.joinToString(","), taskId)
    }

    /**
     * Usages: About Task. Update checklist ids in task in Task table
     */
    @Transaction
    suspend fun updateChecklistIdInTask(checklistId: Int, taskId: Int, isAdd: Boolean = true) {
        val checklistsId = if (isAdd) {
            getTaskChecklistsId(taskId).first().ids + checklistId
        } else {
            getTaskChecklistsId(taskId).first().ids - checklistId
        }
        updateTaskChecklists(checklistsId.joinToString(","), taskId)
    }

    /**
     * Usages: About Task. Update attachment ids in task in Task table
     */
    @Transaction
    suspend fun updateAttachmentIdInTask(attachmentId: Int, taskId: Int, isAdd: Boolean = true) {
        val attachmentsId = if (isAdd) {
            getTaskAttachmentsId(taskId).first().ids + attachmentId
        } else {
            getTaskAttachmentsId(taskId).first().ids - attachmentId
        }
        updateTaskAttachments(attachmentsId.joinToString(","), taskId)
    }

    /**
     * Usages: About Message. Update reply ids in message in Message table
     */
    @Transaction
    suspend fun updateReplyIdInMessage(replyId: Int, messageId: Int, isAdd: Boolean = true) {
        val repliesId = if (isAdd) {
            getMessageRepliesId(messageId).first().ids + replyId
        } else {
            getMessageRepliesId(messageId).first().ids - replyId
        }
        updateMessageReplies(repliesId.joinToString(","), messageId)
    }

    /**
     * Usages: Drawer. Clear all data in db
     */
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