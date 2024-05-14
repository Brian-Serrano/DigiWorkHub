package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.MiscUtils

/**
 * Container used for attachments. Used in send message, about message and about task.
 *
 * @param[fileName] The name of attachment
 * @param[onIconClick] Remove attachment (send message), download attachment (about message, about task)
 * @param[icon] Remove icon (send message), download icon (about message, about task)
 * @param[color] Color of icon
 */
@Composable
fun ColumnScope.UploadFileBox(
    fileName: String,
    onIconClick: () -> Unit,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 30.dp)
            .align(Alignment.CenterHorizontally)
            .border(BorderStroke(2.dp, Color.Black))
    ) {
        Icon(
            painter = painterResource(
                id = MiscUtils.getFileIcon(fileName.split(".").last())
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.padding(start = 20.dp).size(20.dp)
        )
        OneLineText(
            text = fileName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(5.dp)
        )
        IconButton(
            onClick = onIconClick,
            modifier = Modifier.padding(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        }
    }
}