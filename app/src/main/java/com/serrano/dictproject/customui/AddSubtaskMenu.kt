package com.serrano.dictproject.customui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.dropdown.CustomDropDown3
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.AddSubtaskState
import com.serrano.dictproject.utils.User
import com.serrano.dictproject.utils.Utils
import com.serrano.dictproject.utils.imageStr

@Composable
fun AddSubtaskMenu(
    windowInfo: WindowInfo,
    addSubtaskState: AddSubtaskState,
    onDescriptionChange: (String) -> Unit,
    onOpenDueDialog: () -> Unit,
    onOpenPriorityDialog: () -> Unit,
    onOpenTypeDialog: () -> Unit,
    onOpenAssigneeDialog: () -> Unit,
    onUserClick: () -> Unit,
    addSubtask: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                MaterialTheme.shapes.small
            )
    ) {
        when (windowInfo.screenWidthInfo) {
            is WindowInfo.WindowType.Compact, is WindowInfo.WindowType.Medium -> {
                ScrollableTextField(
                    value = addSubtaskState.description,
                    onValueChange = onDescriptionChange,
                    placeholderText = "Enter subtask description",
                    modifier = Modifier.padding(5.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomDropDown3(
                        selected = Utils.dateTimeToDateTimeString(addSubtaskState.due),
                        onClick = onOpenDueDialog
                    )
                    CustomDropDown3(
                        selected = addSubtaskState.priority,
                        onClick = onOpenPriorityDialog
                    )
                    CustomDropDown3(
                        selected = addSubtaskState.type,
                        onClick = onOpenTypeDialog
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenAssigneeDialog) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = null
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
                        addSubtaskState.assignees.forEach {
                            IconButton(onClick = onUserClick) {
                                Icon(
                                    bitmap = Utils.encodedStringToImage(it.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    CustomButton(
                        text = "ADD SUBTASK",
                        onClick = addSubtask,
                        enabled = addSubtaskState.buttonEnabled
                    )
                }
            }
            is WindowInfo.WindowType.Expanded -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScrollableTextField(
                        value = addSubtaskState.description,
                        onValueChange = onDescriptionChange,
                        placeholderText = "Enter subtask description",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onOpenAssigneeDialog) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = null
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
                        addSubtaskState.assignees.forEach {
                            IconButton(onClick = onUserClick) {
                                Icon(
                                    bitmap = Utils.encodedStringToImage(it.image),
                                    contentDescription = null,
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomDropDown3(
                        selected = Utils.dateTimeToDateTimeString(addSubtaskState.due),
                        onClick = onOpenDueDialog
                    )
                    CustomDropDown3(
                        selected = addSubtaskState.priority,
                        onClick = onOpenPriorityDialog
                    )
                    CustomDropDown3(
                        selected = addSubtaskState.type,
                        onClick = onOpenTypeDialog
                    )
                    CustomButton(
                        text = "ADD SUBTASK",
                        onClick = addSubtask,
                        enabled = addSubtaskState.buttonEnabled
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun ASMPrev() {
    DICTProjectTheme {
        AddSubtaskMenu(
            windowInfo = RememberWindowInfo(),
            addSubtaskState = AddSubtaskState(assignees = listOf(User(1, "Aeonsexy", imageStr), User(1, "Aeonsexy", imageStr), User(1, "Aeonsexy", imageStr), User(1, "Aeonsexy", imageStr))),
            onDescriptionChange = { /*TODO*/ },
            onOpenDueDialog = { /*TODO*/ },
            onOpenPriorityDialog = { /*TODO*/ },
            onOpenTypeDialog = { /*TODO*/ },
            onOpenAssigneeDialog = { /*TODO*/ },
            onUserClick = { /*TODO*/ }
        ) {

        }
    }
}