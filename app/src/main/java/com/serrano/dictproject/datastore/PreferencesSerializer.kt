package com.serrano.dictproject.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
class PreferencesSerializer : Serializer<Preferences> {

    override val defaultValue: Preferences
        get() = Preferences()

    override suspend fun readFrom(input: InputStream): Preferences {
        return try {
            Json.decodeFromString(
                deserializer = Preferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (se: SerializationException) {
            se.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = Preferences.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}