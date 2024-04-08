package com.serrano.dictproject.customui.dialog

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.MiscUtils

@Composable
fun UploadFileDialog(
    onDismissRequest: () -> Unit,
    onFilePicked: (Uri?) -> Unit,
    onApplyClick: (Uri?) -> Unit,
    file: Uri?,
    context: Context
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { onFilePicked(it) }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(IntrinsicSize.Min)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (file != null) {
                    val fileName = MiscUtils.getFileNameFromUri(context, file)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .border(BorderStroke(1.dp, Color.Black))
                            .padding(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = MiscUtils.getFileIcon(fileName.split(".").last())
                            ),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(start = 20.dp).size(20.dp)
                        )
                        OneLineText(text = fileName)
                    }
                }
                CustomButton(
                    text = "Pick a file to upload.",
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    }
                )
                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            onDismissRequest()
                            onApplyClick(file)
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