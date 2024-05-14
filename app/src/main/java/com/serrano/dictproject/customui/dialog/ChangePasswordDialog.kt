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
import com.serrano.dictproject.utils.PasswordDialogState

/**
 * A dialog used for changing password in settings page
 *
 * @param[text] Header text for the dialog
 * @param[onDismissRequest] Invoked when the users does something that should close the dialog
 * @param[passwordDialogState] States for the input fields inside this dialog
 * @param[onCurrentPasswordChange] Change the current password input field state when user enters/deletes text
 * @param[onNewPasswordChange] Change the new password input field state when user enters/deletes text
 * @param[onConfirmPasswordChange] Change the confirm password input field state when user enters/deletes text
 * @param[updateCurrentPasswordVisibility] Toggle the visibility of current password field (text or dots)
 * @param[updateNewPasswordVisibility] Toggle the visibility of new password field (text or dots)
 * @param[updateConfirmPasswordVisibility] Toggle the visibility of confirm password field (text or dots)
 * @param[onApplyClick] Invoked when the user want to apply the changes (click apply button)
 */
@Composable
fun ChangePasswordDialog(
    text: String,
    onDismissRequest: () -> Unit,
    passwordDialogState: PasswordDialogState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    updateCurrentPasswordVisibility: (Boolean) -> Unit,
    updateNewPasswordVisibility: (Boolean) -> Unit,
    updateConfirmPasswordVisibility: (Boolean) -> Unit,
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
                    value = passwordDialogState.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    placeholderText = "Current Password",
                    visualTransformation = if (passwordDialogState.currentPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateCurrentPasswordVisibility(
                                    !passwordDialogState.currentPasswordVisibility
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (passwordDialogState.currentPasswordVisibility) {
                                    Icons.Filled.VisibilityOff
                                } else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                CustomTextField(
                    value = passwordDialogState.newPassword,
                    onValueChange = onNewPasswordChange,
                    placeholderText = "New Password",
                    visualTransformation = if (passwordDialogState.newPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateNewPasswordVisibility(
                                    !passwordDialogState.newPasswordVisibility
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (passwordDialogState.newPasswordVisibility) {
                                    Icons.Filled.VisibilityOff
                                } else Icons.Filled.Visibility,
                                contentDescription = null
                            )
                        }
                    }
                )
                CustomTextField(
                    value = passwordDialogState.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholderText = "Confirm Password",
                    visualTransformation = if (passwordDialogState.confirmPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                updateConfirmPasswordVisibility(
                                    !passwordDialogState.confirmPasswordVisibility
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (passwordDialogState.confirmPasswordVisibility) {
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