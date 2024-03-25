package com.serrano.dictproject.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.serrano.dictproject.utils.Utils
import java.lang.reflect.Type
import java.time.LocalDateTime


internal class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime?> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return LocalDateTime.parse(json.asString, Utils.dateTimeFormatter)
    }
}