package com.example.automind.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.security.Timestamp

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tag: String = "work",
    val title: String = "",
    val isLike: Boolean = false,
    val content: String,
    val summary: String,
    val list: String,
    val mindmapMarkdown: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)