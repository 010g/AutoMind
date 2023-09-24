package com.example.automind.ui.detail.mindmap

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.TranscribedTextRepository
import kotlinx.coroutines.launch

class MindmapViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { TranscribedTextRepository(database.transcribedTextDao()) }

    val latestSavedTextId: MutableLiveData<Long?> = MutableLiveData(null)

    fun updateMindmapMarkdownForTranscribedText(id: Long, mindmapMarkdown: String) {
        viewModelScope.launch {
            repository.updateMindmapMarkdownForId(id, mindmapMarkdown)

            val transcribedTexts = repository.getAllTranscribedTexts()
            Log.d("DatabaseTest after saving mindmap", "Getting all data from database...")
            for (text in transcribedTexts) {
                Log.d("DatabaseTest after saving mindmap", "Transcribed Text in database: ${text.content}, Markdown: ${text.mindmapMarkdown}")
            }
        }
    }
}
