package com.serrano.dictproject.activity

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.dialog.EditNameDialog
import com.serrano.dictproject.customui.dialog.UploadImageDialog
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.utils.EditNameDialogState
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.ProfileDataDTO
import com.serrano.dictproject.utils.ProfileDialogs
import com.serrano.dictproject.utils.ProfileState

/**
 * This page is where the user can see information of other user or self
 *
 * @param[preferences] The user information, that will be used to know who user access the page and do not do things or do not show content base on who the user e.g. do not allow user to change image of other user.
 * @param[navController] Used for navigating to different page
 * @param[context] Used to show toast message and the [FileUtils] utility class
 * @param[paddingValues] This is used to move contents down (where it should and can see it), there is top bar on page
 * @param[process] The process of the content (see [ProcessState.Success], [ProcessState.Error], [ProcessState.Loading] for more information)
 * @param[user] The information about the user, this comes from server or in room database
 * @param[profileDialogs] What dialog to show the default is NONE (do not show)
 * @param[profileState] This states are used when user edit his/her information and refresh the page
 * @param[updateDialogState] Update the value of state responsible for showing dialogs
 * @param[updateProfileState] Update the values of [profileState]
 * @param[changeUserName] Callback function responsible for updating user name. It needs the new name.
 * @param[changeUserRole] Callback function responsible for updating user role. It needs the new role.
 * @param[uploadImage] Callback function responsible for updating user image. It needs the new image.
 * @param[refreshUser] Refresh the page
 */
@Composable
fun Profile(
    preferences: Preferences,
    navController: NavController,
    context: Context,
    paddingValues: PaddingValues,
    process: ProcessState,
    user: ProfileDataDTO,
    profileDialogs: ProfileDialogs,
    profileState: ProfileState,
    updateDialogState: (ProfileDialogs) -> Unit,
    updateProfileState: (ProfileState) -> Unit,
    changeUserName: (String) -> Unit,
    changeUserRole: (String) -> Unit,
    uploadImage: (ImageBitmap) -> Unit,
    refreshUser: () -> Unit
) {
    val removeDialog = { updateDialogState(ProfileDialogs.NONE) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = profileState.isRefreshing)
    val activity = LocalContext.current as Activity

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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (preferences.id == user.id) {
                                        updateProfileState(profileState.copy(image = FileUtils.encodedStringToImage(user.image)))
                                        updateDialogState(ProfileDialogs.IMAGE)
                                    } else {
                                        MiscUtils.toast(context, "You can only edit your own image")
                                    }
                                },
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(10.dp)
                            ) {
                                Icon(
                                    bitmap = FileUtils.encodedStringToImage(user.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(100.dp)
                                )
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                                modifier = Modifier
                                                    .size(25.dp)
                                                    .padding(5.dp)
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
                                    IconButton(
                                        onClick = {
                                            if (!navController.popBackStack()) {
                                                activity.finish()
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {
                                        OneLineText(
                                            text = "Role",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row {
                                            Text(
                                                text = user.role,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .padding(5.dp)
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
                                                    modifier = Modifier
                                                        .size(25.dp)
                                                        .padding(5.dp)
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
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
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
                }
                when (profileDialogs) {
                    ProfileDialogs.NONE -> {  }
                    ProfileDialogs.NAME -> {
                        EditNameDialog(
                            text = "User Name",
                            editNameDialogState = profileState.editNameDialogState,
                            onDismissRequest = removeDialog,
                            onApplyClick = { _, name -> changeUserName(name) },
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
                            onApplyClick = { _, role -> changeUserRole(role) },
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
                                    uploadImage(image)
                                } else {
                                    MiscUtils.toast(context, "No image selected")
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