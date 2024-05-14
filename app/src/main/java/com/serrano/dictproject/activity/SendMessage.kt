package com.serrano.dictproject.activity

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.dialog.UploadFileDialog
import com.serrano.dictproject.customui.dropdown.ReceiverDropDown
import com.serrano.dictproject.customui.menu.UploadFileBox
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.customui.textfield.CustomTextField
import com.serrano.dictproject.customui.textfield.InputFieldColors
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SendMessageDialogs
import com.serrano.dictproject.utils.SendMessageState
import com.serrano.dictproject.utils.UserDTO

/**
 * This is the page where you can send messages
 *
 * @param[navController] Used for navigating to different page
 * @param[paddingValues] This is used to move contents down (where it should and can see it), there is top bar on page
 * @param[context] Used to show toast message and the [FileUtils] utility class
 * @param[sendMessageState] This states/values are used for input fields in the page
 * @param[sendMessageDialogs] What dialog to show the default is NONE (do not show)
 * @param[updateSendMessageState] Update the values of [sendMessageState]
 * @param[updateDialogState] Update the value of state responsible for showing dialogs
 * @param[sendMessage] Callback function responsible for sending messages. It needs the navigation callback function that will invoked when the message sent (navigate to inbox).
 * @param[searchUser] Callback function responsible for searching recipient. It needs the search query (text in search bar) and callback function that will be invoked with the result/response users as its argument, should add the data to the state in the callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessage(
    navController: NavController,
    paddingValues: PaddingValues,
    context: Context,
    sendMessageState: SendMessageState,
    sendMessageDialogs: SendMessageDialogs,
    updateSendMessageState: (SendMessageState) -> Unit,
    updateDialogState: (SendMessageDialogs) -> Unit,
    sendMessage: (() -> Unit) -> Unit,
    searchUser: (String, (List<UserDTO>) -> Unit) -> Unit
) {
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
            ReceiverDropDown(
                user = sendMessageState.receiver,
                onUserClick = { navController.navigate("${Routes.PROFILE}/$it") }
            ) {
                updateSendMessageState(sendMessageState.copy(searchState = SearchState()))
                updateDialogState(SendMessageDialogs.RECEIVER)
            }
            CustomTextField(
                value = sendMessageState.title,
                onValueChange = { updateSendMessageState(sendMessageState.copy(title = it)) },
                placeholderText = "Enter message subject"
            )
            TextField(
                value = sendMessageState.description,
                onValueChange = { updateSendMessageState(sendMessageState.copy(description = it)) },
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = {
                    Text(
                        text = "Enter message description",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(5.dp)
                    .verticalScroll(rememberScrollState()),
                colors = InputFieldColors()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 150.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                sendMessageState.fileUris.forEach {
                    UploadFileBox(
                        fileName = MiscUtils.getFileNameFromUri(context, it),
                        onIconClick = { updateSendMessageState(sendMessageState.copy(fileUris = sendMessageState.fileUris - it)) },
                        icon = Icons.Filled.Remove,
                        color = Color.Red
                    )
                }
            }
            Text(
                text = sendMessageState.errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate(Routes.INBOX) },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Button(
                    onClick = {
                        updateSendMessageState(sendMessageState.copy(dialogUri = null))
                        updateDialogState(SendMessageDialogs.ATTACHMENT)
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                CustomButton(
                    text = "SEND MESSAGE",
                    onClick = { sendMessage { navController.navigate(Routes.INBOX) } },
                    enabled = sendMessageState.buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
        when (sendMessageDialogs) {
            SendMessageDialogs.NONE -> {  }
            SendMessageDialogs.RECEIVER -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x55000000)))
                Dialog(
                    onDismissRequest = { updateDialogState(SendMessageDialogs.NONE) }
                ) {
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
                                text = "Enter recipient",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            SearchBar(
                                query = sendMessageState.searchState.searchQuery,
                                onQueryChange = {
                                    updateSendMessageState(
                                        sendMessageState.copy(
                                            searchState = sendMessageState.searchState.copy(searchQuery = it)
                                        )
                                    )
                                },
                                active = sendMessageState.searchState.isActive,
                                onSearch = { query ->
                                    searchUser(query) {
                                        updateSendMessageState(
                                            sendMessageState.copy(
                                                searchState = sendMessageState.searchState.copy(results = it)
                                            )
                                        )
                                    }
                                },
                                onActiveChange = {
                                    updateSendMessageState(
                                        sendMessageState.copy(
                                            searchState = sendMessageState.searchState.copy(isActive = it)
                                        )
                                    )
                                },
                                placeholder = {
                                    OneLineText(text = "Search recipient", style = MaterialTheme.typography.bodySmall)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Row {
                                        if (sendMessageState.searchState.isActive) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(horizontal = 5.dp)
                                                    .clickable {
                                                        if (sendMessageState.searchState.searchQuery.isEmpty()) {
                                                            updateSendMessageState(
                                                                sendMessageState.copy(
                                                                    searchState = sendMessageState.searchState.copy(
                                                                        isActive = false
                                                                    )
                                                                )
                                                            )
                                                        } else {
                                                            updateSendMessageState(
                                                                sendMessageState.copy(
                                                                    searchState = sendMessageState.searchState.copy(
                                                                        searchQuery = ""
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    }
                                            )
                                        }
                                    }
                                },
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth(),
                                colors = SearchBarDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    dividerColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                LazyColumn {
                                    items(items = sendMessageState.searchState.results) { user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    updateDialogState(SendMessageDialogs.NONE)
                                                    updateSendMessageState(
                                                        sendMessageState.copy(
                                                            receiver = user
                                                        )
                                                    )
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    navController.navigate("${Routes.PROFILE}/${user.id}")
                                                }
                                            ) {
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            SendMessageDialogs.ATTACHMENT -> {
                UploadFileDialog(
                    onDismissRequest = { updateDialogState(SendMessageDialogs.NONE) },
                    onFilePicked = { updateSendMessageState(sendMessageState.copy(dialogUri = it)) },
                    onApplyClick = {
                        if (it != null) {
                            if (sendMessageState.fileUris.size < 5) {
                                updateSendMessageState(sendMessageState.copy(fileUris = sendMessageState.fileUris + it))
                            } else {
                                MiscUtils.toast(context, "You can only select up to 5 files.")
                            }
                        }
                    },
                    file = sendMessageState.dialogUri,
                    context = context
                )
            }
        }
    }
}