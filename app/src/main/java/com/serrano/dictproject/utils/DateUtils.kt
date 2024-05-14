package com.serrano.dictproject.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Utility class for processing dates
 */
object DateUtils {

    /**
     * The formatter used for dates when they are send to backend (format to string), received from backend (parse to dates), and stored in room database as string
     */
    val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")

    /**
     * Transform LocalDateTime to a Date String
     *
     * Example:
     * ```
     * DateUtils.dateTimeToDateString(
     *     LocalDateTime.of(2024, 1, 1, 6, 30)
     * )
     * // January 1, 2024
     * ```
     *
     * @param[date] The date to be transformed
     *
     * @return Stringed date
     */
    fun dateTimeToDateString(date: LocalDateTime): String {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }

    /**
     * Transform LocalDateTime to a Date and Time String
     *
     * Example:
     * ```
     * DateUtils.dateTimeToDateTimeString(
     *     LocalDateTime.of(2024, 1, 1, 6, 30)
     * )
     * // January 1, 2024 6:30 AM
     * ```
     *
     * @param[date] The date to be transformed
     *
     * @return Stringed date
     */
    fun dateTimeToDateTimeString(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a"))
    }

    /**
     * Transform LocalDateTime to a Time String
     *
     * Example:
     * ```
     * DateUtils.dateTimeToTimeString(
     *     LocalDateTime.of(2024, 1, 1, 6, 30)
     * )
     * // 6:30 AM
     * ```
     *
     * @param[date] The date to be transformed
     *
     * @return Stringed date
     */
    fun dateTimeToTimeString(date: LocalDateTime): String {
        return LocalTime.of(date.hour, date.minute)
            .format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    /**
     * Transform LocalDateTime object to LocalDate Object
     *
     * Example:
     * ```
     * val date = DateUtils.dateTimeToDate(
     *     LocalDateTime.of(2024, 1, 1, 6, 30)
     * )
     * print(date is LocalDate) // true
     * ```
     *
     * @param[date] The LocalDateTime to be transformed
     *
     * @return LocalDate Object
     */
    fun dateTimeToDate(date: LocalDateTime): LocalDate {
        return LocalDate.of(date.year, date.month, date.dayOfMonth)
    }

    /**
     * Transform LocalDate to a Date String
     *
     * Example:
     * ```
     * DateUtils.dateToDateString(
     *     LocalDate.of(2024, 1, 1)
     * )
     * // January 1, 2024
     * ```
     *
     * @param[date] The date to be transformed
     *
     * @return Stringed date
     */
    fun dateToDateString(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
    }
}