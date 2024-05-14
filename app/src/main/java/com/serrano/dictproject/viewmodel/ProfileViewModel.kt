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
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.ProfileDialogs
import com.serrano.dictproject.utils.ProfileState
import com.serrano.dictproject.utils.Resource
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
class ProfileViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    private val dao: Dao,
    application: Application
): AndroidViewModel(application) {

    private val _user = MutableStateFlow(ProfileDataDTO())
    val user: StateFlow<ProfileDataDTO> = _user.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _dialogState = MutableStateFlow(ProfileDialogs.NONE)
    val dialogState: StateFlow<ProfileDialogs> = _dialogState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    /**
     * Get user information from server or in room database
     *
     * @param[userId] What user to get
     */
    fun getUser(userId: Int) {
        viewModelScope.launch {
            try {
                // get the information in room database
                val localUser = dao.getProfileData(userId).first()

                // check if information was found in room database
                if (localUser == null) {
                    // if information not found
                    // check authorization
                    MiscUtils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                    // request server
                    when (val user = apiRepository.getUser(userId)) {
                        is Resource.Success -> {
                            // assign the user info to the state
                            _user.value = user.data!!

                            // save the user info in room database
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
                    // if information was found assign the user info to the state
                    _user.value = localUser.toDTO()

                    _processState.value = ProcessState.Success
                }
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    /**
     * Refresh the user information, request server for new information
     */
    fun refreshUser(userId: Int) {
        viewModelScope.launch {
            // show refresh icon
            updateProfileState(_profileState.value.copy(isRefreshing = true))

            MiscUtils.apiAddWrapper(
                response = apiRepository.getUser(userId),
                onSuccess = { user ->
                    // assign the user info to the state
                    _user.value = user

                    // delete information previously save in room database
                    dao.deleteProfileData(userId)

                    // save the new information in room database
                    dao.insertProfile(user.toEntity())

                    // show successful message
                    MiscUtils.toast(getApplication(), "User loaded successfully.")

                    _processState.value = ProcessState.Success
                },
                context = getApplication(),
                preferencesRepository = preferencesRepository,
                apiRepository = apiRepository
            )

            // hide refresh icon
            updateProfileState(_profileState.value.copy(isRefreshing = false))
        }
    }

    /**
     * Change username (only users own information can do this action)
     *
     * @param[name] The new username
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
     * Change User Role (only users own information can do this action)
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
     * Change user image (only users own information can do this action)
     *
     * @param[image] The new image
     */
    fun uploadImage(image: ImageBitmap) {
        viewModelScope.launch {
            // make form data from image that can be send to the server
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

            // delete the temporary file created when converting the image to form data
            if (imageFile.exists()) imageFile.delete()
        }
    }

    fun updateProfileState(newState: ProfileState) {
        _profileState.value = newState
    }

    private fun updateUser(newUser: ProfileDataDTO) {
        _user.value = newUser
    }

    fun updateDialogState(newState: ProfileDialogs) {
        _dialogState.value = newState
    }
}