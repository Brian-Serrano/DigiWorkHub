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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.textfield.ScrollableTextField
import com.serrano.dictproject.ui.theme.DICTProjectTheme
import com.serrano.dictproject.utils.FileUtils
import com.serrano.dictproject.utils.UserDTO
import com.serrano.dictproject.utils.imageStr

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

@Preview
@Composable
fun ACMPrev() {
    DICTProjectTheme {
        AddChecklistMenu(
            checklistInput = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            true,
            listOf(UserDTO(1, "Aeonsexy", imageStr), UserDTO(1, "Aeonsexy", imageStr), UserDTO(1, "Aeonsexy", imageStr)),
            updateChecklistInput = {},
            {},
            {},
            {}
        )
    }
}