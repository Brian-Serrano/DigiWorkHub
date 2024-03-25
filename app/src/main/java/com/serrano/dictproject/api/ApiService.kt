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
import com.serrano.dictproject.utils.Unauthorized
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.UserNameChange
import com.serrano.dictproject.utils.UserRoleChange
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @Unauthorized
    @POST("/auth_routes/sign_up")
    suspend fun signup(@Body signup: Signup): Response<SignUpSuccess>

    @Unauthorized
    @POST("/auth_routes/log_in")
    suspend fun login(@Body login: Login): Response<SignUpSuccess>

    @GET("/get_routes/get_tasks")
    suspend fun getTasks(): Response<List<TaskPart>>

    @GET("/get_routes/get_task")
    suspend fun getTask(@Query("task_id") taskId: Int): Response<Task>

    @GET("/get_routes/get_sent_messages")
    suspend fun getSentMessages(): Response<List<MessagePart>>

    @GET("/get_routes/get_received_messages")
    suspend fun getReceivedMessages(): Response<List<MessagePart>>

    @GET("/get_routes/get_message")
    suspend fun getMessage(@Query("message_id") messageId: Int): Response<Message>

    @GET("/get_routes/get_created_tasks")
    suspend fun getCreatedTasks(): Response<List<TaskPart>>

    @GET("/get_routes/search_users")
    suspend fun searchUsers(@Query("search_query") searchQuery: String): Response<List<User>>

    @GET("/get_routes/download_attachment")
    suspend fun downloadAttachment(@Query("attachment_name") attachmentName: String): Response<ResponseBody>

    @GET("/get_routes/get_user")
    suspend fun getUser(@Query("user_id") userId: Int): Response<ProfileData>

    @POST("/post_routes/add_task")
    suspend fun addTask(@Body taskBody: TaskBody): Response<Success>

    @POST("/post_routes/add_comment_to_task")
    suspend fun addCommentToTask(@Body commentBody: CommentBody): Response<Comment>

    @POST("/post_routes/message_user")
    suspend fun messageUser(@Body messageBody: MessageBody): Response<Success>

    @POST("/post_routes/change_task_status")
    suspend fun changeTaskStatus(@Body statusChange: StatusChange): Response<Success>

    @POST("/post_routes/edit_assignees")
    suspend fun editAssignees(@Body assigneeEdit: AssigneeEdit): Response<Success>

    @POST("/post_routes/change_due_date")
    suspend fun changeDueDate(@Body dueChange: DueChange): Response<Success>

    @POST("/post_routes/change_priority")
    suspend fun changePriority(@Body priorityChange: PriorityChange): Response<Success>

    @POST("/post_routes/change_type")
    suspend fun changeType(@Body typeChange: TypeChange): Response<Success>

    @POST("/post_routes/change_name")
    suspend fun changeName(@Body nameChange: NameChange): Response<Success>

    @POST("/post_routes/change_description")
    suspend fun changeDescription(@Body descriptionChange: DescriptionChange): Response<Success>

    @POST("/post_routes/add_subtask")
    suspend fun addSubtask(@Body subtaskBody: SubtaskBody): Response<Subtask>

    @POST("/post_routes/add_checklist")
    suspend fun addChecklist(@Body checklistBody: ChecklistBody): Response<Checklist>

    @Multipart
    @POST("/post_routes/upload_attachment")
    suspend fun uploadAttachment(@Part file: MultipartBody.Part, @Part taskId: MultipartBody.Part): Response<Attachment>

    @Multipart
    @POST("/post_routes/upload_image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<Success>

    @POST("/post_routes/change_subtask_description")
    suspend fun changeSubtaskDescription(@Body subtaskDescriptionChange: SubtaskDescriptionChange): Response<Success>

    @POST("/post_routes/change_subtask_priority")
    suspend fun changeSubtaskPriority(@Body subtaskPriorityChange: SubtaskPriorityChange): Response<Success>

    @POST("/post_routes/change_subtask_due_date")
    suspend fun changeSubtaskDueDate(@Body subtaskDueChange: SubtaskDueChange): Response<Success>

    @POST("/post_routes/edit_subtask_assignees")
    suspend fun editSubtaskAssignees(@Body subtaskAssigneeEdit: SubtaskAssigneeEdit): Response<Success>

    @POST("/post_routes/change_subtask_type")
    suspend fun changeSubtaskType(@Body subtaskTypeChange: SubtaskTypeChange): Response<Success>

    @POST("/post_routes/change_subtask_status")
    suspend fun changeSubtaskStatus(@Body subtaskStatusChange: SubtaskStatusChange): Response<Success>

    @POST("/post_routes/toggle_checklist")
    suspend fun toggleChecklist(@Body toggleChecklist: ToggleChecklist): Response<Success>

    @POST("/post_routes/like_comment")
    suspend fun likeComment(@Body likeComment: LikeComment): Response<Success>

    @POST("/post_routes/change_user_name")
    suspend fun changeUserName(@Body userNameChange: UserNameChange): Response<Success>

    @POST("/post_routes/change_user_role")
    suspend fun changeUserRole(@Body userRoleChange: UserRoleChange): Response<Success>

}