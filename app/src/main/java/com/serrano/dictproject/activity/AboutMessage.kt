package com.serrano.dictproject.activity

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.serrano.dictproject.customui.ErrorComposable
import com.serrano.dictproject.customui.Loading
import com.serrano.dictproject.customui.RememberWindowInfo
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.dialog.ConfirmDialog
import com.serrano.dictproject.customui.dialog.UploadFileDialog
import com.serrano.dictproject.customui.menu.ReplyBox
import com.serrano.dictproject.customui.menu.UploadFileBox
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.customui.textfield.InputFieldColors
import com.serrano.dictproject.datastore.Preferences
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.AboutMessageDialogs
import com.serrano.dictproject.utils.AboutMessageState
import com.serrano.dictproject.utils.ConfirmDialogState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.MessageReplyState
import com.serrano.dictproject.utils.MessageState
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.ProcessState
import com.serrano.dictproject.utils.Routes
import java.time.LocalDateTime

@Composable
fun AboutMessage(
    windowInfo: WindowInfo,
    preferences: Preferences,
    navController: NavController,
    paddingValues: PaddingValues,
    context: Context,
    message: MessageState,
    aboutMessageState: AboutMessageState,
    aboutMessageDialogs: AboutMessageDialogs,
    process: ProcessState,
    updateDialogState: (AboutMessageDialogs) -> Unit,
    updateAboutMessageState: (AboutMessageState) -> Unit,
    updateConfirmDialogState: (ConfirmDialogState) -> Unit,
    downloadAttachment: (String, String) -> Unit,
    replyMessage: () -> Unit,
    refreshMessage: () -> Unit,
    deleteMessage: (Int, () -> Unit) -> Unit,
    deleteReply: (Int) -> Unit,
    deleteMessageFromUser: (Int, () -> Unit) -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = aboutMessageState.isRefreshing)

    when (process) {
        is ProcessState.Loading -> {
            Loading(paddingValues)
        }
        is ProcessState.Error -> {
            ErrorComposable(navController, paddingValues, process.message, swipeRefreshState, refreshMessage)
        }
        is ProcessState.Success -> {
            val removeDialog = { updateDialogState(AboutMessageDialogs.NONE) }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SwipeRefresh(state = swipeRefreshState, onRefresh = refreshMessage) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = message.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth().padding(15.dp).weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        updateConfirmDialogState(
                                            aboutMessageState.confirmDialogState.copy(
                                                id = message.messageId,
                                                placeholder = "message for you only",
                                                onYesClick = { id ->
                                                    removeDialog()
                                                    deleteMessageFromUser(id) {
                                                        navController.navigate(Routes.INBOX)
                                                    }
                                                },
                                                onCancelClick = removeDialog
                                            )
                                        )
                                        updateDialogState(AboutMessageDialogs.CONFIRM)
                                    },
                                    enabled = message.deleteForUserButtonEnabled
                                ) {
                                    if (message.deleteForUserButtonEnabled) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = null
                                        )
                                    } else {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { navController.navigate("${Routes.PROFILE}/${message.sender.id}") },
                                        modifier = Modifier.padding(5.dp)
                                    ) {
                                        Icon(
                                            bitmap = FileUtils.encodedStringToImage(message.sender.image),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    }
                                    OneLineText(
                                        text = message.sender.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OneLineText(
                                    text = DateUtils.dateTimeToDateTimeString(message.sentDate)
                                )
                            }
                            Divider(thickness = 2.dp, color = Color.Black)
                            Text(
                                text = message.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp, vertical = 5.dp),
                                textAlign = TextAlign.Justify
                            )
                            message.fileNames.forEachIndexed { idx, name ->
                                UploadFileBox(
                                    fileName = name,
                                    onIconClick = { downloadAttachment(name, message.attachmentPaths[idx]) },
                                    icon = Icons.Filled.Download,
                                    color = Color.Unspecified
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    )
                                    OneLineText(text = "Sent to: ")
                                    IconButton(
                                        onClick = { navController.navigate("${Routes.PROFILE}/${message.receiver.id}") },
                                        modifier = Modifier.padding(5.dp)
                                    ) {
                                        Icon(
                                            bitmap = FileUtils.encodedStringToImage(message.receiver.image),
                                            contentDescription = null,
                                            tint = Color.Unspecified
                                        )
                                    }
                                    OneLineText(
                                        text = message.receiver.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomButton(
                                text = "INBOX",
                                onClick = { navController.navigate(Routes.INBOX) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            if (preferences.id == message.sender.id) {
                                CustomButton(
                                    text = "DELETE MESSAGE",
                                    onClick = {
                                        updateConfirmDialogState(
                                            aboutMessageState.confirmDialogState.copy(
                                                id = message.messageId,
                                                placeholder = "message for you and the recipient",
                                                onYesClick = { id ->
                                                    removeDialog()
                                                    deleteMessage(id) {
                                                        navController.navigate(Routes.INBOX)
                                                    }
                                                },
                                                onCancelClick = removeDialog
                                            )
                                        )
                                        updateDialogState(AboutMessageDialogs.CONFIRM)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    enabled = message.deleteButtonEnabled
                                )
                            }
                        }
                        TextField(
                            value = aboutMessageState.description,
                            onValueChange = { updateAboutMessageState(aboutMessageState.copy(description = it)) },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            placeholder = {
                                Text(text = "Enter reply", style = MaterialTheme.typography.bodyMedium)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .heightIn(0.dp, 200.dp)
                                .verticalScroll(rememberScrollState()),
                            label = {
                                Column {
                                    aboutMessageState.fileUris.forEach {
                                        ReplyBox(text = MiscUtils.getFileNameFromUri(context, it)) {
                                            updateAboutMessageState(
                                                aboutMessageState.copy(
                                                    fileUris = aboutMessageState.fileUris - it
                                                )
                                            )
                                        }
                                    }
                                }
                            },
                            colors = InputFieldColors()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    updateAboutMessageState(aboutMessageState.copy(dialogUri = null))
                                    updateDialogState(AboutMessageDialogs.ATTACHMENT)
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
                                text = "SEND REPLY",
                                onClick = replyMessage,
                                enabled = aboutMessageState.buttonEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        if (message.replies.isNotEmpty()) {
                            OneLineText(
                                text = "REPLIES",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            message.replies.forEach { reply ->
                                val user = if (reply.fromId == message.sender.id) message.sender else message.receiver

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .border(
                                            BorderStroke(width = 2.dp, Color.Black),
                                            MaterialTheme.shapes.extraSmall
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { navController.navigate("${Routes.PROFILE}/${user.id}") },
                                                modifier = Modifier.padding(5.dp)
                                            ) {
                                                Icon(
                                                    bitmap = FileUtils.encodedStringToImage(user.image),
                                                    contentDescription = null,
                                                    tint = Color.Unspecified
                                                )
                                            }
                                            OneLineText(
                                                text = user.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            OneLineText(
                                                text = when (windowInfo.screenWidthInfo) {
                                                    is WindowInfo.WindowType.Compact -> {
                                                        DateUtils.dateTimeToDateString(reply.sentDate)
                                                    }
                                                    is WindowInfo.WindowType.Medium, is WindowInfo.WindowType.Expanded -> {
                                                        DateUtils.dateTimeToDateTimeString(reply.sentDate)
                                                    }
                                                }
                                            )
                                            if (user.id == preferences.id) {
                                                IconButton(
                                                    onClick = {
                                                        updateConfirmDialogState(
                                                            aboutMessageState.confirmDialogState.copy(
                                                                id = reply.messageReplyId,
                                                                placeholder = "reply",
                                                                onYesClick = { id ->
                                                                    removeDialog()
                                                                    deleteReply(id)
                                                                },
                                                                onCancelClick = removeDialog
                                                            )
                                                        )
                                                        updateDialogState(AboutMessageDialogs.CONFIRM)
                                                    },
                                                    enabled = reply.deleteIconEnabled
                                                ) {
                                                    if (reply.deleteIconEnabled) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Delete,
                                                            contentDescription = null
                                                        )
                                                    } else {
                                                        CircularProgressIndicator(
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(25.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Text(
                                        text = reply.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 5.dp)
                                    )
                                    reply.fileNames.forEachIndexed { idx, name ->
                                        UploadFileBox(
                                            fileName = name,
                                            onIconClick = { downloadAttachment(name, reply.attachmentPaths[idx]) },
                                            icon = Icons.Filled.Download,
                                            color = Color.Unspecified
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                when (aboutMessageDialogs) {
                    AboutMessageDialogs.NONE -> {  }
                    AboutMessageDialogs.ATTACHMENT -> {
                        UploadFileDialog(
                            onDismissRequest = removeDialog,
                            onFilePicked = {
                                updateAboutMessageState(
                                    aboutMessageState.copy(
                                        dialogUri = it
                                    )
                                )
                            },
                            onApplyClick = {
                                if (it != null) {
                                    if (aboutMessageState.fileUris.size < 5) {
                                        updateAboutMessageState(
                                            aboutMessageState.copy(
                                                fileUris = aboutMessageState.fileUris + it
                                            )
                                        )
                                    } else {
                                        MiscUtils.toast(context, "You can only select up to 5 files.")
                                    }
                                }
                            },
                            file = aboutMessageState.dialogUri,
                            context = context
                        )
                    }
                    AboutMessageDialogs.CONFIRM -> {
                        ConfirmDialog(
                            id = aboutMessageState.confirmDialogState.id,
                            placeholder = aboutMessageState.confirmDialogState.placeholder,
                            onYesClick = aboutMessageState.confirmDialogState.onYesClick,
                            onCancelClick = aboutMessageState.confirmDialogState.onCancelClick
                        )
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun AMPrev() {
    DICTProjectTheme(dynamicColor = false) {
        val reply = MessageReplyState(0, 0, LocalDateTime.now(), "Watch This Quick Tutorial for More Tips Draw, Refine, and Witness Your Style Come to Life Available now for both Free and Premium users!.", 0, listOf("Test12345.docx", "Test45678.pptx"), listOf("Test12345.docx", "Test45678.pptx"), true)

        AboutMessage(
            windowInfo = RememberWindowInfo(),
            preferences = Preferences(),
            navController = rememberNavController(),
            paddingValues = PaddingValues(0.dp),
            context = LocalContext.current,
            message = MessageState(replies = listOf(reply, reply, reply)),
            process = ProcessState.Success,
            downloadAttachment = { _, _ -> },
            aboutMessageState = AboutMessageState(
                fileUris = listOf(),
                dialogUri = null,
                description = "",
                buttonEnabled = false
            ),
            aboutMessageDialogs = AboutMessageDialogs.NONE,
            updateDialogState = {},
            updateAboutMessageState = {},
            updateConfirmDialogState = {},
            replyMessage = {},
            refreshMessage = {},
            deleteMessage = { _, _ -> },
            deleteReply = {},
            deleteMessageFromUser = { _, _ -> }
        )
    }
}