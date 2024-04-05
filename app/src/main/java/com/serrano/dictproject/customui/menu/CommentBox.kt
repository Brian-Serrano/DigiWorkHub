package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.CommentState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.UserDTO
import com.serrano.dictproject.utils.imageStr
import com.serrano.dictproject.utils.loremIpsum
import java.time.LocalDateTime

@Composable
fun CommentBox(
    currentUserId: Int,
    comment: CommentState,
    onUserClick: (Int) -> Unit,
    onReplyClick: (CommentState) -> Unit,
    onLikeClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    commentReply: List<String>
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
                        bitmap = FileUtils.encodedStringToImage(comment.user.image),
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
                text = DateUtils.dateTimeToDateTimeString(comment.sentDate)
            )
        }
        commentReply.forEach {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 10.dp),
                border = BorderStroke(1.dp, Color.DarkGray),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.DarkGray
                )
            ) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        color = Color.DarkGray,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxHeight()
                            .width(5.dp)
                    )
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(20.dp)
                    )
                }
            }
        }
        Text(
            text = buildAnnotatedString {
                comment.mentionsName.forEach {
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color.Blue
                        )
                    ) {
                        append("@ $it")
                    }
                    append(" ")
                }
                append(comment.description)
            },
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
                    enabled = comment.likeIconEnabled
                ) {
                    if (comment.likeIconEnabled) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                OneLineText(
                    text = "REPLY",
                    modifier = Modifier
                        .clickable(onClick = { onReplyClick(comment) })
                )
                if (comment.user.id == currentUserId) {
                    IconButton(
                        onClick = { onDeleteClick(comment.commentId) },
                        enabled = comment.deleteIconEnabled
                    ) {
                        if (comment.deleteIconEnabled) {
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
        }
    }
}

@PreviewScreenSizes
@Composable
fun CommentPrev() {
    DICTProjectTheme {
        CommentBox(0, comment = CommentState(0, 0, "Hello World Test Hello World Test Hello World Test Hello World Test Hello World Test", listOf(0), listOf("Danzwomen", "Aeonsexy", "Philippa"), UserDTO(0, "Danzwomen", imageStr), LocalDateTime.now(), listOf(0), true, true), {}, {}, {}, {}, listOf(
            loremIpsum))
    }
}