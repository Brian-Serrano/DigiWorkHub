package com.serrano.dictproject.api

import com.serrano.dictproject.utils.AssigneeEdit
import com.serrano.dictproject.utils.Attachment
import com.serrano.dictproject.utils.Checklist
import com.serrano.dictproject.utils.ChecklistBody
import com.serrano.dictproject.utils.Comment
import com.serrano.dictproject.utils.CommentBody
import com.serrano.dictproject.utils.DescriptionChange
import com.serrano.dictproject.utils.DueChange
import com.serrano.dictproject.utils.LikeComment
import com.serrano.dictproject.utils.Login
import com.serrano.dictproject.utils.Message
import com.serrano.dictproject.utils.MessageBody
import com.serrano.dictproject.utils.MessagePart
import com.serrano.dictproject.utils.NameChange
import com.serrano.dictproject.utils.PriorityChange
import com.serrano.dictproject.utils.ProfileData
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SignUpSuccess
import com.serrano.dictproject.utils.Signup
import com.serrano.dictproject.utils.StatusChange
import com.serrano.dictproject.utils.Subtask
import com.serrano.dictproject.utils.SubtaskAssigneeEdit
import com.serrano.dictproject.utils.SubtaskBody
import com.serrano.dictproject.utils.SubtaskDescriptionChange
import com.serrano.dictproject.utils.SubtaskDueChange
import com.serrano.dictproject.utils.SubtaskPriorityChange
import com.serrano.dictproject.utils.SubtaskStatusChange
import com.serrano.dictproject.utils.SubtaskTypeChange
import com.serrano.dictproject.utils.Success
import com.serrano.dictproject.utils.Task
import com.serrano.dictproject.utils.TaskBody
import com.serrano.dictproject.utils.TaskPart
import com.serrano.dictproject.utils.ToggleChecklist
import com.serrano.dictproject.utils.TypeChange
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.UserNameChange
import com.serrano.dictproject.utils.UserRoleChange
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ApiRepository(
    private val apiService: ApiService,
    private val apiHandler: ApiHandler
) {

    suspend fun signup(signup: Signup): Resource<SignUpSuccess> {
        return apiHandler.handleApi { apiService.signup(signup) }
    }

    suspend fun login(login: Login): Resource<SignUpSuccess> {
        return apiHandler.handleApi { apiService.login(login) }
    }

    suspend fun getTasks(): Resource<List<TaskPart>> {
        return apiHandler.handleApi { apiService.getTasks() }
    }

    suspend fun getTask(taskId: Int): Resource<Task> {
        return apiHandler.handleApi { apiService.getTask(taskId) }
    }

    suspend fun getSentMessages(): Resource<List<MessagePart>> {
        return apiHandler.handleApi { apiService.getSentMessages() }
    }

    suspend fun getReceivedMessages(): Resource<List<MessagePart>> {
        return apiHandler.handleApi { apiService.getReceivedMessages() }
    }

    suspend fun getMessage(messageId: Int): Resource<Message> {
        return apiHandler.handleApi { apiService.getMessage(messageId) }
    }

    suspend fun getCreatedTasks(): Resource<List<TaskPart>> {
        return apiHandler.handleApi { apiService.getCreatedTasks() }
    }

    suspend fun searchUsers(searchQuery: String): Resource<List<User>> {
        return apiHandler.handleApi { apiService.searchUsers(searchQuery) }
    }

    suspend fun downloadAttachment(attachmentName: String): Resource<ResponseBody> {
        return apiHandler.handleApi { apiService.downloadAttachment(attachmentName) }
    }

    suspend fun getUser(userId: Int): Resource<ProfileData> {
        return apiHandler.handleApi { apiService.getUser(userId) }
    }

    suspend fun addTask(taskBody: TaskBody): Resource<Success> {
        return apiHandler.handleApi { apiService.addTask(taskBody) }
    }

    suspend fun addCommentToTask(commentBody: CommentBody): Resource<Comment> {
        return apiHandler.handleApi { apiService.addCommentToTask(commentBody) }
    }

    suspend fun messageUser(messageBody: MessageBody): Resource<Success> {
        return apiHandler.handleApi { apiService.messageUser(messageBody) }
    }

    suspend fun changeTaskStatus(statusChange: StatusChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeTaskStatus(statusChange) }
    }

    suspend fun editAssignees(assigneeEdit: AssigneeEdit): Resource<Success> {
        return apiHandler.handleApi { apiService.editAssignees(assigneeEdit) }
    }

    suspend fun changeDueDate(dueChange: DueChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeDueDate(dueChange) }
    }

    suspend fun changePriority(priorityChange: PriorityChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changePriority(priorityChange) }
    }

    suspend fun changeType(typeChange: TypeChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeType(typeChange) }
    }

    suspend fun changeName(nameChange: NameChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeName(nameChange) }
    }

    suspend fun changeDescription(descriptionChange: DescriptionChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeDescription(descriptionChange) }
    }

    suspend fun addSubtask(subtaskBody: SubtaskBody): Resource<Subtask> {
        return apiHandler.handleApi { apiService.addSubtask(subtaskBody) }
    }

    suspend fun addChecklist(checklistBody: ChecklistBody): Resource<Checklist> {
        return apiHandler.handleApi { apiService.addChecklist(checklistBody) }
    }

    suspend fun uploadAttachment(file: MultipartBody.Part, taskId: MultipartBody.Part): Resource<Attachment> {
        return apiHandler.handleApi { apiService.uploadAttachment(file, taskId) }
    }

    suspend fun uploadImage(file: MultipartBody.Part): Resource<Success> {
        return apiHandler.handleApi { apiService.uploadImage(file) }
    }

    suspend fun changeSubtaskDescription(subtaskDescriptionChange: SubtaskDescriptionChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeSubtaskDescription(subtaskDescriptionChange) }
    }

    suspend fun changeSubtaskPriority(subtaskPriorityChange: SubtaskPriorityChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeSubtaskPriority(subtaskPriorityChange) }
    }

    suspend fun changeSubtaskDueDate(subtaskDueChange: SubtaskDueChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeSubtaskDueDate(subtaskDueChange) }
    }

    suspend fun editSubtaskAssignees(subtaskAssigneeEdit: SubtaskAssigneeEdit): Resource<Success> {
        return apiHandler.handleApi { apiService.editSubtaskAssignees(subtaskAssigneeEdit) }
    }

    suspend fun changeSubtaskType(subtaskTypeChange: SubtaskTypeChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeSubtaskType(subtaskTypeChange) }
    }

    suspend fun changeSubtaskStatus(subtaskStatusChange: SubtaskStatusChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeSubtaskStatus(subtaskStatusChange) }
    }

    suspend fun toggleChecklist(toggleChecklist: ToggleChecklist): Resource<Success> {
        return apiHandler.handleApi { apiService.toggleChecklist(toggleChecklist) }
    }

    suspend fun likeComment(likeComment: LikeComment): Resource<Success> {
        return apiHandler.handleApi { apiService.likeComment(likeComment) }
    }

    suspend fun changeUserName(userNameChange: UserNameChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeUserName(userNameChange) }
    }

    suspend fun changeUserRole(userRoleChange: UserRoleChange): Resource<Success> {
        return apiHandler.handleApi { apiService.changeUserRole(userRoleChange) }
    }
}