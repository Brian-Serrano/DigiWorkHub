package com.serrano.dictproject.activity

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.serrano.dictproject.customui.CustomButton
import com.serrano.dictproject.customui.CustomTextField
import com.serrano.dictproject.customui.OneLineText
import com.serrano.dictproject.customui.dropdown.ReceiverDropDown
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.SearchState
import com.serrano.dictproject.utils.SendMessageDialogs
import com.serrano.dictproject.utils.SendMessageState
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessage(
    navController: NavController,
    paddingValues: PaddingValues,
    sendMessageState: SendMessageState,
    sendMessageDialogs: SendMessageDialogs,
    updateSendMessageState: (SendMessageState) -> Unit,
    updateDialogState: (SendMessageDialogs) -> Unit,
    sendMessage: (() -> Unit) -> Unit,
    searchUser: (String, (List<User>) -> Unit) -> Unit
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
                onUserClick = { navController.navigate("Profile/$it") }
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
                    .verticalScroll(rememberScrollState())
            )
            Text(
                text = sendMessageState.errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red
            )
            CustomButton(
                text = "SEND MESSAGE",
                onClick = { sendMessage { navController.navigate("Inbox") } },
                modifier = Modifier.fillMaxWidth(),
                enabled = sendMessageState.buttonEnabled
            )
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
                                        IconButton(onClick = {  }) {
                                            Icon(
                                                bitmap = Utils.encodedStringToImage(user.image),
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
    }
}

@PreviewScreenSizes
@Composable
fun SMPrev() {
    DICTProjectTheme {
        SendMessage(
            navController = rememberNavController(),
            paddingValues = PaddingValues(0.dp),
            sendMessageState = SendMessageState(),
            sendMessageDialogs = SendMessageDialogs.RECEIVER,
            updateSendMessageState = {},
            updateDialogState = {},
            sendMessage = { _ -> },
            searchUser = { _, _ -> }
        )
    }
}