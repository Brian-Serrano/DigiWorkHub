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
import com.serrano.dictproject.utils.PasswordBody
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

    /**
     * Get user information from server or in room database
     */
    fun getUser() {
        viewModelScope.launch {
            try {
                // get the information in room database
                val userId = preferencesRepository.getData().first().id
                val localUser = dao.getProfileData(userId).first()

                // check if the information found
                if (localUser == null) {
                    // if information not found
                    // check authorization
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server
                    when (val user = apiRepository.getUser(userId)) {
                        is Resource.Success -> {
                            // assign the information to the state
                            _user.value = user.data!!

                            // save the information in room database
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
                    // if information is found assign the information to the state
                    _user.value = localUser.toDTO()

                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh user information by requesting server
     */
    fun refreshUser() {
        viewModelScope.launch {
            // show refresh icon
            updateSettingsState(_settingsState.value.copy(isRefreshing = true))

            val userId = preferencesRepository.getData().first().id

            MiscUtils.apiAddWrapper(
                response = apiRepository.getUser(userId),
                onSuccess = { user ->
                    // assign the information to the state
                    _user.value = user

                    // delete the previously save information in room database
                    dao.deleteProfileData(userId)

                    // save the new information in room database
                    dao.insertProfile(user.toEntity())

                    // show success message
                    MiscUtils.toast(getApplication(), "User loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            updateSettingsState(_settingsState.value.copy(isRefreshing = false))
        }
    }

    /**
     * Change the user name
     *
     * @param[name] The new name
     */
    fun changeUserName(name: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeUserName(UserNameChange(name)),
                onSuccess = {
                    // update user interface with the changed name
                    updateUser(_user.value.copy(name = name))

                    // change the name in the preferences
                    preferencesRepository.changeName(name)

                    // change the name in room database
                    dao.updateUserName(name, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the user role
     *
     * @param[role] The new role
     */
    fun changeUserRole(role: String) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeUserRole(UserRoleChange(role)),
                onSuccess = {
                    // update user interface with the changed role
                    updateUser(_user.value.copy(role = role))

                    // change the role in room database
                    dao.updateUserRole(role, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Change the user image
     *
     * @param[image] The new image
     */
    fun uploadImage(image: ImageBitmap) {
        viewModelScope.launch {
            // create form data from image that can be send to the server
            val imageFile = FileUtils.bitmapToFile(image, getApplication())
            val imagePart = MultipartBody.Part.createFormData(
                "file",
                imageFile.name,
                imageFile.asRequestBody("images/*".toMediaTypeOrNull())
            )

            MiscUtils.apiEditWrapper(
                response = apiRepository.uploadImage(imagePart),
                onSuccess = {
                    // update user interface with the changed image
                    val encodedString = FileUtils.imageToEncodedString(image)
                    updateUser(_user.value.copy(image = encodedString))

                    // change the image in the preferences
                    preferencesRepository.changeImage(encodedString)

                    // change the image in room database
                    dao.updateUserImage(encodedString, preferencesRepository.getData().first().id)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // delete the temporary file created when creating form data from image
            if (imageFile.exists()) imageFile.delete()
        }
    }

    /**
     * Change the user password
     */
    fun changeUserPassword() {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.changeUserPassword(
                    PasswordBody(
                        _settingsState.value.passwordDialogState.currentPassword,
                        _settingsState.value.passwordDialogState.newPassword,
                        _settingsState.value.passwordDialogState.confirmPassword
                    )
                ),
                onSuccess = {
                    // change the password in the preferences
                    preferencesRepository.changePassword(_settingsState.value.passwordDialogState.newPassword)
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }

    /**
     * Delete the user account
     *
     * @param[navigate] Callback for navigation that will be invoked when the response is successful. Direct navigation should be done on user interface. Separation of concerns.
     */
    fun deleteAccount(navigate: () -> Unit) {
        viewModelScope.launch {
            MiscUtils.apiEditWrapper(
                response = apiRepository.deleteUser(),
                onSuccess = {
                    // clear preferences and room database
                    preferencesRepository.logout()
                    dao.logout()

                    // navigate
                    navigate()
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )
        }
    }
}