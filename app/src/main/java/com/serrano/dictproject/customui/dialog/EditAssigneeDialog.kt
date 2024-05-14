package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.UserDTO

/**
 * A dialog used for selecting/editing assignee over assignee options
 *
 * @param[taskAssignees] The possible assignees to select
 * @param[searchUserDialogState] Contains the selected assignees and an id that can be used for server request
 * @param[onUserAdd] Add the user to the selected assignees
 * @param[onUserRemove] Remove the user to the selected assignees
 * @param[onUserClick] Invoked when user click assignee/user image (e.g. go to user profile)
 * @param[onDismissRequest] Invoked when the users does something that should close the dialog
 * @param[onApplyClick] Invoked when the user want to apply the changes (click apply button)
 * @param[text] (Optional) Header text for the dialog
 */
@Composable
fun EditAssigneeDialog(
    taskAssignees: List<UserDTO>,
    searchUserDialogState: SearchUserDialogState,
    onUserAdd: (UserDTO) -> Unit,
    onUserRemove: (UserDTO) -> Unit,
    onUserClick: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onApplyClick: (Int, List<UserDTO>) -> Unit,
    text: String = "USERS"
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
                Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                    taskAssignees.forEach { user ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onUserClick(user.id) }) {
                                Icon(
                                    bitmap = FileUtils.encodedStringToImage(user.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                            OneLineText(
                                text = user.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            if (searchUserDialogState.users.any { user == it }) {
                                IconButton(onClick = { onUserRemove(user) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Remove,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                }
                            } else {
                                IconButton(onClick = { onUserAdd(user) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                            }
                        }
                    }
                }
                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            onDismissRequest()
                            onApplyClick(searchUserDialogState.taskId, searchUserDialogState.users)
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