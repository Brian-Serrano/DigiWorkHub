package com.serrano.dictproject.customui.menu

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
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.WindowInfo
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.dropdown.CustomDropDown3
import com.serrano.dictproject.customui.textfield.ScrollableTextField
import com.serrano.dictproject.utils.AddSubtaskState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.FileUtils

/**
 * Component used for adding subtasks on task in about task page
 *
 * @param[windowInfo] Used for responsive/adaptive content
 * @param[addSubtaskState] State for the menu
 * @param[onDescriptionChange] Change description of subtask
 * @param[onOpenDueDialog] Open dialog where the user can select due date for subtask
 * @param[onOpenPriorityDialog] Open dialog where the user can select priority for subtask
 * @param[onOpenTypeDialog] Open dialog where the user can select type for subtask
 * @param[onOpenAssigneeDialog] Open dialog where the user can select assignees for subtask
 * @param[onUserClick] Open dialog where the user see the selected assignees
 * @param[addSubtask] Add the subtask of task on the server
 */
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
                        selected = DateUtils.dateTimeToDateTimeString(addSubtaskState.due),
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
                                    bitmap = FileUtils.encodedStringToImage(it.image),
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
                                    bitmap = FileUtils.encodedStringToImage(it.image),
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
                        selected = DateUtils.dateTimeToDateTimeString(addSubtaskState.due),
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