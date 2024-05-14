package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
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
import com.serrano.dictproject.customui.CustomSearchBar
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SearchUserDialogState
import com.serrano.dictproject.utils.UserDTO

/**
 * A dialog used for selecting/editing assignees. This contains a search bar where the user can search for assignees to add.
 *
 * @param[text] Header text for the dialog
 * @param[searchUserDialogState] State for the dialog, includes the id needed for the server request and the selected assignees
 * @param[searchState] State for the search bar component inside this dialog
 * @param[onUserClick] Invoked when the user click the image of user/assignee (go to user's profile)
 * @param[onDismissRequest] Invoked when the users does something that should close the dialog
 * @param[onApplyClick] Invoked when the user want to apply the changes (click apply button)
 * @param[onSearch] Search for user
 * @param[onQueryChange] Change the search query in search bar
 * @param[onActiveChange] Activate/Deactivate the search bar
 * @param[onTrailingIconClick] Invoked when the user click the close/remove button of search bar
 * @param[onUserAdd] Add the user to the selected assignees
 * @param[onUserRemove] Remove the user to the selected assignees
 */
@Composable
fun SearchUserDialog(
    text: String,
    searchUserDialogState: SearchUserDialogState,
    searchState: SearchState,
    onUserClick: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    onApplyClick: (Int, List<UserDTO>) -> Unit,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    onTrailingIconClick: () -> Unit,
    onUserAdd: (UserDTO) -> Unit,
    onUserRemove: (UserDTO) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(500.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OneLineText(
                    text = "Edit $text",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(searchUserDialogState.users) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onUserClick(it.id) }) {
                                Icon(
                                    bitmap = FileUtils.encodedStringToImage(it.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                            OneLineText(text = it.name, modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f))
                            IconButton(onClick = { onUserRemove(it) }) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
                CustomSearchBar(
                    placeHolder = "Search User",
                    searchState = searchState,
                    searchUserDialogState = searchUserDialogState,
                    onUserClick = onUserClick,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    onActiveChange = onActiveChange,
                    onTrailingIconClick = onTrailingIconClick,
                    onUserAdd = onUserAdd
                )
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