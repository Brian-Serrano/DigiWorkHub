package com.serrano.dictproject.customui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.dropdown.DueDropDown
import com.serrano.dictproject.customui.dropdown.PriorityDropDown
import com.serrano.dictproject.customui.dropdown.StatusDropDown
import com.serrano.dictproject.customui.dropdown.TypeDropDown
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.Subtask
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr
import java.time.LocalDateTime

@Composable
fun SubtaskBox(
    windowInfo: WindowInfo,
    task: Subtask,
    navigateToProfile: (Int) -> Unit,
    openViewDialog: () -> Unit,
    onDescriptionClick: () -> Unit,
    onStatusClick: () -> Unit,
    onAssigneeClick: () -> Unit,
    onPriorityClick: () -> Unit,
    onDueClick: () -> Unit,
    onTypeClick: () -> Unit
) {
    val assigneesComposable: @Composable RowScope.() -> Unit = {
        Row {
            Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
                task.assignees.forEach {
                    IconButton(onClick = openViewDialog) {
                        Icon(
                            bitmap = Utils.encodedStringToImage(it.image),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            IconButton(
                onClick = onAssigneeClick,
                modifier = Modifier
                    .size(25.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                MaterialTheme.shapes.small
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navigateToProfile(task.creator.id) },
                modifier = Modifier
                    .size(100.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    bitmap = Utils.encodedStringToImage(task.creator.image),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(100.dp)
                )
            }
            TextWithEditButton(
                text = task.description,
                onEditButtonClick = onDescriptionClick,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                isOneLine = false
            )
        }
        when (windowInfo.screenWidthInfo) {
            is WindowInfo.WindowType.Compact -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    DueDropDown(date = task.due, onClick = onDueClick)
                    StatusDropDown(text = task.status, onClick = onStatusClick)
                    PriorityDropDown(text = task.priority, onClick = onPriorityClick)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    assigneesComposable()
                    TypeDropDown(text = task.type, onClick = onTypeClick)
                }
            }
            is WindowInfo.WindowType.Medium, is WindowInfo.WindowType.Expanded -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
                    assigneesComposable()
                    StatusDropDown(text = task.status, onClick = onStatusClick)
                    PriorityDropDown(text = task.priority, onClick = onPriorityClick)
                    DueDropDown(date = task.due, onClick = onDueClick)
                    TypeDropDown(text = task.type, onClick = onTypeClick)
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SubtaskBoxPrev() {
    DICTProjectTheme {
        SubtaskBox(
            windowInfo = RememberWindowInfo(),
            task = Subtask(0, "sdfghjnrgfdisdmnfvdkbfrnvsdkbsdfghjnrgfdisdmnfvdk bfrnv sdkb sdfghj nrgfdis dm nfvdkbf rnvsdkbs df ghjnrgf disd mnfvdkbfrnvsdkb", LocalDateTime.now().plusDays(1), "LOW", "ON HOLD", "TASK", listOf(User(1, "Aeonsexy", imageStr), User(1, "Aeonsexy", imageStr), User(1, "Aeonsexy", imageStr)), User(1, "Aeonsexy", imageStr)),
            navigateToProfile = { /*TODO*/ },
            openViewDialog = { /*TODO*/ },
            onDescriptionClick = { /*TODO*/ },
            onStatusClick = { /*TODO*/ },
            onAssigneeClick = { /*TODO*/ },
            onPriorityClick = { /*TODO*/ },
            onDueClick = { /*TODO*/ }) {

        }
    }
}