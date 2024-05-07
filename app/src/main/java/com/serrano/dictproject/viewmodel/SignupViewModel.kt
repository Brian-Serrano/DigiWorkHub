package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.ForgotChangePasswordBody
import com.serrano.dictproject.utils.ForgotPasswordBody
import com.serrano.dictproject.utils.Login
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.Signup
import com.serrano.dictproject.utils.SignupDialogs
import com.serrano.dictproject.utils.SignupState
import com.serrano.dictproject.utils.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    private val _signupState = MutableStateFlow(SignupState())
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    private val _dialogState = MutableStateFlow(SignupDialogs.NONE)
    val dialogState: StateFlow<SignupDialogs> = _dialogState.asStateFlow()

    fun updateSignupState(newSignupState: SignupState) {
        _signupState.value = newSignupState
    }

    fun updateSignupDialog(newState: SignupDialogs) {
        _dialogState.value = newState
    }

    fun updateConfirmDialog(newState: ConfirmDialogState) {
        _signupState.value = _signupState.value.copy(confirmDialogState = newState)
    }

    fun signup(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                updateSignupState(_signupState.value.copy(signupButtonEnabled = false))

                when (
                    val response = apiRepository.signup(
                        Signup(
                            _signupState.value.signupName,
                            _signupState.value.signupEmail,
                            _signupState.value.signupPassword,
                            _signupState.value.signupConfirmPassword,
                            Firebase.messaging.token.await()
                        )
                    )
                ) {
                    is Resource.Success -> {
                        val data = response.data!!
                        preferencesRepository.login(data.token, data.id, data.name, data.email, data.password, data.image)
                        MiscUtils.toast(getApplication(), "Created Account Successfully!")
                        updateSignupState(_signupState.value.copy(signupButtonEnabled = true))
                        navigate()
                    }
                    is Resource.ClientError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.clientError?.message ?: "",
                                signupButtonEnabled = true
                            )
                        )
                    }
                    is Resource.GenericError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.genericError ?: "",
                                signupButtonEnabled = true
                            )
                        )
                    }
                    is Resource.ServerError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.serverError?.error ?: "",
                                signupButtonEnabled = true
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                updateSignupState(
                    _signupState.value.copy(
                        errorMessage = e.message ?: "",
                        signupButtonEnabled = true
                    )
                )
            }
        }
    }

    fun login(navigate: () -> Unit) {
        viewModelScope.launch {
            try {
                updateSignupState(_signupState.value.copy(loginButtonEnabled = false))

                when (
                    val response = apiRepository.login(
                        Login(
                            _signupState.value.loginEmail,
                            _signupState.value.loginPassword,
                            Firebase.messaging.token.await()
                        )
                    )
                ) {
                    is Resource.Success -> {
                        val data = response.data!!
                        preferencesRepository.login(data.token, data.id, data.name, data.email, data.password, data.image)
                        MiscUtils.toast(getApplication(), "User Logged In Successfully!")
                        updateSignupState(_signupState.value.copy(loginButtonEnabled = true))
                        navigate()
                    }
                    is Resource.ClientError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.clientError?.message ?: "",
                                loginButtonEnabled = true
                            )
                        )
                    }
                    is Resource.GenericError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.genericError ?: "",
                                loginButtonEnabled = true
                            )
                        )
                    }
                    is Resource.ServerError -> {
                        updateSignupState(
                            _signupState.value.copy(
                                errorMessage = response.serverError?.error ?: "",
                                loginButtonEnabled = true
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                updateSignupState(
                    _signupState.value.copy(
                        errorMessage = e.message ?: "",
                        loginButtonEnabled = true
                    )
                )
            }
        }
    }

    fun forgotPassword() {
        viewModelScope.launch {
            val email = _signupState.value.loginEmail

            when {
                email.isEmpty() -> {
                    MiscUtils.toast(getApplication(), "Enter your email address")
                }
                !Regex(MiscUtils.EMAIL_PATTERN).containsMatchIn(email) -> {
                    MiscUtils.toast(getApplication(), "Invalid email")
                }
                else -> {
                    apiCallWrapper(
                        response = apiRepository.forgotPassword(ForgotPasswordBody(email)),
                        onSuccess = {
                            updateSignupDialog(SignupDialogs.FORGOT)
                            MiscUtils.toast(getApplication(), "A code was sent to your email.")
                        }
                    )
                }
            }
        }
    }

    fun changePassword() {
        viewModelScope.launch {
            apiCallWrapper(
                response = apiRepository.changePassword(
                    ForgotChangePasswordBody(
                        email = _signupState.value.loginEmail,
                        code = _signupState.value.forgotCode,
                        password = _signupState.value.forgotNewPassword,
                        confirmPassword = _signupState.value.forgotConfirmPassword
                    )
                ),
                onSuccess = {
                    MiscUtils.toast(getApplication(), "Password successfully changed.")
                }
            )
        }
    }

    private suspend fun apiCallWrapper(response: Resource<Success>, onSuccess: suspend () -> Unit) {
        try {
            MiscUtils.toast(
                getApplication(),
                when (response) {
                    is Resource.Success -> {
                        onSuccess()
                        response.data?.message
                    }

                    is Resource.ClientError -> response.clientError?.message
                    is Resource.GenericError -> response.genericError
                    is Resource.ServerError -> response.serverError?.error
                }
            )
        } catch (e: Exception) {
            MiscUtils.toast(getApplication(), e.message)
        }
    }
}