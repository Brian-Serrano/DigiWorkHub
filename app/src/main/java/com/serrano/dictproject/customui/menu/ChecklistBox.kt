package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.ChecklistState
import com.serrano.dictproject.utils.DateUtils
import com.serrano.dictproject.utils.FileUtils

/**
 * Container used for received checklists
 *
 * @param[currentUserId] The id of user to determine who the user. User created the checklist is only the user can delete it.
 * @param[checklist] Checklist information
 * @param[onChecklistChange] Check/uncheck the checklist on the server
 * @param[openViewDialog] Invoked when the user click the assignees of checklist (open view assignee dialog)
 * @param[navigateToProfile] Invoked when the user click the image of user created the checklist (go to user profile)
 * @param[onDeleteClick] Delete the checklist on the server
 */
@Composable
fun ChecklistBox(
    currentUserId: Int,
    checklist: ChecklistState,
    onChecklistChange: (Int, Boolean) -> Unit,
    openViewDialog: () -> Unit,
    navigateToProfile: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .border(BorderStroke(2.dp, Color.Black), MaterialTheme.shapes.small)
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
                    onClick = { navigateToProfile(checklist.user.id) },
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        bitmap = FileUtils.encodedStringToImage(checklist.user.image),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
                OneLineText(
                    text = checklist.user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            OneLineText(
                text = DateUtils.dateTimeToDateTimeString(checklist.sentDate),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (checklist.isChecked) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                tint = if (checklist.isChecked) Color.Green else Color.Red,
                modifier = Modifier
                    .size(70.dp)
                    .padding(10.dp)
            )
            Text(
                text = checklist.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(5.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OneLineText(text = "Assignees: ")
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-25).dp)
                ) {
                    checklist.assignees.forEach {
                        IconButton(onClick = openViewDialog) {
                            Icon(
                                bitmap = FileUtils.encodedStringToImage(it.image),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (checklist.user.id == currentUserId) {
                    IconButton(
                        onClick = { onDeleteClick(checklist.checklistId) },
                        enabled = checklist.deleteIconEnabled
                    ) {
                        if (checklist.deleteIconEnabled) {
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
                CustomButton(
                    text = if (checklist.isChecked) "Uncheck" else "Check",
                    onClick = { onChecklistChange(checklist.checklistId, !checklist.isChecked) },
                    enabled = checklist.checkButtonEnabled
                )
            }
        }
    }
}