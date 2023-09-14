package com.example.automind.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TranscribedText::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcribedTextDao(): TranscribedTextDao
}