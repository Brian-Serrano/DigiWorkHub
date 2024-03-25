package com.serrano.dictproject.customui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.Comment
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr
import java.time.LocalDateTime

@Composable
fun CommentBox(
    currentUserId: Int,
    comment: Comment,
    likeIconEnabled: Boolean,
    onUserClick: (Int) -> Unit,
    onReplyClick: (String) -> Unit,
    onLikeClick: (Int) -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onUserClick(comment.user.id) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        bitmap = Utils.encodedStringToImage(comment.user.image),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                OneLineText(
                    text = comment.user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            OneLineText(
                text = Utils.dateTimeToDateTimeString(comment.sentDate)
            )
        }
        Text(
            text = comment.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp)
        )
        Divider(thickness = 2.dp, color = Color.Black)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onLikeClick(comment.commentId) },
                    enabled = likeIconEnabled
                ) {
                    if (likeIconEnabled) {
                        Icon(
                            imageVector = if (comment.likesId.any { it == currentUserId }) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = null
                        )
                    } else {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
                OneLineText(text = comment.likesId.size.toString())
            }
            OneLineText(
                text = "REPLY",
                modifier = Modifier
                    .clickable(onClick = { onReplyClick("@ ${comment.user.name} ") })
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun CommentPrev() {
    DICTProjectTheme {
        CommentBox(0, comment = Comment(0, "Hello World Test Hello World Test Hello World Test Hello World Test Hello World Test", User(0, "Danzwomen", imageStr), LocalDateTime.now(), listOf(0)), false, {}, {}, {})
    }
}