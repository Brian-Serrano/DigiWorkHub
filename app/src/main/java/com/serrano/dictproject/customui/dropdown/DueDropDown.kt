package com.serrano.dictproject.customui.dropdown

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.DateUtils
import java.time.LocalDateTime

/**
 * Container for due date of tasks/subtasks
 *
 * @param[date] Date that will be shown as text
 * @param[onClick] Invoked when the user click the container (open dialog and edit due date)
 * @param[includeTime] Whether to include time. Default is false
 */
@Composable
fun DueDropDown(
    date: LocalDateTime,
    onClick: () -> Unit,
    includeTime: Boolean = false
) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(if (date.isBefore(LocalDateTime.now())) Color(0xffff4d4d) else Color(0xff4dff4d))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        OneLineText(
            text = if (includeTime) DateUtils.dateTimeToDateTimeString(date) else DateUtils.dateTimeToDateString(date),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}