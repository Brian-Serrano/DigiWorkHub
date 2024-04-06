package com.serrano.dictproject.api

import com.serrano.dictproject.utils.AssigneeEdit
import com.serrano.dictproject.utils.AttachmentDTO
import com.serrano.dictproject.utils.ChecklistBody
import com.serrano.dictproject.utils.ChecklistDTO
import com.serrano.dictproject.utils.CommentBody
import com.serrano.dictproject.utils.CommentDTO
import com.serrano.dictproject.utils.DescriptionChange
import com.serrano.dictproject.utils.DueChange
import com.serrano.dictproject.utils.LikeComment
import com.serrano.dictproject.utils.Login
import com.serrano.dictproject.utils.MessageBody
import com.serrano.dictproject.utils.MessageDTO
import com.serrano.dictproject.utils.MessageIdBody
import com.serrano.dictproject.utils.MessagePartDTO
import com.serrano.dictproject.utils.MessageReplyDTO
import com.serrano.dictproject.utils.NameChange
import com.serrano.dictproject.utils.PasswordBody
import com.serrano.dictproject.utils.PriorityChange
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.ReplyBody
import com.serrano.dictproject.utils.SignUpSuccess
import com.serrano.dictproject.utils.Signup
import com.serrano.dictproject.utils.StatusChange
import com.serrano.dictproject.utils.SubtaskAssigneeEdit
import com.serrano.dictproject.utils.SubtaskBody
import com.serrano.dictproject.utils.SubtaskDTO
import com.serrano.dictproject.utils.SubtaskDescriptionChange
import com.serrano.dictproject.utils.SubtaskDueChange
import com.serrano.dictproject.utils.SubtaskPriorityChange
import com.serrano.dictproject.utils.SubtaskStatusChange
import com.serrano.dictproject.utils.SubtaskTypeChange
import com.serrano.dictproject.utils.Success
import com.serrano.dictproject.utils.TaskBody
import com.serrano.dictproject.utils.TaskDTO
import com.serrano.dictproject.utils.TaskPartDTO
import com.serrano.dictproject.utils.ToggleChecklist
import com.serrano.dictproject.utils.TypeChange
import com.serrano.dictproject.utils.Unauthorized
import com.serrano.dictproject.utils.UserDTO
import com.serrano.dictproject.utils.UserNameChange
import com.serrano.dictproject.utils.UserRoleChange
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @GET("/task_routes/get_tasks")
    suspend fun getTasks(): Response<List<TaskPartDTO>>

    @GET("/task_routes/get_task")
    suspend fun getTask(@Query("task_id") taskId: Int): Response<TaskDTO>

    @GET("/message_routes/get_sent_messages")
    suspend fun getSentMessages(): Response<List<MessagePartDTO>>

    @GET("/message_routes/get_received_messages")
    suspend fun getReceivedMessages(): Response<List<MessagePartDTO>>

    @GET("/message_routes/get_message")
    suspend fun getMessage(@Query("message_id") messageId: Int): Response<MessageDTO>

    @GET("/task_routes/get_created_tasks")
    suspend fun getCreatedTasks(): Response<List<TaskPartDTO>>

    @GET("/user_routes/search_users")
    suspend fun searchUsers(@Query("search_query") searchQuery: String): Response<List<UserDTO>>

    @GET("/attachment_routes/download_attachment")
    suspend fun downloadAttachment(@Query("attachment_name") attachmentName: String): Response<ResponseBody>

    @GET("/user_routes/get_user")
    suspend fun getUser(@Query("user_id") userId: Int): Response<ProfileDataDTO>

    @POST("/task_routes/add_task")
    suspend fun addTask(@Body taskBody: TaskBody): Response<TaskPartDTO>

    @POST("/comment_routes/add_comment_to_task")
    suspend fun addCommentToTask(@Body commentBody: CommentBody): Response<CommentDTO>

    @Multipart
    @POST("/message_routes/message_user")
    suspend fun messageUser(@Part file: List<MultipartBody.Part>, @Part("messageBody") messageBody: MessageBody): Response<MessagePartDTO>

    @POST("/task_routes/change_task_status")
    suspend fun changeTaskStatus(@Body statusChange: StatusChange): Response<Success>

    @POST("/task_routes/edit_assignees")
    suspend fun editAssignees(@Body assigneeEdit: AssigneeEdit): Response<Success>

    @POST("/task_routes/change_due_date")
    suspend fun changeDueDate(@Body dueChange: DueChange): Response<Success>

    @POST("/task_routes/change_priority")
    suspend fun changePriority(@Body priorityChange: PriorityChange): Response<Success>

    @POST("/task_routes/change_type")
    suspend fun changeType(@Body typeChange: TypeChange): Response<Success>

    @POST("/task_routes/change_name")
    suspend fun changeName(@Body nameChange: NameChange): Response<Success>

    @POST("/task_routes/change_description")
    suspend fun changeDescription(@Body descriptionChange: DescriptionChange): Response<Success>

    @POST("/subtask_routes/add_subtask")
    suspend fun addSubtask(@Body subtaskBody: SubtaskBody): Response<SubtaskDTO>

    @POST("/checklist_routes/add_checklist")
    suspend fun addChecklist(@Body checklistBody: ChecklistBody): Response<ChecklistDTO>

    @Multipart
    @POST("/attachment_routes/upload_attachment")
    suspend fun uploadAttachment(@Part file: MultipartBody.Part, @Part taskId: MultipartBody.Part): Response<AttachmentDTO>

    @Multipart
    @POST("/user_routes/upload_image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<Success>

    @POST("/subtask_routes/change_subtask_description")
    suspend fun changeSubtaskDescription(@Body subtaskDescriptionChange: SubtaskDescriptionChange): Response<Success>

    @POST("/subtask_routes/change_subtask_priority")
    suspend fun changeSubtaskPriority(@Body subtaskPriorityChange: SubtaskPriorityChange): Response<Success>

    @POST("/subtask_routes/change_subtask_due_date")
    suspend fun changeSubtaskDueDate(@Body subtaskDueChange: SubtaskDueChange): Response<Success>

    @POST("/subtask_routes/edit_subtask_assignees")
    suspend fun editSubtaskAssignees(@Body subtaskAssigneeEdit: SubtaskAssigneeEdit): Response<Success>

    @POST("/subtask_routes/change_subtask_type")
    suspend fun changeSubtaskType(@Body subtaskTypeChange: SubtaskTypeChange): Response<Success>

    @POST("/subtask_routes/change_subtask_status")
    suspend fun changeSubtaskStatus(@Body subtaskStatusChange: SubtaskStatusChange): Response<Success>

    @POST("/checklist_routes/toggle_checklist")
    suspend fun toggleChecklist(@Body toggleChecklist: ToggleChecklist): Response<Success>

    @POST("/comment_routes/like_comment")
    suspend fun likeComment(@Body likeComment: LikeComment): Response<Success>

    @POST("/user_routes/change_user_name")
    suspend fun changeUserName(@Body userNameChange: UserNameChange): Response<Success>

    @POST("/user_routes/change_user_role")
    suspend fun changeUserRole(@Body userRoleChange: UserRoleChange): Response<Success>

    @Multipart
    @POST("/message_routes/reply_to_message")
    suspend fun replyToMessage(@Part file: List<MultipartBody.Part>, @Part("replyBody") replyBody: ReplyBody): Response<MessageReplyDTO>

    @POST("/message_routes/delete_message_from_user")
    suspend fun deleteMessageFromUser(@Body messageIdBody: MessageIdBody): Response<Success>

    @POST("/user_routes/change_user_password")
    suspend fun changeUserPassword(@Body passwordBody: PasswordBody): Response<Success>

    @DELETE("/task_routes/delete_task")
    suspend fun deleteTask(@Query("task_id") taskId: Int): Response<Success>

    @DELETE("/comment_routes/delete_comment")
    suspend fun deleteComment(@Query("comment_id") commentId: Int): Response<Success>

    @DELETE("/subtask_routes/delete_subtask")
    suspend fun deleteSubtask(@Query("subtask_id") subtaskId: Int): Response<Success>

    @DELETE("/checklist_routes/delete_checklist")
    suspend fun deleteChecklist(@Query("checklist_id") checklistId: Int): Response<Success>

    @DELETE("/attachment_routes/delete_attachment")
    suspend fun deleteAttachment(@Query("attachment_id") attachmentId: Int): Response<Success>

    @DELETE("/message_routes/delete_message")
    suspend fun deleteMessage(@Query("message_id") messageId: Int): Response<Success>

    @DELETE("/message_routes/delete_message_reply")
    suspend fun deleteMessageReply(@Query("message_reply_id") messageReplyId: Int): Response<Success>

    @DELETE("/user_routes/delete_user")
    suspend fun deleteUser(): Response<Success>

}