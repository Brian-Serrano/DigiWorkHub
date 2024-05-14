package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.DropDownState

/**
 * Dropdown used in dashboard for organization of tasks (filter, sort, group)
 *
 * @param[prefix] Name of dropdown
 * @param[dropDownState] The state/values for the dropdown
 * @param[icon] Leading icon for the dropdown
 * @param[onArrowClick] Invoked when the user click the dropdown and shows the menu of options that can be selected
 * @param[onDismissRequest] Hide the menu of options that can be selected
 * @param[onItemSelect] Invoked when user selected option in dropdown
 */
@Composable
fun RowScope.CustomDropDown(
    prefix: String,
    dropDownState: DropDownState,
    icon: ImageVector,
    onArrowClick: () -> Unit,
    onDismissRequest: () -> Unit,
    onItemSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
            .weight(1f)
            .clickable(onClick = onArrowClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(10.dp)
            )
            OneLineText(
                text = prefix,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
        DropdownMenu(
            expanded = dropDownState.expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            dropDownState.options.forEach {
                DropdownMenuItem(
                    text = {
                        OneLineText(
                            text = it,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    onClick = {
                        onItemSelect(it)
                    }
                )
            }
        }
    }
}