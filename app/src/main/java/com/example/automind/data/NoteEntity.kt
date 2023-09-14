package com.example.automind.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcribed_text")
data class TranscribedText(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String
)