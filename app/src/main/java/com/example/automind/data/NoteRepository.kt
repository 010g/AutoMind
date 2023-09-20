package com.example.automind.data

class TranscribedTextRepository(private val transcribedTextDao: TranscribedTextDao) {
    suspend fun insertTranscribedText(text: String): Long {
        val transcribedText = TranscribedText(content = text)
        return transcribedTextDao.insertTranscribedText(transcribedText)
    }

    suspend fun getAllTranscribedTexts(): List<TranscribedText> {
        return transcribedTextDao.getAllTranscribedTexts()
    }


    suspend fun deleteAllTranscribedTexts() {
        transcribedTextDao.deleteAllTranscribedTexts()
    }


    suspend fun updateMindmapMarkdownForId(id: Long, markdown: String) {
        transcribedTextDao.updateMindmapMarkdownForId(id, markdown)
    }

}