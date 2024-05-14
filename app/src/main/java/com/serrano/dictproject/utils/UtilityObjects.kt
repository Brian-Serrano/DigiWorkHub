package com.serrano.dictproject.utils

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

/**
 * Data for drawer items
 *
 * @param[icon] Icon for the drawer item
 * @param[name] Text/Name for the drawer item
 * @param[action] An action that will be done when the user click the drawer item
 */
data class DrawerData(
    val icon: ImageVector,
    val name: String,
    val action: suspend () -> Unit
)

/**
 * Calendar data used in Calendar View in Dashboard page
 *
 * @param[date] Date of a month
 * @param[calendarTasks] The task with the due date matches the date argument
 */
data class Calendar(
    val date: LocalDate,
    val calendarTasks: List<CalendarTask>
)

/**
 * A task item from the Calendar Object
 *
 * @param[taskId] Task Id
 * @param[name] Task Name
 */
data class CalendarTask(
    val taskId: Int,
    val name: String
)