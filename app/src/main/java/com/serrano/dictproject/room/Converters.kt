package com.serrano.dictproject.room

import androidx.room.TypeConverter
import com.serrano.dictproject.utils.DateUtils
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun stringToDate(string: String): LocalDateTime {
        return LocalDateTime.parse(string, DateUtils.DATE_TIME_FORMATTER)
    }

    @TypeConverter
    fun dateToString(date: LocalDateTime): String {
        return date.format(DateUtils.DATE_TIME_FORMATTER)
    }

    @TypeConverter
    fun stringToIntegerList(string: String): List<Int> {
        return if (string.isEmpty()) emptyList() else string.split(",").map { it.toInt() }
    }

    @TypeConverter
    fun integerListToString(integerList: List<Int>): String {
        return integerList.joinToString(",")
    }

    @TypeConverter
    fun stringToStringList(string: String): List<String> {
        return if (string.isEmpty()) emptyList() else string.split(",")
    }

    @TypeConverter
    fun stringListToString(stringList: List<String>): String {
        return stringList.joinToString(",")
    }
}