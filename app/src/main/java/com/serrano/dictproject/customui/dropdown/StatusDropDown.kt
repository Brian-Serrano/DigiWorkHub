package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.OneLineText

@Composable
fun StatusDropDown(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(
                when (text) {
                    "OPEN" -> Color(0xffff4dd2)
                    "ON HOLD" -> Color(0xffff4d4d)
                    "IN PROGRESS" -> Color(0xff4d4dff)
                    "COMPLETE" -> Color(0xff4dff4d)
                    else -> Color(0xffffff4d)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OneLineText(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            Divider(
                color = Color.White,
                modifier = Modifier.fillMaxHeight().width(1.dp)
            )
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp).padding(5.dp)
            )
        }
    }
}