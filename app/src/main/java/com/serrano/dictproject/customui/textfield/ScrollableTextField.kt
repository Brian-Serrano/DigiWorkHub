package com.serrano.dictproject.customui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Used by inputs that can have many content
 *
 * @param[value] Text on the field
 * @param[onValueChange] Change the text on the field
 * @param[placeholderText] Placeholder when the text in the field is empty
 * @param[modifier] (Optional) Add design/behavior to the text field
 */
@Composable
fun ScrollableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = {
            Text(text = placeholderText, style = MaterialTheme.typography.bodyMedium)
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .heightIn(0.dp, 200.dp)
            .verticalScroll(rememberScrollState()),
        colors = InputFieldColors()
    )
}