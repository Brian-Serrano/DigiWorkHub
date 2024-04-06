package com.serrano.dictproject.activity

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.dialog.ChangePasswordDialog
import com.serrano.dictproject.customui.dialog.ConfirmDialog
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.UploadImageDialog
import com.serrano.dictproject.customui.menu.InfoLine
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.PasswordDialogState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SettingsDialogs
import com.serrano.dictproject.utils.SettingsState

@Composable
fun Settings(
    navController: NavController,
    paddingValues: PaddingValues,
    context: Context,
    user: ProfileDataDTO,
    process: ProcessState,
    settingsDialogs: SettingsDialogs,
    settingsState: SettingsState,
    updateDialogState: (SettingsDialogs) -> Unit,
    updateSettingsState: (SettingsState) -> Unit,
    updateChangePasswordState: (PasswordDialogState) -> Unit,
    changeUserName: (String) -> Unit,
    changeUserRole: (String) -> Unit,
    uploadImage: (ImageBitmap) -> Unit,
    refreshUser: () -> Unit
) {
    val removeDialog = { updateDialogState(SettingsDialogs.NONE) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = settingsState.isRefreshing)

    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message, swipeRefreshState, refreshUser)
        }
        is ProcessState.Success -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SwipeRefresh(state = swipeRefreshState, onRefresh = refreshUser) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .border(
                                    BorderStroke(width = 2.dp, Color.Black),
                                    MaterialTheme.shapes.extraSmall
                                )
                        ) {
                            OneLineText(
                                text = "YOUR INFO",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(10.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OneLineText(
                                    text = "Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(5.dp)
                                )
                                IconButton(
                                    onClick = {
                                        updateSettingsState(
                                            settingsState.copy(
                                                image = FileUtils.encodedStringToImage(user.image)
                                            )
                                        )
                                        updateDialogState(SettingsDialogs.IMAGE)
                                    },
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(75.dp)
                                ) {
                                    Icon(
                                        bitmap = FileUtils.encodedStringToImage(user.image),
                                        contentDescription = null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(75.dp)
                                    )
                                }
                            }
                            Divider(
                                thickness = 1.dp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                            InfoLine(label = "Username", text = user.name) {
                                updateSettingsState(
                                    settingsState.copy(
                                        editNameDialogState = EditNameDialogState(user.name, 0)
                                    )
                                )
                                updateDialogState(SettingsDialogs.NAME)
                            }
                            Divider(
                                thickness = 1.dp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                            InfoLine(label = "Email", text = user.email)
                            Divider(
                                thickness = 1.dp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                            InfoLine(label = "Password", text = "********") {
                                updateSettingsState(
                                    settingsState.copy(
                                        passwordDialogState = PasswordDialogState()
                                    )
                                )
                                updateDialogState(SettingsDialogs.PASSWORD)
                            }
                            Divider(
                                thickness = 1.dp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 15.dp)
                            )
                            InfoLine(label = "Role", text = user.role) {
                                updateSettingsState(
                                    settingsState.copy(
                                        editNameDialogState = EditNameDialogState(user.role, 0)
                                    )
                                )
                                updateDialogState(SettingsDialogs.ROLE)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth()) {
                            CustomButton(
                                text = "DASHBOARD",
                                onClick = { navController.navigate(Routes.DASHBOARD) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            CustomButton(
                                text = "DELETE ACCOUNT",
                                onClick = {
                                    updateSettingsState(
                                        settingsState.copy(
                                            confirmDialogState = ConfirmDialogState(
                                                id = 0,
                                                placeholder = "account",
                                                onYesClick = {},
                                                onCancelClick = removeDialog
                                            )
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
                when (settingsDialogs) {
                    SettingsDialogs.NONE -> {  }
                    SettingsDialogs.NAME -> {
                        EditNameDialog(
                            text = "Name",
                            editNameDialogState = settingsState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, name -> changeUserName(name) },
                            onTextChange = {
                                updateSettingsState(
                                    settingsState.copy(
                                        editNameDialogState = settingsState.editNameDialogState.copy(
                                            name = it
                                        )
                                    )
                                )
                            }
                        )
                    }
                    SettingsDialogs.ROLE -> {
                        EditNameDialog(
                            text = "Role",
                            editNameDialogState = settingsState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, role -> changeUserRole(role) },
                            onTextChange = {
                                updateSettingsState(
                                    settingsState.copy(
                                        editNameDialogState = settingsState.editNameDialogState.copy(
                                            name = it
                                        )
                                    )
                                )
                            }
                        )
                    }
                    SettingsDialogs.IMAGE -> {
                        UploadImageDialog(
                            onDismissRequest = removeDialog,
                            onImagePicked = {
                                updateSettingsState(
                                    settingsState.copy(image = it)
                                )
                            },
                            onApplyClick = { image ->
                                if (image != null) {
                                    uploadImage(image)
                                } else {
                                    MiscUtils.toast(context, "No image selected")
                                }
                            },
                            image = settingsState.image,
                            context = context
                        )
                    }
                    SettingsDialogs.PASSWORD -> {
                        ChangePasswordDialog(
                            text = "Change Password",
                            onDismissRequest = removeDialog,
                            passwordDialogState = settingsState.passwordDialogState,
                            onCurrentPasswordChange = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        currentPassword = it
                                    )
                                )
                            },
                            onNewPasswordChange = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        newPassword = it
                                    )
                                )
                            },
                            onConfirmPasswordChange = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        confirmPassword = it
                                    )
                                )
                            },
                            updateCurrentPasswordVisibility = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        currentPasswordVisibility = it
                                    )
                                )
                            },
                            updateNewPasswordVisibility = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        newPasswordVisibility = it
                                    )
                                )
                            },
                            updateConfirmPasswordVisibility = {
                                updateChangePasswordState(
                                    settingsState.passwordDialogState.copy(
                                        confirmPasswordVisibility = it
                                    )
                                )
                            },
                            onApplyClick = {}
                        )
                    }
                    SettingsDialogs.CONFIRM -> {
                        ConfirmDialog(
                            id = settingsState.confirmDialogState.id,
                            placeholder = settingsState.confirmDialogState.placeholder,
                            onYesClick = settingsState.confirmDialogState.onYesClick,
                            onCancelClick = settingsState.confirmDialogState.onCancelClick
                        )
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun InfoPrev() {
    DICTProjectTheme(dynamicColor = false) {
        Settings(
            navController = rememberNavController(),
            paddingValues = PaddingValues(0.dp),
            context = LocalContext.current,
            user = ProfileDataDTO(),
            process = ProcessState.Success,
            settingsDialogs = SettingsDialogs.NONE,
            settingsState = SettingsState(),
            updateDialogState = {},
            updateSettingsState = {},
            updateChangePasswordState = {},
            changeUserName = {},
            changeUserRole = {},
            uploadImage = {},
            refreshUser = {}
        )
    }
}