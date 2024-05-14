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

    /**
     * Get the task from the server or from the room database
     *
     * @param[taskId] What task to get
     */
    fun getTaskInfo(taskId: Int) {
        viewModelScope.launch {
            try {
                // get the task from room database
                val localTask = dao.getTask(taskId).first()

                // check if there are data got in the room database
                if (localTask == null) {
                    // if no data
                    // check authorization of user
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server for data
                    when (val response = apiRepository.getTask(taskId)) {
                        is Resource.Success -> {
                            // get the data of task and store in variable
                            val task = response.data!!

                            // assign to the task state the transformed TaskDTO Object to state
                            _task.value = mapToTaskState(task)

                            // store the task in the room database
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
                    // if task exist convert it to DTO and assign to the task state
                    _task.value = mapToTaskState(localTask.toDTO(dao))

                    mutableProcessState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh the data/task in user interface by requesting the server for task again
     *
     * @param[taskId] What task to get
     */
    fun refreshTaskInfo(taskId: Int) {
        viewModelScope.launch {
            // show the refresh icon of swipe refresh component
            updateTask(_task.value.copy(isRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getTask(taskId),
                onSuccess = { task ->
                    // assign to the task state the transformed TaskDTO Object to state
                    _task.value = mapToTaskState(task)

                    // delete the previous task saved in room database
                    dao.aboutTaskDeleteTasks(taskId)

                    // store the task in the room database
                    storeInStorage(task)

                    // show toast message
                    MiscUtils.toast(getApplication(), "Data loaded successfully")

                    mutableProcessState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide the refresh icon of swipe refresh component
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

    /**
     * Add comment to the task
     */
    fun sendComment() {
        viewModelScope.launch {
            // disable the send comment button
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
                    // this callback received CommentDTO (comment user added) as response from the server and can be added to room database or user interface
                    // make all comment inputs empty
                    updateAddCommentState(
                        _aboutTaskState.value.addCommentState.copy(
                            description = "",
                            reply = emptyList(),
                            mentions = emptyList()
                        )
                    )

                    // save the comment in room database
                    dao.addComment(listOf(it.toEntity()), it.getUsers().toSet())

                    // add the comment id to the list of task comment ids
                    dao.updateCommentIdInTask(it.commentId, it.taskId)

                    // add the comment to the user interface
                    updateTask(
                        _task.value.copy(
                            comments = _task.value.comments + mapToCommentState(it)
                        )
                    )

                    // show toast message
                    MiscUtils.toast(getApplication(), "Comment Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the send comment button
            updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = true))
        }
    }

    /**
     * Add checklist to the task
     */
    fun addChecklist() {
        viewModelScope.launch {
            // disable the add checklist button
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
                    // this callback received ChecklistDTO (checklist user added) as response from the server and can be added to room database or user interface
                    // make all checklist inputs empty
                    updateAddChecklistState(
                        _aboutTaskState.value.addChecklistState.copy(
                            description = "",
                            assignees = emptyList()
                        )
                    )

                    // save the checklist in room database
                    dao.addChecklist(listOf(it.toEntity()), it.getUsers().toSet())

                    // add the checklist id to the list of task checklist ids
                    dao.updateChecklistIdInTask(it.checklistId, it.taskId)

                    // add the checklist to the user interface
                    updateTask(_task.value.copy(checklists = _task.value.checklists + mapToChecklistState(it)))

                    // show toast message
                    MiscUtils.toast(getApplication(), "Checklist Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the add checklist button
            updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = true))
        }
    }

    /**
     * Add subtask to the task
     */
    fun addSubtask() {
        viewModelScope.launch {
            // disable the add subtask button
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
                    // this callback received SubtaskDTO (subtask user added) as response from the server and can be added to room database or user interface
                    // make all subtask inputs empty
                    updateAddSubtaskState(
                        _aboutTaskState.value.addSubtaskState.copy(
                            description = "",
                            priority = "LOW",
                            due = LocalDateTime.now(),
                            type = "TASK",
                            assignees = emptyList()
                        )
                    )

                    // save the subtask in room database
                    dao.addSubtask(listOf(it.toEntity()), it.getUsers().toSet())

                    // add the subtask id to the list of task subtask ids
                    dao.updateSubtaskIdInTask(it.subtaskId, it.taskId)

                    // add the subtask in the user interface
                    updateTask(_task.value.copy(subtasks = _task.value.subtasks + mapToSubtaskState(it)))

                    // show toast message
                    MiscUtils.toast(getApplication(), "Subtask Added.")
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the add subtask button
            updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = true))
        }
    }

    /**
     * Upload attachment in the task
     */
    fun uploadAttachment() {
        viewModelScope.launch {
            // disable the upload attachment button
            updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = false))

            val uri = _aboutTaskState.value.addAttachmentState.fileUri

            // check if user selected file
            if (uri != null) {
                // create form data for the file that can be send to the server
                val file = FileUtils.getFileFromUri(getApplication(), uri)
                val filePart = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                val taskId = MultipartBody.Part.createFormData("taskId", _task.value.taskId.toString())

                // send the file to the server
                MiscUtils.apiAddWrapper(
                    response = apiRepository.uploadAttachment(filePart, taskId),
                    onSuccess = {
                        // this callback received AttachmentDTO (attachment information user added) as response from the server and can be added to room database or user interface
                        // make the picked file empty
                        updateAddAttachmentState(
                            _aboutTaskState.value.addAttachmentState.copy(
                                fileUri = null
                            )
                        )

                        // save the attachment information in room database
                        dao.addAttachment(listOf(it.toEntity()), it.getUsers().toSet())

                        // add the attachment id to the list of task attachment ids
                        dao.updateAttachmentIdInTask(it.attachmentId, it.taskId)

                        // add the attachment information in the user interface
                        updateTask(_task.value.copy(attachments = _task.value.attachments + mapToAttachmentState(it)))

                        // show toast message
                        MiscUtils.toast(getApplication(), "Attachment Uploaded.")
                    },
                    context = getApplication(),
                    preferencesRepository = preferencesRepository,
                    apiRepository = apiRepository
                )

                // delete the file temporarily created when converting uri to file
                if (file.exists()) file.delete()
            } else {
                MiscUtils.toast(getApplication(), "No file selected")
            }

            // enable the upload attachment button
            updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = true))
        }
    }

    /**
     * Change description of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[description] The new description
     */
    fun changeSubtaskDescription(subtaskId: Int, description: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskDescription(
                    SubtaskDescriptionChange(subtaskId, description)
                ),
                onSuccess = {
                    // update user interface with the changed description of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(description = description) else it
                            }
                        )
                    )

                    // update room database with the changed description of subtask
                    dao.updateSubtaskDescription(description, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change priority of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[priority] The new priority
     */
    fun changeSubtaskPriority(subtaskId: Int, priority: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskPriority(
                    SubtaskPriorityChange(subtaskId, priority)
                ),
                onSuccess = {
                    // update user interface with the changed priority of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(priority = priority) else it
                            }
                        )
                    )

                    // update room database with the changed priority of subtask
                    dao.updateSubtaskPriority(priority, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change due date of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[due] The new due date
     */
    fun changeSubtaskDueDate(subtaskId: Int, due: LocalDateTime) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskDueDate(
                    SubtaskDueChange(subtaskId, due)
                ),
                onSuccess = {
                    // update user interface with the changed due date of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(due = due) else it
                            }
                        )
                    )

                    // update room database with the changed due date of subtask
                    dao.updateSubtaskDue(due.format(DateUtils.DATE_TIME_FORMATTER), subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change assignees of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[assignee] The new assignees
     */
    fun editSubtaskAssignees(subtaskId: Int, assignee: List<UserDTO>) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.editSubtaskAssignees(
                    SubtaskAssigneeEdit(subtaskId, assignee.map { it.id })
                ),
                onSuccess = {
                    // update user interface with the changed assignee of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(assignees = assignee) else it
                            }
                        )
                    )

                    // update room database with the changed assignee of subtask
                    dao.updateSubtaskAssignees(assignee.joinToString(",") { it.id.toString() }, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change type of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[type] The new type
     */
    fun changeSubtaskType(subtaskId: Int, type: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskType(
                    SubtaskTypeChange(subtaskId, type)
                ),
                onSuccess = {
                    // update user interface with the changed type of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(type = type) else it
                            }
                        )
                    )

                    // update room database with the changed type of subtask
                    dao.updateSubtaskType(type, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change status of a subtask
     *
     * @param[subtaskId] What subtask to change
     * @param[status] The new status
     */
    fun changeSubtaskStatus(subtaskId: Int, status: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeSubtaskStatus(
                    SubtaskStatusChange(subtaskId, status)
                ),
                onSuccess = {
                    // update user interface with the changed status of subtask
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.map {
                                if (it.subtaskId == subtaskId) it.copy(status = status) else it
                            }
                        )
                    )

                    // update room database with the changed status of subtask
                    dao.updateSubtaskStatus(status, subtaskId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Check/Uncheck Checklist
     *
     * @param[checklistId] What checklist to check/uncheck
     * @param[check] true = Check, false = Uncheck
     */
    fun toggleChecklist(checklistId: Int, check: Boolean) {
        viewModelScope.launch {
            // disable the check button
            updateToggleChecklistButton(checklistId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.toggleChecklist(ToggleChecklist(checklistId, check)),
                onSuccess = {
                    // update user interface with the changed checklist
                    updateTask(
                        _task.value.copy(
                            checklists = _task.value.checklists.map {
                                if (it.checklistId == checklistId) it.copy(isChecked = check) else it
                            }
                        )
                    )

                    // update room database with the changed checklist
                    dao.toggleChecklist(check, checklistId)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the check button
            updateToggleChecklistButton(checklistId, true)
        }
    }

    /**
     * Like/Unlike Comment
     *
     * @param[currentUserId] The id of user liked/unliked. Will be used to check if the user id included in the list of likes id to perform which (like or unlike).
     * @param[commentId] What comment to like
     */
    fun likeComment(currentUserId: Int, commentId: Int) {
        viewModelScope.launch {
            // disable the like icon
            updateLikeCommentButton(commentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.likeComment(LikeComment(commentId)),
                onSuccess = {
                    // update user interface with the liked comment
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

                    // update room database with the liked comment
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

            // enable the like icon
            updateLikeCommentButton(commentId, true)
        }
    }

    /**
     * Get the attachment data from server and save to users device shared storage
     *
     * @param[fileName] The name of file when it was uploaded. It will be used as the name of the stored file with the date appended.
     * @param[fileServerName] The path of file in the server. Used to determine what file to download.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAttachment(fileName: String, fileServerName: String) {
        viewModelScope.launch {
            MiscUtils.downloadAttachment(fileName, fileServerName, getApplication(), preferencesRepository, apiRepository)
        }
    }

    /**
     * Delete the task
     *
     * @param[taskId] What task to delete
     * @param[navigate] Callback for navigation that will be invoked when the response is successful. Direct navigation should be done on user interface. Separation of concerns.
     */
    fun deleteTask(taskId: Int, navigate: () -> Unit) {
        viewModelScope.launch {
            // disable the delete button
            updateTask(_task.value.copy(deleteButtonEnabled = false))

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteTask(taskId),
                onSuccess = {
                    // delete the task that is shown in about task in the room database
                    dao.aboutTaskDeleteTasks(taskId)

                    // delete the task that is shown in dashboard in the room database
                    dao.deleteTaskPart(taskId)

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete button
            updateTask(_task.value.copy(deleteButtonEnabled = true))
        }
    }

    /**
     * Delete a comment of task
     *
     * @param[commentId] What comment to delete
     */
    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            // disable the delete comment button
            updateDeleteCommentButton(commentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteComment(commentId),
                onSuccess = {
                    // remove the comment in user interface
                    updateTask(
                        _task.value.copy(
                            comments = _task.value.comments.filter {
                                it.commentId != commentId
                            }
                        )
                    )

                    // remove the comment in room database
                    dao.deleteComment(commentId)

                    // remove the comment id from the list of task comment ids
                    dao.updateCommentIdInTask(commentId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete comment button
            updateDeleteCommentButton(commentId, true)
        }
    }

    /**
     * Delete a subtask of task
     *
     * @param[subtaskId] What subtask to delete
     */
    fun deleteSubtask(subtaskId: Int) {
        viewModelScope.launch {
            // disable the delete subtask button
            updateDeleteSubtaskButton(subtaskId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteSubtask(subtaskId),
                onSuccess = {
                    // remove the subtask in user interface
                    updateTask(
                        _task.value.copy(
                            subtasks = _task.value.subtasks.filter {
                                it.subtaskId != subtaskId
                            }
                        )
                    )

                    // remove the subtask in room database
                    dao.deleteSubtask(subtaskId)

                    // remove the subtask id from the list of task subtask ids
                    dao.updateSubtaskIdInTask(subtaskId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete subtask button
            updateDeleteSubtaskButton(subtaskId, true)
        }
    }

    /**
     * Delete a checklist of task
     *
     * @param[checklistId] What checklist to delete
     */
    fun deleteChecklist(checklistId: Int) {
        viewModelScope.launch {
            // disable the delete checklist button
            updateDeleteChecklistButton(checklistId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteChecklist(checklistId),
                onSuccess = {
                    // remove the checklist in user interface
                    updateTask(
                        _task.value.copy(
                            checklists = _task.value.checklists.filter {
                                it.checklistId != checklistId
                            }
                        )
                    )

                    // remove the checklist in room database
                    dao.deleteChecklist(checklistId)

                    // remove the checklist id from the list of task checklist ids
                    dao.updateChecklistIdInTask(checklistId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete checklist button
            updateDeleteChecklistButton(checklistId, true)
        }
    }

    /**
     * Delete an attachment of task
     *
     * @param[attachmentId] What attachment to delete
     */
    fun deleteAttachment(attachmentId: Int) {
        viewModelScope.launch {
            // disable the delete attachment button
            updateDeleteAttachmentButton(attachmentId, false)

            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteAttachment(attachmentId),
                onSuccess = {
                    // remove the attachment in user interface
                    updateTask(
                        _task.value.copy(
                            attachments = _task.value.attachments.filter {
                                it.attachmentId != attachmentId
                            }
                        )
                    )

                    // remove the attachment in room database
                    dao.deleteAttachment(attachmentId)

                    // remove the attachment id from the list of task attachment ids
                    dao.updateAttachmentIdInTask(attachmentId, _task.value.taskId, false)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // enable the delete attachment button
            updateDeleteAttachmentButton(attachmentId, true)
        }
    }

    /**
     * Enable/disable one like comment button from a list of like comment buttons
     */
    private fun updateLikeCommentButton(commentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                comments = _task.value.comments.map {
                    if (it.commentId == commentId) it.copy(likeIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Enable/disable one toggle checklist button from a list of toggle checklist buttons
     */
    private fun updateToggleChecklistButton(checklistId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                checklists = _task.value.checklists.map {
                    if (it.checklistId == checklistId) it.copy(checkButtonEnabled = value) else it
                }
            )
        )
    }

    /**
     * Enable/disable one delete comment button from a list of delete comment buttons
     */
    private fun updateDeleteCommentButton(commentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                comments = _task.value.comments.map {
                    if (it.commentId == commentId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Enable/disable one delete subtask button from a list of delete subtask buttons
     */
    private fun updateDeleteSubtaskButton(subtaskId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                subtasks = _task.value.subtasks.map {
                    if (it.subtaskId == subtaskId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Enable/disable one delete checklist button from a list of delete checklist buttons
     */
    private fun updateDeleteChecklistButton(checklistId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                checklists = _task.value.checklists.map {
                    if (it.checklistId == checklistId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Enable/disable one delete attachment button from a list of delete attachment buttons
     */
    private fun updateDeleteAttachmentButton(attachmentId: Int, value: Boolean) {
        updateTask(
            _task.value.copy(
                attachments = _task.value.attachments.map {
                    if (it.attachmentId == attachmentId) it.copy(deleteIconEnabled = value) else it
                }
            )
        )
    }

    /**
     * Store the task in room database
     */
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

    /**
     * Convert TaskDTO to TaskState
     */
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

    /**
     * Convert CommentDTO to CommentState
     */
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

    /**
     * Convert SubtaskDTO to SubtaskState
     */
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

    /**
     * Convert ChecklistDTO to ChecklistState
     */
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

    /**
     * Convert AttachmentDTO to AttachmentState
     */
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