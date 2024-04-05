package com.serrano.dictproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.serrano.dictproject.api.ApiRepository
import com.serrano.dictproject.datastore.PreferencesRepository
import com.serrano.dictproject.utils.Login
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Resource
import com.serrano.dictproject.utils.Signup
import com.serrano.dictproject.utils.SignupState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val preferencesRepository: PreferencesRepository,
    application: Application
): AndroidViewModel(application) {

    private val _signupState = MutableStateFlow(SignupState())
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    fun updateSignupState(newSignupState: SignupState) {
        _signupState.value = newSignupState
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
                            _signupState.value.signupConfirmPassword
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
                            _signupState.value.loginPassword
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

}