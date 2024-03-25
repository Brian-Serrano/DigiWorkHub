package com.serrano.dictproject.customui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr
import java.time.LocalDateTime

@Composable
fun MessageBox(
    user: User,
    sentDate: LocalDateTime,
    description: String,
    onUserClick: (Int) -> Unit,
    onViewClick: () -> Unit
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
                    onClick = { onUserClick(user.id) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        bitmap = Utils.encodedStringToImage(user.image),
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
            OneLineText(
                text = Utils.dateTimeToDateTimeString(sentDate)
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 5.dp)
        )
        CustomButton(
            text = "VIEW",
            onClick = onViewClick,
            modifier = Modifier.align(Alignment.End).padding(5.dp)
        )
    }
}

@Preview
@Composable
fun MBPrev() {
    DICTProjectTheme {
        MessageBox(
            user = User(1, "AdvyGay", imageStr),
            sentDate = LocalDateTime.now(),
            description = "Here, len is Python's built-in function that counts the length of an element. In this case, the sorted() method sorts the list based on the length of the element. For example,",
            onUserClick = {},
            onViewClick = {}
        )
    }
}