package com.example.automind.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranscribedTextDao {

    @Insert
    suspend fun insertTranscribedText(transcribedText: TranscribedText)

    @Query("SELECT * FROM transcribed_text")
    suspend fun getAllTranscribedTexts(): List<TranscribedText>
}