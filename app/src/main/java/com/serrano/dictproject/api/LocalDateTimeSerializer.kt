package com.serrano.dictproject.api

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.serrano.dictproject.utils.DateUtils
import java.lang.reflect.Type
import java.time.LocalDateTime


internal class LocalDateTimeSerializer : JsonSerializer<LocalDateTime?> {

    override fun serialize(
        localDateTime: LocalDateTime?,
        srcType: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(DateUtils.DATE_TIME_FORMATTER.format(localDateTime))
    }
}