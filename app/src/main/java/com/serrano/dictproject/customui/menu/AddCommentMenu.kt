package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.textfield.CommentWithMention
import com.serrano.dictproject.customui.textfield.InputFieldColors
import com.serrano.dictproject.utils.UserDTO

@Composable
fun AddCommentMenu(
    commentInput: String,
    buttonEnabled: Boolean,
    onCommentInputChange: (String) -> Unit,
    sendComment: () -> Unit,
    commentReply: List<String>,
    mentions: List<UserDTO>,
    onAddMentionsMenu: () -> Unit,
    onRemoveReply: () -> Unit
) {

    TextField(
        value = commentInput,
        onValueChange = onCommentInputChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(text = "Enter comment", style = MaterialTheme.typography.bodyMedium)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .heightIn(0.dp, 200.dp)
            .verticalScroll(rememberScrollState()),
        label = {
            Column {
                commentReply.forEach { ReplyBox(it, onRemoveReply) }
            }
        },
        visualTransformation = CommentWithMention(mentions),
        colors = InputFieldColors()
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onAddMentionsMenu,
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
            text = "SEND COMMENT",
            onClick = sendComment,
            enabled = buttonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}