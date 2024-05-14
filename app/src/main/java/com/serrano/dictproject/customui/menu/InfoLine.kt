package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText

/**
 * Used in settings as container for user information
 *
 * @param[label] Label for the user information
 * @param[text] The value of user information
 * @param[onArrowClick] (Optional) Action when the component clicked (open dialog to edit user information)
 */
@Composable
fun InfoLine(
    label: String,
    text: String,
    onArrowClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OneLineText(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(5.dp)
        )
        OneLineText(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp)
        )
        IconButton(onClick = onArrowClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}