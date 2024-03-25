package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.CustomButton
import com.serrano.dictproject.customui.OneLineText
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr

@Composable
fun ViewAssigneeDialog(
    assignees: List<User>,
    onUserClick: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
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
            OneLineText(
                text = "USERS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                assignees.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onUserClick(it.id) }) {
                            Icon(
                                bitmap = Utils.encodedStringToImage(it.image),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                        OneLineText(
                            text = it.name,
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        )
                    }
                }
            }
            CustomButton(
                text = "CLOSE",
                onClick = onDismissRequest
            )
        }
    }
}

@Preview
@Composable
fun VADPrev() {
    DICTProjectTheme {
        ViewAssigneeDialog(assignees = listOf(User(1, "Aeonsexy", imageStr), User(1, "XCreatorGirl", imageStr), User(1, "ItzAdvyGay", imageStr)), onUserClick = {}, {})
    }
}