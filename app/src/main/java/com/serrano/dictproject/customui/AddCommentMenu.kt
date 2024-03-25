package com.serrano.dictproject.customui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddCommentMenu(
    commentInput: String,
    buttonEnabled: Boolean,
    onCommentInputChange: (String) -> Unit,
    sendComment: () -> Unit
) {
    ScrollableTextField(
        value = commentInput,
        onValueChange = onCommentInputChange,
        placeholderText = "Enter comment"
    )
    CustomButton(
        text = "SEND COMMENT",
        onClick = sendComment,
        enabled = buttonEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}