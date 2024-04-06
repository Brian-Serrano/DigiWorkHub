package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.room.Dao
import com.serrano.dictproject.room.toDTO
import com.serrano.dictproject.room.toEntity
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.PasswordDialogState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.SettingsDialogs
import com.serrano.dictproject.utils.SettingsState
import com.serrano.dictproject.utils.UserNameChange
import com.serrano.dictproject.utils.UserRoleChange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _user = MutableStateFlow(ProfileDataDTO())
    val user: StateFlow<ProfileDataDTO> = _user.asStateFlow()

    private val _dialogState = MutableStateFlow(SettingsDialogs.NONE)
    val dialogState: StateFlow<SettingsDialogs> = _dialogState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    fun updateDialogState(newState: SettingsDialogs) {
        _dialogState.value = newState
    }

    fun updateSettingsState(newState: SettingsState) {
        _settingsState.value = newState
    }

    private fun updateUser(newState: ProfileDataDTO) {
        _user.value = newState
    }

    fun updateChangePasswordState(newState: PasswordDialogState) {
        _settingsState.value = _settingsState.value.copy(passwordDialogState = newState)
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                val userId = preferencesRepository.getData().first().id
                val localUser = dao.getProfileData(userId).first()

                if (localUser == null) {
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    when (val user = apiRepository.getUser(userId)) {
                        is Resource.Success -> {
                            _user.value = user.data!!

                            // save fetched data locally
                            dao.insertProfile(user.data.toEntity())

                            _processState.value = ProcessState.Success
                        }
                        is Resource.ClientError -> {
                            _processState.value = ProcessState.Error(user.clientError?.message ?: "")
                        }
                        is Resource.GenericError -> {
                            _processState.value = ProcessState.Error(user.genericError ?: "")
                        }
                        is Resource.ServerError -> {
                            _processState.value = ProcessState.Error(user.serverError?.error ?: "")
                        }
                    }
                } else {
                    _user.value = localUser.toDTO()

                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            updateSettingsState(_settingsState.value.copy(isRefreshing = true))

            val userId = preferencesRepository.getData().first().id

            MiscUtils.apiAddWrapper(
                response = apiRepository.getUser(userId),
                onSuccess = { user ->
                    _user.value = user

                    // delete the previously save data
                    dao.deleteProfileData(userId)

                    // save fetched data locally
                    dao.insertProfile(user.toEntity())

                    MiscUtils.toast(getApplication(), "User loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            updateSettingsState(_settingsState.value.copy(isRefreshing = false))
        }
    }

    fun changeUserName(name: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeUserName(UserNameChange(name)),
                onSuccess = {
                    // update ui with the changed name
                    updateUser(_user.value.copy(name = name))

                    // change the name in the preferences
                    preferencesRepository.changeName(name)

                    // change the name in local storage
                    dao.updateUserName(name, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun changeUserRole(role: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeUserRole(UserRoleChange(role)),
                onSuccess = {
                    // update ui with the changed role
                    updateUser(_user.value.copy(role = role))

                    // change the role in local storage
                    dao.updateUserRole(role, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    fun uploadImage(image: ImageBitmap) {
        viewModelScope.launch {
            val imageFile = FileUtils.bitmapToFile(image, getApplication())
            val imagePart = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                imageFile.asRequestBody("images/*".toMediaTypeOrNull())
            )

            MiscUtils.apiEditWrapper(
                response = apiRepository.uploadImage(imagePart),
                onSuccess = {
                    // update ui with the changed role
                    val encodedString = FileUtils.imageToEncodedString(image)
                    updateUser(_user.value.copy(image = encodedString))

                    // change the image in the preferences
                    preferencesRepository.changeImage(encodedString)

                    // change the image in local storage
                    dao.updateUserImage(encodedString, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            if (imageFile.exists()) imageFile.delete()
        }
    }
}