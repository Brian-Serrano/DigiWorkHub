package com.serrano.dictproject.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateUtils {

    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

    fun dateTimeToDateString(date: LocalDateTime): String {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }

    fun dateTimeToDateTimeString(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a"))
    }

    fun dateTimeToTimeString(date: LocalDateTime): String {
        return LocalTime.of(date.hour, date.minute)
            .format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    fun dateTimeToDate(date: LocalDateTime): LocalDate {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
    }

    fun dateToDateString(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }
}