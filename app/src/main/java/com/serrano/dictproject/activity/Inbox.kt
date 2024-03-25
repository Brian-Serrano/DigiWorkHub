package com.serrano.dictproject.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.MessageBox
import com.serrano.dictproject.utils.MessagePart
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.SharedViewModelState

@Composable
fun Inbox(
    navController: NavController,
    paddingValues: PaddingValues,
    process: ProcessState,
    process2: ProcessState,
    sentMessages: List<MessagePart>,
    receivedMessages: List<MessagePart>,
    sharedState: SharedViewModelState
) {
    when (sharedState.messageBottomBarIdx) {
        0 -> {
            InboxMenu(
                messages = receivedMessages,
                process = process2,
                navController = navController,
                paddingValues = paddingValues
            )
        }
        1 -> {
            InboxMenu(
                messages = sentMessages,
                process = process,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
fun InboxMenu(
    messages: List<MessagePart>,
    process: ProcessState,
    navController: NavController,
    paddingValues: PaddingValues
) {
    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message)
        }
        is ProcessState.Success -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn {
                    items(items = messages) { message ->
                        MessageBox(
                            user = message.other,
                            sentDate = message.sentDate,
                            description = message.title,
                            onUserClick = { navController.navigate("Profile/$it") }
                        ) {
                            navController.navigate("AboutMessage/${message.messageId}")
                        }
                    }
                }
            }
        }
    }
}