package com.serrano.dictproject.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.textfield.CustomTextField
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SignupState

@Composable
fun Signup(
    navController: NavController,
    signupState: SignupState,
    updateSignupState: (SignupState) -> Unit,
    signup: (() -> Unit) -> Unit,
    login: (() -> Unit) -> Unit
) {
    val navBuilder: NavOptionsBuilder.() -> Unit = {
        popUpTo(navController.graph.id) {
            inclusive = false
        }
    }

    val annotatedText = buildAnnotatedString {

        append(
            when (signupState.tab) {
                0 -> "Already have account? "
                1 -> "Don't have an account? "
                else -> throw IllegalStateException()
            }
        )

        pushStringAnnotation(
            tag = "SignUp",
            annotation = "SignUp"
        )

        withStyle(style = SpanStyle(color = Color.Blue)) {
            append(
                when (signupState.tab) {
                    0 -> "Log In"
                    1 -> "Sign Up"
                    else -> throw IllegalStateException()
                }
            )
        }

        pop()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = null)
            }
            Spacer(modifier = Modifier.fillMaxWidth())
        }
        when (signupState.tab) {
            0 -> {
                Text(
                    text = "SIGN UP",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = "Username",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.signupName,
                    onValueChange = {
                        if (it.length <= 20) {
                            updateSignupState(
                                signupState.copy(
                                    signupName = it,
                                    signupNameError = if (Regex(MiscUtils.NAME_PATTERN).matches(it)) "" else {
                                        "Username should only have alphanumeric, underscore and space characters."
                                    }
                                )
                            )
                        }
                    },
                    placeholderText = "Enter Username",
                    supportingText = {
                        Text(
                            text = signupState.signupNameError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.signupEmail,
                    onValueChange = {
                        if (it.length <= 40) {
                            updateSignupState(
                                signupState.copy(
                                    signupEmail = it,
                                    signupEmailError = if (Regex(MiscUtils.EMAIL_PATTERN).matches(it)) "" else {
                                        "Invalid Email Address"
                                    }
                                )
                            )
                        }
                    },
                    placeholderText = "Enter Email",
                    supportingText = {
                        Text(
                            text = signupState.signupEmailError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.signupPassword,
                    onValueChange = {
                        if (it.length <= 20) {
                            val message = MiscUtils.checkPassword(it, signupState.signupConfirmPassword)

                            updateSignupState(
                                signupState.copy(
                                    signupPassword = it,
                                    signupPasswordError = message,
                                    signupConfirmPasswordError = message
                                )
                            )
                        }
                    },
                    placeholderText = "Enter Password",
                    visualTransformation = if (signupState.signupPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateSignupState(
                                    signupState.copy(
                                        signupPasswordVisibility = !signupState.signupPasswordVisibility
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (signupState.signupPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = signupState.signupPasswordError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )
                Text(
                    text = "Confirm Password",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.signupConfirmPassword,
                    onValueChange = {
                        if (it.length <= 20) {
                            val message = MiscUtils.checkPassword(it, signupState.signupPassword)

                            updateSignupState(
                                signupState.copy(
                                    signupConfirmPassword = it,
                                    signupConfirmPasswordError = message,
                                    signupPasswordError = message
                                )
                            )
                        }
                    },
                    placeholderText = "Enter Confirm Password",
                    visualTransformation = if (signupState.signupConfirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateSignupState(
                                    signupState.copy(
                                        signupConfirmPasswordVisibility = !signupState.signupConfirmPasswordVisibility
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (signupState.signupConfirmPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = signupState.signupConfirmPasswordError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )

                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = "SignUp",
                            start = offset,
                            end = offset
                        ).firstOrNull().let {
                            updateSignupState(signupState.copy(tab = 1))
                        }
                    },
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                CustomButton(
                    text = "SIGN UP",
                    onClick = { signup { navController.navigate(Routes.DASHBOARD, navBuilder) } },
                    enabled = signupState.signupButtonEnabled
                )
            }
            1 -> {
                Text(
                    text = "LOGIN",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.loginEmail,
                    onValueChange = {
                        if (it.length <= 40) {
                            updateSignupState(
                                signupState.copy(
                                    loginEmail = it,
                                    loginEmailError = if (Regex(MiscUtils.EMAIL_PATTERN).matches(it)) "" else {
                                        "Invalid Email Address"
                                    }
                                )
                            )
                        }
                    },
                    placeholderText = "Enter Email",
                    supportingText = {
                        Text(
                            text = signupState.loginEmailError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(5.dp)
                )
                CustomTextField(
                    value = signupState.loginPassword,
                    onValueChange = {
                        if (it.length <= 20) {
                            updateSignupState(
                                signupState.copy(
                                    loginPassword = it,
                                    loginPasswordError = if (Regex(MiscUtils.PASSWORD_PATTERN).matches(it)) "" else {
                                        "Password should have at least one letter and number, and 8-20 characters."
                                    }

                                )
                            )
                        }
                    },
                    placeholderText = "Enter Password",
                    visualTransformation = if (signupState.loginPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateSignupState(
                                    signupState.copy(
                                        loginPasswordVisibility = !signupState.loginPasswordVisibility
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (signupState.loginPasswordVisibility) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = signupState.loginPasswordError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                )

                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = "SignUp",
                            start = offset,
                            end = offset
                        ).firstOrNull().let {
                            updateSignupState(signupState.copy(tab = 0))
                        }
                    },
                    modifier = Modifier.padding(5.dp),
                    style = MaterialTheme.typography.bodySmall
                )

                CustomButton(
                    text = "LOGIN",
                    onClick = { login { navController.navigate(Routes.DASHBOARD, navBuilder) } },
                    enabled = signupState.loginButtonEnabled
                )
            }
        }
        Text(
            text = signupState.errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(5.dp),
            color = MaterialTheme.colorScheme.error
        )
    }
}