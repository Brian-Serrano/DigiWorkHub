package com.serrano.dictproject.viewmodel

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.AboutTaskDialogs
import com.serrano.dictproject.utils.AboutTaskState
import com.serrano.dictproject.utils.AddAttachmentState
import com.serrano.dictproject.utils.AddChecklistState
import com.serrano.dictproject.utils.AddCommentState
import com.serrano.dictproject.utils.AddSubtaskState
import com.serrano.dictproject.utils.ChecklistBody
import com.serrano.dictproject.utils.CommentBody
import com.serrano.dictproject.utils.LikeComment
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SubtaskAssigneeEdit
import com.serrano.dictproject.utils.SubtaskBody
import com.serrano.dictproject.utils.SubtaskDescriptionChange
import com.serrano.dictproject.utils.SubtaskDueChange
import com.serrano.dictproject.utils.SubtaskPriorityChange
import com.serrano.dictproject.utils.SubtaskStatusChange
import com.serrano.dictproject.utils.SubtaskTypeChange
import com.serrano.dictproject.utils.Task
import com.serrano.dictproject.utils.ToggleChecklist
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AboutTaskViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): BaseViewModel(apiRepository, preferencesRepository, application) {

    private val _task = MutableStateFlow(Task())
    val task: StateFlow<Task> = _task.asStateFlow()

    private val _aboutTaskState = MutableStateFlow(AboutTaskState())
    val aboutTaskState: StateFlow<AboutTaskState> = _aboutTaskState.asStateFlow()

    private val _dialogState = MutableStateFlow(AboutTaskDialogs.NONE)
    val dialogState: StateFlow<AboutTaskDialogs> = _dialogState.asStateFlow()

    fun getTaskInfo(taskId: Int) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val task = apiRepository.getTask(taskId)) {
                    is Resource.Success -> {
                        _task.value = task.data!!
                        updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonsEnabled = task.data.checklists.map { true }))
                        updateAddCommentState(_aboutTaskState.value.addCommentState.copy(likeIconsEnabled = task.data.comments.map { true }))
                        mutableProcessState.value = ProcessState.Success
                    }
                    is Resource.ClientError -> {
                        mutableProcessState.value = ProcessState.Error(task.clientError?.message ?: "")
                    }
                    is Resource.GenericError -> {
                        mutableProcessState.value = ProcessState.Error(task.genericError ?: "")
                    }
                    is Resource.ServerError -> {
                        mutableProcessState.value = ProcessState.Error(task.serverError?.error ?: "")
                    }
                }
            } catch (e: Exception) {
                mutableProcessState.value = ProcessState.Error(e.message ?: "")
            }
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

    fun updateTabIdx(newIdx: Int) {
        _aboutTaskState.value = _aboutTaskState.value.copy(tabIndex = newIdx)
    }

    fun updateTask(newTask: Task) {
        _task.value = newTask
    }

    fun sendComment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = false))

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (
                    val response = apiRepository.addCommentToTask(
                        CommentBody(
                            _aboutTaskState.value.addCommentState.description,
                            _task.value.taskId
                        )
                    )
                ) {
                    is Resource.Success -> {
                        onSuccess()
                        updateTask(_task.value.copy(comments = _task.value.comments + response.data!!))
                        updateAddCommentState(
                            _aboutTaskState.value.addCommentState.copy(
                                likeIconsEnabled = _aboutTaskState.value.addCommentState.likeIconsEnabled + true
                            )
                        )
                        Toast.makeText(getApplication(), "Comment Added.", Toast.LENGTH_LONG).show()
                    }
                    is Resource.ClientError -> {
                        Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.GenericError -> {
                        Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                    }
                    is Resource.ServerError -> {
                        Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                    }
                }

                updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = true))
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                updateAddCommentState(_aboutTaskState.value.addCommentState.copy(buttonEnabled = true))
            }
        }
    }

    fun addChecklist(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = false))

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (
                    val response = apiRepository.addChecklist(
                        ChecklistBody(
                            _task.value.taskId,
                            _aboutTaskState.value.addChecklistState.description,
                            _aboutTaskState.value.addChecklistState.assignees.map { it.id }
                        )
                    )
                ) {
                    is Resource.Success -> {
                        onSuccess()
                        updateTask(_task.value.copy(checklists = _task.value.checklists + response.data!!))
                        updateAddChecklistState(
                            _aboutTaskState.value.addChecklistState.copy(
                                buttonsEnabled = _aboutTaskState.value.addChecklistState.buttonsEnabled + true
                            )
                        )
                        Toast.makeText(getApplication(), "Checklist Added.", Toast.LENGTH_LONG).show()
                    }
                    is Resource.ClientError -> {
                        Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.GenericError -> {
                        Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                    }
                    is Resource.ServerError -> {
                        Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                    }
                }

                updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = true))
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                updateAddChecklistState(_aboutTaskState.value.addChecklistState.copy(buttonEnabled = true))
            }
        }
    }

    fun addSubtask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = false))

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (
                    val response = apiRepository.addSubtask(
                        SubtaskBody(
                            _task.value.taskId,
                            _aboutTaskState.value.addSubtaskState.description,
                            _aboutTaskState.value.addSubtaskState.priority,
                            _aboutTaskState.value.addSubtaskState.due,
                            _aboutTaskState.value.addSubtaskState.type,
                            _aboutTaskState.value.addSubtaskState.assignees.map { it.id }
                        )
                    )
                ) {
                    is Resource.Success -> {
                        onSuccess()
                        updateTask(_task.value.copy(subtasks = _task.value.subtasks + response.data!!))
                        Toast.makeText(getApplication(), "Subtask Added.", Toast.LENGTH_LONG).show()
                    }
                    is Resource.ClientError -> {
                        Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.GenericError -> {
                        Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                    }
                    is Resource.ServerError -> {
                        Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                    }
                }

                updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = true))
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                updateAddSubtaskState(_aboutTaskState.value.addSubtaskState.copy(buttonEnabled = true))
            }
        }
    }

    fun uploadAttachment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = false))
                val uri = _aboutTaskState.value.addAttachmentState.fileUri

                if (uri != null) {
                    val file = Utils.getFileFromUri(getApplication(), uri)
                    val filePart = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
                    val taskId = MultipartBody.Part.createFormData("taskId", _task.value.taskId.toString())

                    Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val response = apiRepository.uploadAttachment(filePart, taskId)) {
                        is Resource.Success -> {
                            onSuccess()
                            updateTask(_task.value.copy(attachments = _task.value.attachments + response.data!!))
                            Toast.makeText(getApplication(), "Attachment Uploaded.", Toast.LENGTH_LONG).show()
                        }
                        is Resource.ClientError -> {
                            Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                        }
                        is Resource.GenericError -> {
                            Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                        }
                        is Resource.ServerError -> {
                            Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(getApplication(), "No file selected", Toast.LENGTH_LONG).show()
                }

                updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = true))
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
                updateAddAttachmentState(_aboutTaskState.value.addAttachmentState.copy(buttonEnabled = true))
            }
        }
    }

    fun changeSubtaskDescription(subtaskId: Int, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.changeSubtaskDescription(
                            SubtaskDescriptionChange(subtaskId, description)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeSubtaskPriority(subtaskId: Int, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.changeSubtaskPriority(
                            SubtaskPriorityChange(subtaskId, priority)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeSubtaskDueDate(subtaskId: Int, due: LocalDateTime, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.changeSubtaskDueDate(
                            SubtaskDueChange(subtaskId, due)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun editSubtaskAssignees(subtaskId: Int, assignee: List<Int>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.editSubtaskAssignees(
                            SubtaskAssigneeEdit(subtaskId, assignee)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeSubtaskType(subtaskId: Int, type: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.changeSubtaskType(
                            SubtaskTypeChange(subtaskId, type)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeSubtaskStatus(subtaskId: Int, status: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (
                        val response = apiRepository.changeSubtaskStatus(
                            SubtaskStatusChange(subtaskId, status)
                        )
                    ) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun toggleChecklist(buttonIdx: Int, checklistId: Int, check: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddChecklistState(
                    _aboutTaskState.value.addChecklistState.copy(
                        buttonsEnabled = _aboutTaskState.value.addChecklistState.buttonsEnabled.mapIndexed { index, button ->
                            if (buttonIdx == index) false else button
                        }
                    )
                )

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.toggleChecklist(ToggleChecklist(checklistId, check))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()

                updateAddChecklistState(
                    _aboutTaskState.value.addChecklistState.copy(
                        buttonsEnabled = _aboutTaskState.value.addChecklistState.buttonsEnabled.mapIndexed { index, button ->
                            if (buttonIdx == index) true else button
                        }
                    )
                )
            } catch (e: Exception) {
                updateAddChecklistState(
                    _aboutTaskState.value.addChecklistState.copy(
                        buttonsEnabled = _aboutTaskState.value.addChecklistState.buttonsEnabled.mapIndexed { index, button ->
                            if (buttonIdx == index) true else button
                        }
                    )
                )
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun likeComment(iconIdx: Int, commentId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updateAddCommentState(
                    _aboutTaskState.value.addCommentState.copy(
                        likeIconsEnabled = _aboutTaskState.value.addCommentState.likeIconsEnabled.mapIndexed { index, button ->
                            if (iconIdx == index) false else button
                        }
                    )
                )

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.likeComment(LikeComment(commentId))) {
                        is Resource.Success -> {
                            onSuccess()
                            response.data?.message
                        }
                        is Resource.ClientError -> response.clientError?.message
                        is Resource.GenericError -> response.genericError
                        is Resource.ServerError -> response.serverError?.error
                    },
                    Toast.LENGTH_LONG
                ).show()

                updateAddCommentState(
                    _aboutTaskState.value.addCommentState.copy(
                        likeIconsEnabled = _aboutTaskState.value.addCommentState.likeIconsEnabled.mapIndexed { index, button ->
                            if (iconIdx == index) true else button
                        }
                    )
                )
            } catch (e: Exception) {
                updateAddCommentState(
                    _aboutTaskState.value.addCommentState.copy(
                        likeIconsEnabled = _aboutTaskState.value.addCommentState.likeIconsEnabled.mapIndexed { index, button ->
                            if (iconIdx == index) true else button
                        }
                    )
                )
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun downloadAttachment(fileName: String, fileServerName: String) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val response = apiRepository.downloadAttachment(fileServerName.split("/").last())) {
                    is Resource.Success -> {
                        val fileNameAndExtension = fileName.split(".")
                        Utils.saveFileToDevice(getApplication(), response.data!!, fileNameAndExtension.first(), fileNameAndExtension.last())
                        Toast.makeText(getApplication(), "Downloading file...", Toast.LENGTH_LONG).show()
                    }
                    is Resource.ClientError -> {
                        Toast.makeText(getApplication(), response.clientError?.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.GenericError -> {
                        Toast.makeText(getApplication(), response.genericError, Toast.LENGTH_LONG).show()
                    }
                    is Resource.ServerError -> {
                        Toast.makeText(getApplication(), response.serverError?.error, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}