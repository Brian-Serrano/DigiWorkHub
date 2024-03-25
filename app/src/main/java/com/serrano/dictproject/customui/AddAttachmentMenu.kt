package com.serrano.dictproject.customui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.AddAttachmentState
import com.serrano.dictproject.utils.Utils

@Composable
fun AddAttachmentMenu(
    attachmentState: AddAttachmentState,
    onFilePicked: (Uri?) -> Unit,
    onFileUpload: () -> Unit,
    context: Context
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { onFilePicked(it) }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .border(BorderStroke(1.dp, Color.Black), MaterialTheme.shapes.extraSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (attachmentState.fileUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .border(BorderStroke(1.dp, Color.Black))
                    .padding(10.dp)
            ) {
                OneLineText(text = Utils.getFileNameFromUri(context, attachmentState.fileUri))
            }
        }
        CustomButton(
            text = "Pick a file to upload.",
            onClick = {
                filePickerLauncher.launch("*/*")
            }
        )
    }
    CustomButton(
        text = "UPLOAD",
        onClick = onFileUpload,
        enabled = attachmentState.buttonEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun AAMPrev() {
    DICTProjectTheme {
        Column {
            AddAttachmentMenu(attachmentState = AddAttachmentState(), onFilePicked = {}, onFileUpload = {}, LocalContext.current)
        }
    }
}