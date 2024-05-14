package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.UserDTO

/**
 * Container used in recipient in send message
 *
 * @param[user] The recipient
 * @param[onUserClick] Invoked when the user click the recipient image (go to user profile)
 * @param[onSearchUser] Invoked when the user click the edit icon (select recipient from dialog)
 */
@Composable
fun ReceiverDropDown(
    user: UserDTO?,
    onUserClick: (Int) -> Unit,
    onSearchUser: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant),
                MaterialTheme.shapes.extraSmall
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (user != null) {
                IconButton(
                    onClick = { onUserClick(user.id) },
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
        }
        IconButton(
            onClick = onSearchUser,
            modifier = Modifier.padding(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}