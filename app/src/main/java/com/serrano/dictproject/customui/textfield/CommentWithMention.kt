package com.serrano.dictproject.customui.textfield

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.serrano.dictproject.utils.UserDTO

/**
 * Text transformation for comments with mentions
 *
 * @param[mentions] The mentioned users to add to the text
 */
class CommentWithMention(
    private val mentions: List<UserDTO>
): VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = buildAnnotatedString {
                mentions.forEach {
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = Color.Blue
                        )
                    ) {
                        append("@ ${it.name}")
                    }
                    append(" ")
                }
                append(text)
            },
            offsetMapping = object : OffsetMapping {

                override fun originalToTransformed(offset: Int): Int {
                    return offset + mentions.sumOf { it.name.length + 3 }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return (offset - mentions.sumOf { it.name.length + 3 }).coerceAtLeast(0)
                }
            }
        )
    }
}