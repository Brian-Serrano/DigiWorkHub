package com.serrano.dictproject.utils

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

data class DrawerData(
    val icon: ImageVector,
    val name: String,
    val action: suspend () -> Unit
)

data class Calendar(
    val date: LocalDate,
    val calendarTasks: List<CalendarTask>
)

data class CalendarTask(
    val taskId: Int,
    val name: String
)