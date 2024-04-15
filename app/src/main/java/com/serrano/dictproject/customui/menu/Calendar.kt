package com.serrano.dictproject.customui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.serrano.dictproject.customui.text.OneLineText
import com.serrano.dictproject.utils.MiscUtils
import com.serrano.dictproject.utils.TaskPartDTO
import java.time.format.DateTimeFormatter

@Composable
fun Calendar(
    tasks: List<TaskPartDTO>,
    calendarTabIdx: Int,
    updateCalendarIdx: (Int) -> Unit,
    navigate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = MiscUtils.getCalendarData(calendarTabIdx, tasks)
    val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(5.dp)) {
            OneLineText(
                text = dates[dates.size / 2].date.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = { updateCalendarIdx(calendarTabIdx - 1) }) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = { updateCalendarIdx(calendarTabIdx + 1) }) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next"
                )
            }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(items = days) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)),
                ) {
                    OneLineText(
                        text = it,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
            }
            items(items = dates) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer)),
                ) {
                    OneLineText(
                        text = it.date.dayOfMonth.toString(),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .heightIn(0.dp, 100.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        it.calendarTasks.forEach {
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    )
                                    .clickable { navigate(it.taskId) }
                            ) {
                                OneLineText(text = it.name)
                            }
                        }
                    }
                }
            }
        }
    }
}