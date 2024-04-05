package com.serrano.dictproject.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.dialog.ConfirmDialog
import com.serrano.dictproject.customui.menu.MessageBox
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.InboxDialogs
import com.serrano.dictproject.utils.InboxState
import com.serrano.dictproject.utils.MessagePartState
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Routes
import com.serrano.dictproject.utils.SharedViewModelState

@Composable
fun Inbox(
    navController: NavController,
    paddingValues: PaddingValues,
    process: ProcessState,
    process2: ProcessState,
    sentMessages: List<MessagePartState>,
    receivedMessages: List<MessagePartState>,
    inboxState: InboxState,
    sharedState: SharedViewModelState,
    inboxDialogs: InboxDialogs,
    updateInboxDialogs: (InboxDialogs) -> Unit,
    updateConfirmDialogState: (ConfirmDialogState) -> Unit,
    refreshSentMessages: () -> Unit,
    refreshReceivedMessages: () -> Unit,
    deleteMessageFromUser: (Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        when (sharedState.messageBottomBarIdx) {
            0 -> {
                InboxMenu(
                    messages = receivedMessages,
                    process = process2,
                    navController = navController,
                    paddingValues = paddingValues,
                    refreshState = inboxState.isReceivedRefreshing,
                    updateInboxDialogs = updateInboxDialogs,
                    updateConfirmDialogState = updateConfirmDialogState,
                    onRefresh = refreshReceivedMessages,
                    deleteMessageFromUser = deleteMessageFromUser
                )
            }
            1 -> {
                InboxMenu(
                    messages = sentMessages,
                    process = process,
                    navController = navController,
                    paddingValues = paddingValues,
                    refreshState = inboxState.isSentRefreshing,
                    updateInboxDialogs = updateInboxDialogs,
                    updateConfirmDialogState = updateConfirmDialogState,
                    onRefresh = refreshSentMessages,
                    deleteMessageFromUser = deleteMessageFromUser
                )
            }
        }
        when (inboxDialogs) {
            InboxDialogs.NONE -> {  }
            InboxDialogs.CONFIRM -> {
                ConfirmDialog(
                    id = inboxState.confirmDialogState.id,
                    placeholder = inboxState.confirmDialogState.placeholder,
                    onYesClick = inboxState.confirmDialogState.onYesClick,
                    onCancelClick = inboxState.confirmDialogState.onCancelClick
                )
            }
        }
    }
}

@Composable
fun InboxMenu(
    messages: List<MessagePartState>,
    process: ProcessState,
    navController: NavController,
    paddingValues: PaddingValues,
    refreshState: Boolean,
    updateInboxDialogs: (InboxDialogs) -> Unit,
    updateConfirmDialogState: (ConfirmDialogState) -> Unit,
    onRefresh: () -> Unit,
    deleteMessageFromUser: (Int) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = refreshState)

    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message, swipeRefreshState, onRefresh)
        }
        is ProcessState.Success -> {
            SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OneLineText(
                        text = "MESSAGES",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (messages.isNotEmpty()) {
                        LazyColumn {
                            items(items = messages) { message ->
                                MessageBox(
                                    message = message,
                                    onUserClick = {
                                        navController.navigate("${Routes.PROFILE}/$it")
                                    },
                                    onViewClick = {
                                        navController.navigate("${Routes.ABOUT_MESSAGE}/${message.messageId}")
                                    },
                                    onDeleteClick = { messageId ->
                                        updateConfirmDialogState(
                                            ConfirmDialogState(
                                                id = messageId,
                                                placeholder = "message for you only",
                                                onYesClick = {
                                                    deleteMessageFromUser(it)
                                                    updateInboxDialogs(InboxDialogs.NONE)
                                                },
                                                onCancelClick = {
                                                    updateInboxDialogs(InboxDialogs.NONE)
                                                }
                                            )
                                        )
                                        updateInboxDialogs(InboxDialogs.CONFIRM)
                                    }
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Spacer(modifier = Modifier.padding(100.dp))
                        }
                    }
                }
            }
        }
    }
}