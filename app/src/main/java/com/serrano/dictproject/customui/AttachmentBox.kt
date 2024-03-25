package com.serrano.dictproject.customui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.Attachment
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr
import java.time.LocalDateTime

@Composable
fun AttachmentBox(
    attachment: Attachment,
    onUserClick: (Int) -> Unit,
    onDownload: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp)
        ) {
            IconButton(
                onClick = { onUserClick(attachment.user.id) },
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(
                    bitmap = Utils.encodedStringToImage(attachment.user.image),
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(10.dp)
                .align(Alignment.CenterHorizontally)
                .border(BorderStroke(2.dp, Color.Black))
        ) {
            Icon(
                imageVector = Icons.Filled.AccountBox,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.padding(start = 20.dp)
            )
            OneLineText(
                text = attachment.fileName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(5.dp)
            )
            IconButton(
                onClick = onDownload,
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Preview
@Composable
fun ABPrev() {
    DICTProjectTheme {
        AttachmentBox(
            attachment = Attachment(
                User(0, "Danzwomen", imageStr), Utils.dateTimeToDateTimeString(
                    LocalDateTime.now()
                ), "Test Hello World.docx", LocalDateTime.now()
            ), onUserClick = {}
        ) {

        }
    }
}