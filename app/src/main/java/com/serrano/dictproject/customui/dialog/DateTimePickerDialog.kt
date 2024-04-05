package com.serrano.dictproject.customui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.serrano.dictproject.customui.button.CustomButton
import com.serrano.dictproject.customui.button.TextWithEditButton
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.DateDialogState
import com.serrano.dictproject.utils.DateUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    text: String,
    dateDialogState: DateDialogState,
    onDismissRequest: () -> Unit,
    onApplyClick: (Int, LocalDateTime) -> Unit,
    datePicker: (Boolean) -> Unit,
    timePicker: (Boolean) -> Unit,
    selected: (LocalDateTime) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateDialogState.selected.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = dateDialogState.selected.hour,
        initialMinute = dateDialogState.selected.minute
    )

    val dateMapper = { dps: DatePickerState, tps: TimePickerState ->
        LocalDateTime
            .ofInstant(
                Instant.ofEpochMilli(dps.selectedDateMillis!!),
                ZoneId.systemDefault()
            )
            .plusHours(tps.hour.toLong())
            .plusMinutes(tps.minute.toLong())
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0x55000000)))
    Dialog(onDismissRequest = onDismissRequest) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .height(IntrinsicSize.Min)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OneLineText(
                    text = "Edit $text",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextWithEditButton(
                    text = "Selected Date: ${DateUtils.dateTimeToDateString(dateDialogState.selected)}",
                    onEditButtonClick = { datePicker(true) }
                )
                TextWithEditButton(
                    text = "Selected Time: ${DateUtils.dateTimeToTimeString(dateDialogState.selected)}",
                    onEditButtonClick = { timePicker(true) }
                )
                Row {
                    CustomButton(
                        text = "APPLY",
                        onClick = {
                            onApplyClick(
                                dateDialogState.taskId,
                                dateMapper(datePickerState, timePickerState)
                            )
                            onDismissRequest()
                        }
                    )
                    CustomButton(
                        text = "CANCEL",
                        onClick = onDismissRequest
                    )
                }
            }
        }
    }
    if (dateDialogState.datePickerEnabled) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0x55000000)))
        DatePickerDialog(
            onDismissRequest = { datePicker(false) },
            confirmButton = {
                CustomButton(
                    text = "OK",
                    onClick = {
                        selected(dateMapper(datePickerState, timePickerState))
                    }
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            titleContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            headlineContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            weekdayContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            subheadContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            yearContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            currentYearContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedYearContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            dayContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedDayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            todayContentColor = MaterialTheme.colorScheme.surfaceVariant,
                            todayDateBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    }
    if (dateDialogState.timePickerEnabled) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0x55000000)))
        TimePickerDialog(
            onDismissRequest = { timePicker(false) },
            confirmButton = {
                CustomButton(
                    text = "OK",
                    onClick = {
                        selected(dateMapper(datePickerState, timePickerState))
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = MaterialTheme.colorScheme.surface,
                        clockDialSelectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        selectorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        periodSelectorBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        periodSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surface,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.surfaceVariant,
                        timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}