package com.serrano.dictproject.customui.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText

@Composable
fun TextWithEditButton(
    text: String,
    onEditButtonClick: () -> Unit,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight = FontWeight.Normal,
    isOneLine: Boolean = true,
    isJustify: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(5.dp)
    ) {
        if (isOneLine) {
            OneLineText(
                text = text,
                style = style,
                fontWeight = fontWeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textAlign = if (isJustify) TextAlign.Justify else null
            )
        }
        IconButton(
            onClick = onEditButtonClick,
            modifier = Modifier.size(25.dp).padding(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}