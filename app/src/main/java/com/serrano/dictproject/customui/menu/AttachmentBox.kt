package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.AttachmentState
import com.serrano.dictproject.utils.FileUtils

/**
 * Container used for received attachments of tasks
 *
 * @param[currentUserId] The id of user to determine who the user. User uploaded the attachment is only the user can delete it.
 * @param[attachment] Attachment information
 * @param[onUserClick] Invoked when the user click the image of user uploaded the attachment (go to user profile)
 * @param[onDownload] Download the file from server and store to user's device shared storage
 * @param[onDeleteClick] Delete the attachment on the server
 */
@Composable
fun AttachmentBox(
    currentUserId: Int,
    attachment: AttachmentState,
    onUserClick: (Int) -> Unit,
    onDownload: () -> Unit,
    onDeleteClick: (Int) -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(5.dp)
            ) {
                IconButton(
                    onClick = { onUserClick(attachment.user.id) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        bitmap = FileUtils.encodedStringToImage(attachment.user.image),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                OneLineText(
                    text = attachment.user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            if (attachment.user.id == currentUserId) {
                IconButton(
                    onClick = { onDeleteClick(attachment.attachmentId) },
                    enabled = attachment.deleteIconEnabled
                ) {
                    if (attachment.deleteIconEnabled) {
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
        UploadFileBox(
            fileName = attachment.fileName,
            onIconClick = onDownload,
            icon = Icons.Filled.Download,
            color = Color.Unspecified
        )
    }
}