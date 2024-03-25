package com.serrano.dictproject.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileData
import com.serrano.dictproject.utils.ProfileDialogs
import com.serrano.dictproject.utils.ProfileState
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.UserNameChange
import com.serrano.dictproject.utils.UserRoleChange
import com.serrano.dictproject.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    private val _user = MutableStateFlow(ProfileData())
    val user: StateFlow<ProfileData> = _user.asStateFlow()

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState.asStateFlow()

    private val _dialogState = MutableStateFlow(ProfileDialogs.NONE)
    val dialogState: StateFlow<ProfileDialogs> = _dialogState.asStateFlow()

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun getUser(userId: Int) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                when (val user = apiRepository.getUser(userId)) {
                    is Resource.Success -> {
                        _user.value = user.data!!
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
            } catch (e: Exception) {
                _processState.value = ProcessState.Error(e.message ?: "")
            }
        }
    }

    fun changeUserName(name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeUserName(UserNameChange(name))) {
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

    fun changeUserRole(role: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.changeUserRole(UserRoleChange(role))) {
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

    fun uploadImage(image: ImageBitmap, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val imageFile = Utils.bitmapToFile(image, getApplication())
                val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, imageFile.asRequestBody("images/*".toMediaTypeOrNull()))

                Utils.checkAuthentication(getApplication(), preferencesRepository, apiRepository)

                Toast.makeText(
                    getApplication(),
                    when (val response = apiRepository.uploadImage(imagePart)) {
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

    fun changePreferencesName(name: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.changeName(name)
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changePreferencesImage(image: String) {
        viewModelScope.launch {
            try {
                preferencesRepository.changeImage(image)
            } catch (e: Exception) {
                Toast.makeText(getApplication(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateProfileState(newState: ProfileState) {
        _profileState.value = newState
    }

    fun updateUser(newUser: ProfileData) {
        _user.value = newUser
    }

    fun updateDialogState(newState: ProfileDialogs) {
        _dialogState.value = newState
    }
}