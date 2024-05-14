package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText

/**
 * Input field in add task page
 *
 * @param[text] Label for dropdown
 * @param[selected] The text in dropdown
 * @param[onArrowClick] Action that will invoked when the user click the arrow in dropdown (open dialog)
 * @param[modifier] (Optional) Add design/behavior to the component
 * @param[isOneLine] Whether the text should not wrap to next line. Overflows that happen will use ellipsis. Default is true.
 */
@Composable
fun CustomDropDown2(
    text: String,
    selected: String,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOneLine: Boolean = true
) {
    Column(modifier = modifier.padding(5.dp)) {
        OneLineText(
            text = text,
            modifier = Modifier.padding(top = 5.dp, start = 5.dp),
            style = MaterialTheme.typography.titleSmall
        )
        Box(
            modifier = Modifier
                .padding(5.dp)
                .height(IntrinsicSize.Min)
                .heightIn(0.dp, 200.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant),
                    MaterialTheme.shapes.extraSmall
                )
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isOneLine) {
                    OneLineText(
                        text = selected,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(0.dp, 200.dp)
                            .weight(1f)
                            .padding(10.dp)

                    )
                } else {
                    Text(
                        text = selected,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable(onClick = onArrowClick)
                )
            }
        }
    }
}