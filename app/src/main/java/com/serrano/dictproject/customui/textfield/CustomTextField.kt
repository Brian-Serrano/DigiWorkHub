package com.serrano.dictproject.customui.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Text field commonly used by the application
 *
 * @param[value] The text on the field
 * @param[onValueChange] Change the text on the field
 * @param[placeholderText] Placeholder when the text is empty
 * @param[visualTransformation] (Optional) Transformation to the text on field. Used by password input fields and comment with mentions.
 * @param[trailingIcon] Trailing icon of the input field. Used by password input fields.
 * @param[supportingText] Text below the input field. Used by fields on signup for instant validation.
 */
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
        trailingIcon = trailingIcon,
        placeholder = {
            Text(text = placeholderText, style = MaterialTheme.typography.bodyMedium)
        },
        visualTransformation = visualTransformation,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        singleLine = true,
        supportingText = supportingText,
        colors = InputFieldColors()
    )
}