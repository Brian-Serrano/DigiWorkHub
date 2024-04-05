package com.serrano.dictproject.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.getUsers
import com.serrano.dictproject.room.toDTO
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.AboutTaskDialogs
import com.serrano.dictproject.utils.AboutTaskState
import com.serrano.dictproject.utils.AddAttachmentState
import com.serrano.dictproject.utils.AddChecklistState
import com.serrano.dictproject.utils.AddCommentState
import com.serrano.dictproject.utils.AddSubtaskState
import com.serrano.dictproject.utils.AttachmentDTO
import com.serrano.dictproject.utils.AttachmentState
import com.serrano.dictproject.utils.ChecklistBody
import com.serrano.dictproject.utils.ChecklistDTO
import com.serrano.dictproject.utils.ChecklistState
import com.serrano.dictproject.utils.CommentBody
import com.serrano.dictproject.utils.CommentDTO
import com.serrano.dictproject.utils.CommentState
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.LikeComment
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SubtaskAssigneeEdit
import com.serrano.dictproject.utils.SubtaskBody
import com.serrano.dictproject.utils.SubtaskDTO
import com.serrano.dictproject.utils.SubtaskDescriptionChange
import com.serrano.dictproject.utils.SubtaskDueChange
import com.serrano.dictproject.utils.SubtaskPriorityChange
import com.serrano.dictproject.utils.SubtaskState
import com.serrano.dictproject.utils.SubtaskStatusChange
import com.serrano.dictproject.utils.SubtaskTypeChange
import com.serrano.dictproject.utils.TaskDTO
import com.serrano.dictproject.utils.TaskState
import com.serrano.dictproject.utils.ToggleChecklist
import com.serrano.dictproject.utils.UserDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AboutTaskViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, dao, application) {

    private val _task = MutableStateFlow(TaskState())
    val task: StateFlow<TaskState> = _task.asStateFlow()

    private val _aboutTaskState = MutableStateFlow(AboutTaskState())
    val aboutTaskState: StateFlow<AboutTaskState> = _aboutTaskState.asStateFlow()

    private val _dialogState = MutableStateFlow(AboutTaskDialogs.NONE)
    val dialogState: StateFlow<AboutTaskDialogs> = _dialogState.asStateFlow()

    fun getTaskInfo(taskId: Int) {
        viewModelScope.launch {
            try {
                val localTask = dao.getTask(taskId).first()

                // if there is no task in the local storage load it from api
                if (localTask == null) {
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val response = apiRepository.getTask(taskId)) {
                        is Resource.Success -> {
                            val task = response.data!!

                            // assign response to the state
                            _task.value = mapToTaskState(task)

                            // convert fetched data to entity and save locally
                            storeInStorage(task)

                            mutableProcessState.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            mutableProcessState.value = ProcessState.Error(response.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            mutableProcessState.value = ProcessState.Error(response.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            mutableProcessState.value = ProcessState.Error(response.serverError?.error ?: "")
                        }
                    }
                } else {
                    // if task exist convert it to DTO and assign to the state
                    _task.value = mapToTaskState(localTask.toDTO(dao))

                    mutableProcessState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshTaskInfo(taskId: Int) {
        viewModelScope.launch {
            updateTask(_task.value.copy(isRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getTask(taskId),
                onSuccess = { task ->
                    // assign response to the state
                    _task.value = mapToTaskState(task)

                    // delete the data previously save
                    dao.aboutTaskDeleteTasks(taskId)

                    // convert fetched data to entity and save locally
                    storeInStorage(task)

                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    mutableProcessState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateTask(_task.value.copy(isRefreshing = false))
        }
    }

    fun updateDialogState(newDialogState: AboutTaskDialogs) {
        _dialogState.value = newDialogState
    }

    fun updateAddSubtaskState(newState: AddSubtaskState) {
        _aboutTaskState.value = _aboutTaskState.value.copy(addSubtaskState = newState)
    }

    fun updateAddCommentState(newState: AddCommentState) {
        _aboutTaskState.value = _aboutTaskState.value.copy(addCommentState = newState)
    }

    fun updateAddChecklistState(newState: AddChecklistState) {
        _aboutTaskState.value = _aboutTaskState.value.copy(addChecklistState = newState)
    }

    fun updateAddAttachmentState(newState: AddAttachmentState) {
        _aboutTaskState.value = _aboutTaskState.value.copy(addAttachmentState = newState)
    }

    fun updateConfirmDialogState(newState: ConfirmDialogState) {
        _aboutTaskState.value = _aboutTaskState.value.copy(confirmDialogState = newState)
    }

    fun updateTask(newTask: TaskState) {
        _task.value = newTask
    }

    fun sendComment() {
        viewModelScope.launch {
            updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = false))

            MiscUtils.apiAddWrapper(
                response = apiRepository.addCommentToTask(
                    CommentBody(
                        _aboutTaskState.value.addCommentState.description,
                        _task.value.taskId,
                        _aboutTaskState.value.addCommentState.reply,
                        _aboutTaskState.value.addCommentState.mentions.map { it.id }
                    )
                ),
                onSuccess = {
                    // make all inputs empty
                    updateAddCommentState(
                        _aboutTaskState.value.addCommentState.copy(
                            description = "",
                            reply = emptyList(),
                            mentions = emptyList()
                        )
                    )

                    // save the comment locally
                    dao.addComment(listOf(it.toEntity()), it.getUsers().toSet())
                    dao.updateCommentIdInTask(it.commentId, it.taskId)

                    // update the ui with the new comment
                    updateTask(
                        _task.value.copy(
                            comments = _task.value.comments + mapToCommentState(it)
                        )
                    )

                    MiscUtils.toast(getApplication(), "Comment Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = true))
        }
    }

    fun addChecklist() {
        viewModelScope.launch {
            updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = false))

            MiscUtils.apiAddWrapper(
                response = apiRepository.addChecklist(
                    ChecklistBody(
                        _task.value.taskId,
                        _aboutTaskState.value.addChecklistState.description,
                        _aboutTaskState.value.addChecklistState.assignees.map { it.id }
                    )
                ),
                onSuccess = {
                    // make all inputs empty
                    updateAddChecklistState(
                        _aboutTaskState.value.addChecklistState.copy(
                            description = "",
                            assignees = emptyList()
                        )
                    )

                    // save the checklist locally
                    dao.addChecklist(listOf(it.toEntity()), it.getUsers().toSet())
                    dao.updateChecklistIdInTask(it.checklistId, it.taskId)

                    // update the ui with the new checklist
                    updateTask(_task.value.copy(checklists = _task.value.checklists + mapToChecklistState(it)))

                    MiscUtils.toast(getApplication(), "Checklist Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = true))
        }
    }

    fun addSubtask() {
        viewModelScope.launch {
            updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = false))

            MiscUtils.apiAddWrapper(
                response = apiRepository.addSubtask(
                    SubtaskBody(
                        _task.value.taskId,
                        _aboutTaskState.value.addSubtaskState.description,
                        _aboutTaskState.value.addSubtaskState.priority,
                        _aboutTaskState.value.addSubtaskState.due,
                        _aboutTaskState.value.addSubtaskState.type,
                        _aboutTaskState.value.addSubtaskState.assignees.map { it.id }
                    )
                ),
                onSuccess = {
                    // make all inputs empty
                    updateAddSubtaskState(
                        _aboutTaskState.value.addSubtaskState.copy(
                            description = "",
                            priority = "LOW",
                            due = LocalDateTime.now(),
                            type = "TASK",
                            assignees = emptyList()
                        )
                    )

                    // save the subtask locally
                    dao.addSubtask(listOf(it.toEntity()), it.getUsers().toSet())
                    dao.updateSubtaskIdInTask(it.subtaskId, it.taskId)

                    // update the ui with the new subtask
                    updateTask(_task.value.copy(subtasks = _task.value.subtasks + mapToSubtaskState(it)))

                    MiscUtils.toast(getApplication(), "Subtask Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = true))
        }
    }

    fun uploadAttachment() {
        viewModelScope.launch {
            updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = false))

            val uri = _aboutTaskState.value.addAttachmentState.fileUri

            // check if user selected file
            if (uri != null) {
                val file = FileUtils.getFileFromUri(getApplication(), uri)
                val filePart = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                val taskId = MultipartBody.Part.createFormData("taskId", _task.value.taskId.toString())

                MiscUtils.apiAddWrapper(
                    response = apiRepository.uploadAttachment(filePart, taskId),
                    onSuccess = {
                        // make all inputs empty
                        updateAddAttachmentState(
                            _aboutTaskState.value.addAttachmentState.copy(
                                fileUri = null
                            )
                        )

                        // save the attachment locally
                        dao.addAttachment(listOf(it.toEntity()), it.getUsers().toSet())
                        dao.updateAttachmentIdInTask(it.attachmentId, it.taskId)

                        // update ui with the new attachment
                        updateTask(_task.value.copy(attachments = _task.value.attachments + mapToAttachmentState(it)))

                        MiscUtils.toast(getApplication(), "Attachment Uploaded.")
                    },
                    context = getApplication(),
                    preferencesRepository = preferencesRepository,
                    apiRepository = apiRepository
                )

                if (file.exists()) file.delete()
            } else {
                MiscUtils.toast(getApplication(), "No file selected")
            }

            updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = true))
        }
    }

    fun changeSubtaskDescription(subtaskId: Int, description: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskDescription(
                    SubtaskDescriptionChange(subtaskId, description)
                ),
                onSuccess = {
                    // update ui with the changed description
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(description = description) else it
                            }
                        )
                    )

                    // update description changed in storage
                    dao.updateSubtaskDescription(description, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun changeSubtaskPriority(subtaskId: Int, priority: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskPriority(
                    SubtaskPriorityChange(subtaskId, priority)
                ),
                onSuccess = {
                    // update ui with the changed priority
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(priority = priority) else it
                            }
                        )
                    )

                    // update priority changed in storage
                    dao.updateSubtaskPriority(priority, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun changeSubtaskDueDate(subtaskId: Int, due: LocalDateTime) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskDueDate(
                    SubtaskDueChange(subtaskId, due)
                ),
                onSuccess = {
                    // update ui with the changed due
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(due = due) else it
                            }
                        )
                    )

                    // update due changed in storage
                    dao.updateSubtaskDue(due.format(DateUtils.DATE_TIME_FORMATTER), subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun editSubtaskAssignees(subtaskId: Int, assignee: List<UserDTO>) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.editSubtaskAssignees(
                    SubtaskAssigneeEdit(subtaskId, assignee.map { it.id })
                ),
                onSuccess = {
                    // update ui with the changed assignee
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(assignees = assignee) else it
                            }
                        )
                    )

                    // update assignees changed in storage
                    dao.updateSubtaskAssignees(assignee.joinToString(",") { it.id.toString() }, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun changeSubtaskType(subtaskId: Int, type: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskType(
                    SubtaskTypeChange(subtaskId, type)
                ),
                onSuccess = {
                    // update ui with the changed type
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(type = type) else it
                            }
                        )
                    )

                    // update type changed in storage
                    dao.updateSubtaskType(type, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun changeSubtaskStatus(subtaskId: Int, status: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskStatus(
                    SubtaskStatusChange(subtaskId, status)
                ),
                onSuccess = {
                    // update ui with the changed status
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(status = status) else it
                            }
                        )
                    )

                    // update status changed in storage
                    dao.updateSubtaskStatus(status, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun toggleChecklist(checklistId: Int, check: Boolean) {
        viewModelScope.launch {
            updateToggleChecklistButton(checklistId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.toggleChecklist(ToggleChecklist(checklistId, check)),
                onSuccess = {
                    // update ui with the changed checklist
                    updateTask(
                        _task.value.copy(
                            checklists = _task.value.checklists.map {
                                if (it.checklistId == checklistId) it.copy(isChecked = check) else it
                            }
                        )
                    )

                    // update the checklist changes in storage
                    dao.toggleChecklist(check, checklistId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateToggleChecklistButton(checklistId, true)
        }
    }

    fun likeComment(currentUserId: Int, commentId: Int) {
        viewModelScope.launch {
            updateLikeCommentButton(commentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.likeComment(LikeComment(commentId)),
                onSuccess = {
                    // update ui with the liked comment
                    updateTask(
                        _task.value.copy(
                            comments = _task.value.comments.map { comment ->
                                if (comment.commentId == commentId) {
                                    comment.copy(
                                        likesId = if (comment.likesId.any { it == currentUserId }) {
                                            comment.likesId - currentUserId
                                        } else {
                                            comment.likesId + currentUserId
                                        }
                                    )
                                } else comment
                            }
                        )
                    )

                    // update the liked comment in storage
                    dao.likeComment(
                        likesId = _task.value.comments
                            .first { it.commentId == commentId }
                            .likesId
                            .joinToString(","),
                        commentId = commentId
                    )
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateLikeCommentButton(commentId, true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAttachment(fileName: String, fileServerName: String) {
        viewModelScope.launch {
            MiscUtils.downloadAttachment(fileName, fileServerName, getApplication(), preferencesRepository, apiRepository)
        }
    }

    fun deleteTask(taskId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            updateTask(_task.value.copy(deleteButtonEnabled = false))

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteTask(taskId),
                onSuccess = {
                    // delete the task that is shown in about task in the storage
                    dao.aboutTaskDeleteTasks(taskId)

                    // delete the task that is shown in dashboard in the storage
                    dao.deleteTaskPart(taskId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateTask(_task.value.copy(deleteButtonEnabled = true))
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            updateDeleteCommentButton(commentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteComment(commentId),
                onSuccess = {
                    // update comments ui with the removed comment
                    updateTask(
                        _task.value.copy(
                            comments = _task.value.comments.filter {
                                it.commentId != commentId
                            }
                        )
                    )

                    // remove the comment in local storage
                    dao.deleteComment(commentId)
                    dao.updateCommentIdInTask(commentId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateDeleteCommentButton(commentId, true)
        }
    }

    fun deleteSubtask(subtaskId: Int) {
        viewModelScope.launch {
            updateDeleteSubtaskButton(subtaskId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteSubtask(subtaskId),
                onSuccess = {
                    // update subtasks ui with the removed subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.filter {
                                it.subtaskId != subtaskId
                            }
                        )
                    )

                    // remove the subtask in local storage
                    dao.deleteSubtask(subtaskId)
                    dao.updateSubtaskIdInTask(subtaskId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateDeleteSubtaskButton(subtaskId, true)
        }
    }

    fun deleteChecklist(checklistId: Int) {
        viewModelScope.launch {
            updateDeleteChecklistButton(checklistId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteChecklist(checklistId),
                onSuccess = {
                    // update checklists ui with the removed checklist
                    updateTask(
                        _task.value.copy(
                            checklists = _task.value.checklists.filter {
                                it.checklistId != checklistId
                            }
                        )
                    )

                    // remove the checklist in local storage
                    dao.deleteChecklist(checklistId)
                    dao.updateChecklistIdInTask(checklistId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateDeleteChecklistButton(checklistId, true)
        }
    }

    fun deleteAttachment(attachmentId: Int) {
        viewModelScope.launch {
            updateDeleteAttachmentButton(attachmentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteAttachment(attachmentId),
                onSuccess = {
                    // update the attachments ui with the removed attachment
                    updateTask(
                        _task.value.copy(
                            attachments = _task.value.attachments.filter {
                                it.attachmentId != attachmentId
                            }
                        )
                    )

                    // remove the attachment in local storage
                    dao.deleteAttachment(attachmentId)
                    dao.updateAttachmentIdInTask(attachmentId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateDeleteAttachmentButton(attachmentId, true)
        }
    }

    private fun updateLikeCommentButton(commentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                comments = _task.value.comments.map {
                    if (it.commentId == commentId) it.copy(likeIconEnabled = value) else it
                }
            )
        )
    }

    private fun updateToggleChecklistButton(checklistId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                checklists = _task.value.checklists.map {
                    if (it.checklistId == checklistId) it.copy(checkButtonEnabled = value) else it
                }
            )
        )
    }

    private fun updateDeleteCommentButton(commentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                comments = _task.value.comments.map {
                    if (it.commentId == commentId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    private fun updateDeleteSubtaskButton(subtaskId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                subtasks = _task.value.subtasks.map {
                    if (it.subtaskId == subtaskId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    private fun updateDeleteChecklistButton(checklistId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                checklists = _task.value.checklists.map {
                    if (it.checklistId == checklistId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    private fun updateDeleteAttachmentButton(attachmentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                attachments = _task.value.attachments.map {
                    if (it.attachmentId == attachmentId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    private suspend fun storeInStorage(task: TaskDTO) {
        dao.aboutTaskInsertTasks(
            task = task.toEntity(),
            comments = task.comments.map { it.toEntity() },
            subtasks = task.subtasks.map { it.toEntity() },
            checklists = task.checklists.map { it.toEntity() },
            attachments = task.attachments.map { it.toEntity() },
            users = listOf(
                task.getUsers(),
                task.comments.flatMap { it.getUsers() },
                task.subtasks.flatMap { it.getUsers() },
                task.checklists.flatMap { it.getUsers() },
                task.attachments.flatMap { it.getUsers() }
            ).flatten().toSet()
        )
    }

    private fun mapToTaskState(task: TaskDTO): TaskState {
        return TaskState(
            taskId = task.taskId,
            title = task.title,
            description = task.description,
            due = task.due,
            priority = task.priority,
            status = task.status,
            type = task.type,
            sentDate = task.sentDate,
            assignees = task.assignees,
            creator = task.creator,
            comments = task.comments.map { mapToCommentState(it) },
            subtasks = task.subtasks.map { mapToSubtaskState(it) },
            checklists = task.checklists.map { mapToChecklistState(it) },
            attachments = task.attachments.map { mapToAttachmentState(it) },
            tabIndex = 0,
            isRefreshing = false,
            deleteButtonEnabled = true
        )
    }

    private fun mapToCommentState(comment: CommentDTO): CommentState {
        return CommentState(
            commentId = comment.commentId,
            taskId = comment.taskId,
            description = comment.description,
            replyId = comment.replyId,
            mentionsName = comment.mentionsName,
            user = comment.user,
            sentDate = comment.sentDate,
            likesId = comment.likesId,
            likeIconEnabled = true,
            deleteIconEnabled = true
        )
    }

    private fun mapToSubtaskState(subtask: SubtaskDTO): SubtaskState {
        return SubtaskState(
            subtaskId = subtask.subtaskId,
            taskId = subtask.taskId,
            description = subtask.description,
            due = subtask.due,
            priority = subtask.priority,
            status = subtask.status,
            type = subtask.type,
            assignees = subtask.assignees,
            creator = subtask.creator,
            deleteIconEnabled = true
        )
    }

    private fun mapToChecklistState(checklist: ChecklistDTO): ChecklistState {
        return ChecklistState(
            checklistId = checklist.checklistId,
            taskId = checklist.taskId,
            user = checklist.user,
            description = checklist.description,
            isChecked = checklist.isChecked,
            assignees = checklist.assignees,
            sentDate = checklist.sentDate,
            deleteIconEnabled = true,
            checkButtonEnabled = true
        )
    }

    private fun mapToAttachmentState(attachment: AttachmentDTO): AttachmentState {
        return AttachmentState(
            attachmentId = attachment.attachmentId,
            taskId = attachment.taskId,
            user = attachment.user,
            attachmentPath = attachment.attachmentPath,
            fileName = attachment.fileName,
            sentDate = attachment.sentDate,
            deleteIconEnabled = true
        )
    }
}