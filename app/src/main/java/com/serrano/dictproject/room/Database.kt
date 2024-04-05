package com.serrano.dictproject.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TaskPart::class,
        Task::class,
        Comment::class,
        Subtask::class,
        Checklist::class,
        Attachment::class,
        ProfileData::class,
        MessagePart::class,
        Message::class,
        MessageReply::class,
        User::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun dao(): Dao
}