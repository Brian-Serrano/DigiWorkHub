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

/**
 * This page is where you can see all the sent and received messages.
 *
 * @param[navController] Used for navigating to different page
 * @param[paddingValues] This is used to move contents down (where it should and can see it), there is top bar on page
 * @param[process] The process for the sent messages tab (see [ProcessState.Success], [ProcessState.Error], [ProcessState.Loading] for more information)
 * @param[process2] The process for the received messages tab (see [ProcessState.Success], [ProcessState.Error], [ProcessState.Loading] for more information)
 * @param[sentMessages] The sent messages coming from the server or in room database
 * @param[receivedMessages] The received messages coming from the server or in room database
 * @param[inboxState] States/values for this page, used by confirm dialog and swipe refresh components
 * @param[sharedState] [SharedViewModelState.messageBottomBarIdx] is needed for this page
 * @param[inboxDialogs] What dialog to show the default is NONE (do not show)
 * @param[updateInboxDialogs] Update the value of state responsible for showing dialogs
 * @param[updateConfirmDialogState] Update the values and actions in confirm dialog. One confirm dialog is only used and only change its values base on what triggers/shows it.
 * @param[refreshSentMessages] Refresh sent messages
 * @param[refreshReceivedMessages] Refresh received messages
 * @param[deleteMessageFromUser] Callback function responsible for deleting the message only for the user. It needs the id of message to delete.
 */
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
                                                placeholder = "Are you sure you want to delete this message for you only?",
                                                onYesClick = deleteMessageFromUser,
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