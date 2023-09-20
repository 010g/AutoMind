package com.example.automind.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranscribedTextDao {

    @Insert
    suspend fun insertTranscribedText(transcribedText: TranscribedText): Long

    @Query("SELECT * FROM transcribed_texts")
    suspend fun getAllTranscribedTexts(): List<TranscribedText>

    @Query("DELETE FROM transcribed_texts")
    suspend fun deleteAllTranscribedTexts()

    @Query("UPDATE transcribed_texts SET mindmapMarkdown = :markdown WHERE id = :id")
    suspend fun updateMindmapMarkdownForId(id: Long, markdown: String)

}