package com.example.automind.data

class TranscribedTextRepository(private val transcribedTextDao: TranscribedTextDao) {
    suspend fun insertTranscribedText(text: String) {
        val transcribedText = TranscribedText(text = text)
        transcribedTextDao.insertTranscribedText(transcribedText)
    }

    suspend fun getAllTranscribedTexts(): List<TranscribedText> {
        return transcribedTextDao.getAllTranscribedTexts()
    }


    suspend fun deleteAllTranscribedTexts() {
        transcribedTextDao.deleteAllTranscribedTexts()
    }


}