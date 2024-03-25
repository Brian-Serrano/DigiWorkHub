package com.serrano.dictproject.activity

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.OneLineText
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.UploadImageDialog
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileData
import com.serrano.dictproject.utils.ProfileDialogs
import com.serrano.dictproject.utils.ProfileState
import com.serrano.dictproject.utils.Utils

@Composable
fun Profile(
    preferences: Preferences,
    navController: NavController,
    context: Context,
    paddingValues: PaddingValues,
    process: ProcessState,
    user: ProfileData,
    profileDialogs: ProfileDialogs,
    profileState: ProfileState,
    updateUser: (ProfileData) -> Unit,
    updateDialogState: (ProfileDialogs) -> Unit,
    updateProfileState: (ProfileState) -> Unit,
    changeUserName: (String, () -> Unit) -> Unit,
    changeUserRole: (String, () -> Unit) -> Unit,
    uploadImage: (ImageBitmap, () -> Unit) -> Unit,
    changePreferencesName: (String) -> Unit,
    changePreferencesImage: (String) -> Unit
) {
    val removeDialog = { updateDialogState(ProfileDialogs.NONE) }

    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message)
        }
        is ProcessState.Success -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (preferences.id == user.id) {
                                    updateProfileState(profileState.copy(image = Utils.encodedStringToImage(user.image)))
                                    updateDialogState(ProfileDialogs.IMAGE)
                                } else {
                                    Toast.makeText(context, "You can only edit your own image", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .size(100.dp)
                                .padding(10.dp)
                        ) {
                            Icon(
                                bitmap = Utils.encodedStringToImage(user.image),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OneLineText(
                                    text = user.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (preferences.id == user.id) {
                                    IconButton(
                                        onClick = {
                                            updateProfileState(
                                                profileState.copy(
                                                    editNameDialogState = EditNameDialogState(user.name, 0)
                                                )
                                            )
                                            updateDialogState(ProfileDialogs.NAME)
                                        },
                                        modifier = Modifier.size(25.dp).padding(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().weight(1f)
                                ) {
                                    OneLineText(
                                        text = "Role",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row {
                                        Text(
                                            text = user.role,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.fillMaxWidth().weight(1f).padding(5.dp)
                                        )
                                        if (preferences.id == user.id) {
                                            IconButton(
                                                onClick = {
                                                    updateProfileState(
                                                        profileState.copy(
                                                            editNameDialogState = EditNameDialogState(user.role, 0)
                                                        )
                                                    )
                                                    updateDialogState(ProfileDialogs.ROLE)
                                                },
                                                modifier = Modifier.size(25.dp).padding(5.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Edit,
                                                    contentDescription = null,
                                                    tint = Color.Unspecified,
                                                    modifier = Modifier.size(25.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier.fillMaxWidth().weight(1f)
                                ) {
                                    OneLineText(
                                        text = "Email",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                when (profileDialogs) {
                    ProfileDialogs.NONE -> {  }
                    ProfileDialogs.NAME -> {
                        EditNameDialog(
                            text = "User Name",
                            editNameDialogState = profileState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, name ->
                                changeUserName(name) {
                                    updateUser(user.copy(name = name))
                                    changePreferencesName(name)
                                }
                            },
                            onTextChange = {
                                updateProfileState(
                                    profileState.copy(
                                        editNameDialogState = profileState.editNameDialogState.copy(
                                            name = it
                                        )
                                    )
                                )
                            }
                        )
                    }
                    ProfileDialogs.ROLE -> {
                        EditNameDialog(
                            text = "User Role",
                            editNameDialogState = profileState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, role ->
                                changeUserRole(role) {
                                    updateUser(user.copy(role = role))
                                }
                            },
                            onTextChange = {
                                updateProfileState(
                                    profileState.copy(
                                        editNameDialogState = profileState.editNameDialogState.copy(
                                            name = it
                                        )
                                    )
                                )
                            }
                        )
                    }
                    ProfileDialogs.IMAGE -> {
                        UploadImageDialog(
                            onDismissRequest = removeDialog,
                            onImagePicked = {
                                updateProfileState(
                                    profileState.copy(
                                        image = it
                                    )
                                )
                            },
                            onApplyClick = { image ->
                                if (image != null) {
                                    uploadImage(image) {
                                        val encodedString = Utils.imageToEncodedString(image)
                                        updateUser(user.copy(image = encodedString))
                                        changePreferencesImage(encodedString)
                                    }
                                } else {
                                    Toast.makeText(context, "No image selected", Toast.LENGTH_LONG).show()
                                }
                            },
                            image = profileState.image,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfilePrev() {
    DICTProjectTheme {
        Profile(
            preferences = Preferences(),
            navController = rememberNavController(),
            context = LocalContext.current,
            paddingValues = PaddingValues(0.dp),
            process = ProcessState.Success,
            user = ProfileData(),
            profileDialogs = ProfileDialogs.NONE,
            profileState = ProfileState(),
            updateUser = {},
            updateDialogState = {},
            updateProfileState = {},
            changeUserName = { _, _ -> },
            changeUserRole = { _, _ -> },
            uploadImage = { _, _ -> },
            changePreferencesName = {},
            changePreferencesImage = {}
        )
    }
}