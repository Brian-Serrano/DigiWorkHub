package com.serrano.dictproject.customui.text

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * A text that will not wrap to next line used by most component of application
 *
 * @param[text] Text
 * @param[modifier] (Optional) Add design/behavior to the text
 * @param[style] (Optional) Add style to text
 * @param[color] (Optional) Add color to text
 * @param[fontWeight] (Optional) Add weight to text
 */
@Composable
fun OneLineText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = Color.Black,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = modifier.padding(5.dp),
        style = style,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontWeight = fontWeight
    )
}