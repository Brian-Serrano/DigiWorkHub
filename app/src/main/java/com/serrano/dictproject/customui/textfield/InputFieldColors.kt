package com.serrano.dictproject.customui.textfield

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

@Composable
fun InputFieldColors(): TextFieldColors {
    val contentColor = MaterialTheme.colorScheme.surfaceVariant
    val containerColor = MaterialTheme.colorScheme.onSurfaceVariant

    return TextFieldDefaults.colors(
        focusedTextColor = contentColor,
        unfocusedTextColor = contentColor,
        disabledTextColor = contentColor,
        errorTextColor = contentColor,
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor,
        errorContainerColor = containerColor,
        cursorColor = contentColor,
        errorCursorColor = contentColor,
        focusedIndicatorColor = contentColor,
        unfocusedIndicatorColor = contentColor,
        disabledIndicatorColor = contentColor,
        errorIndicatorColor = contentColor,
        focusedLeadingIconColor = contentColor,
        unfocusedLeadingIconColor = contentColor,
        disabledLeadingIconColor = contentColor,
        errorLeadingIconColor = contentColor,
        focusedTrailingIconColor = contentColor,
        unfocusedTrailingIconColor = contentColor,
        disabledTrailingIconColor = contentColor,
        errorTrailingIconColor = contentColor,
        focusedLabelColor = contentColor,
        unfocusedLabelColor = contentColor,
        disabledLabelColor = contentColor,
        errorLabelColor = contentColor,
        focusedPlaceholderColor = contentColor,
        unfocusedPlaceholderColor = contentColor,
        disabledPlaceholderColor = contentColor,
        errorPlaceholderColor = contentColor,
        focusedSupportingTextColor = contentColor,
        unfocusedSupportingTextColor = contentColor,
        disabledSupportingTextColor = contentColor,
        errorSupportingTextColor = contentColor,
        focusedPrefixColor = contentColor,
        unfocusedPrefixColor = contentColor,
        disabledPrefixColor = contentColor,
        errorPrefixColor = contentColor,
        focusedSuffixColor = contentColor,
        unfocusedSuffixColor = contentColor,
        disabledSuffixColor = contentColor,
        errorSuffixColor = contentColor
    )
}