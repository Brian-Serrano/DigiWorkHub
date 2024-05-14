package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.customui.textfield.CustomTextField

/**
 * A dialog used for changing password in signup page
 *
 * @param[code] The text in code input field
 * @param[newPassword] The text in new password input field
 * @param[confirmPassword] The text in confirm password input field
 * @param[newPasswordVisibility] Visibility for new password field (text or dots)
 * @param[confirmPasswordVisibility] Visibility for confirm password field (text or dots)
 * @param[onCodeChange] Changes text in code field when user enter/delete text
 * @param[onNewPasswordChange] Changes text in new password field when user enter/delete text
 * @param[onConfirmPasswordChange] Changes text in confirm password field when user enter/delete text
 * @param[updateNewPasswordVisibility] Toggles visibility for new password field (text or dots)
 * @param[updateConfirmPasswordVisibility] Toggles visibility for confirm password field (text or dots)
 * @param[text] Header text for the dialog
 * @param[onDismissRequest] Invoked when the users does something that should close the dialog
 * @param[onApplyClick] Invoked when the user want to apply the changes (click apply button)
 */
@Composable
fun ForgotPasswordDialog(
    code: String,
    newPassword: String,
    confirmPassword: String,
    newPasswordVisibility: Boolean,
    confirmPasswordVisibility: Boolean,
    onCodeChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    updateNewPasswordVisibility: (Boolean) -> Unit,
    updateConfirmPasswordVisibility: (Boolean) -> Unit,
    text: String,
    onDismissRequest: () -> Unit,
    onApplyClick: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(IntrinsicSize.Min)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OneLineText(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                CustomTextField(
                    value = code,
                    onValueChange = onCodeChange,
                    placeholderText = "Code"
                )
                CustomTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    placeholderText = "New Password",
                    visualTransformation = if (newPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateNewPasswordVisibility(
                                    !newPasswordVisibility
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (newPasswordVisibility) {
                                    Icons.Filled.VisibilityOff
                                } else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                CustomTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholderText = "Confirm Password",
                    visualTransformation = if (confirmPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateConfirmPasswordVisibility(
                                    !confirmPasswordVisibility
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (confirmPasswordVisibility) {
                                    Icons.Filled.VisibilityOff
                                } else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            onDismissRequest()
                            onApplyClick()
                        }
                    )
                    CustomButton(
                        text = "CANCEL",
                        onClick = onDismissRequest
                    )
                }
            }
        }
    }
}