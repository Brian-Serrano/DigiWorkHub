package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.textfield.ScrollableTextField
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.UserDTO

/**
 * Component used for adding checklist on task in about task page
 *
 * @param[checklistInput] Text for the checklist description
 * @param[buttonEnabled] Whether button is enabled. Should only disable if there are server request running.
 * @param[assigneesAdded] The assignees for the checklist
 * @param[updateChecklistInput] Update the checklist description
 * @param[onUserClick] Invoked when the user click the selected assignees (open view assignee dialog)
 * @param[onPersonAddClick] Invoked when the user click the person add icon (open dialog to select assignee)
 * @param[addChecklist] Add the checklist of task on the server
 */
@Composable
fun AddChecklistMenu(
    checklistInput: String,
    buttonEnabled: Boolean,
    assigneesAdded: List<UserDTO>,
    updateChecklistInput: (String) -> Unit,
    onUserClick: () -> Unit,
    onPersonAddClick: () -> Unit,
    addChecklist: () -> Unit
) {
    ScrollableTextField(
        value = checklistInput,
        onValueChange = updateChecklistInput,
        placeholderText = "Enter checklist description"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPersonAddClick) {
            Icon(
                imageVector = Icons.Filled.PersonAdd,
                contentDescription = null
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy((-25).dp)) {
            assigneesAdded.forEach {
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
            text = "ADD CHECKLIST",
            onClick = addChecklist,
            enabled = buttonEnabled,
            modifier = Modifier.padding(10.dp)
        )
    }
}