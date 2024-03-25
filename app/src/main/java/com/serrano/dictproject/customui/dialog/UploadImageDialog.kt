package com.serrano.dictproject.customui.dialog

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.CustomButton
import com.serrano.dictproject.utils.Utils

@Composable
fun UploadImageDialog(
    onDismissRequest: () -> Unit,
    onImagePicked: (ImageBitmap?) -> Unit,
    onApplyClick: (ImageBitmap?) -> Unit,
    image: ImageBitmap?,
    context: Context
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { onImagePicked(Utils.imageUriToImage(it, context)) }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
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
            if (image != null) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(100.dp).padding(10.dp)
                ) {
                    Icon(
                        bitmap = image,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color.Unspecified
                    )
                }
            }
            CustomButton(
                text = "Pick a file to upload.",
                onClick = {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
            Row {
                CustomButton(
                    text = "APPLY",
                    onClick = {
                        onApplyClick(image)
                        onDismissRequest()
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