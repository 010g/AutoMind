package com.example.automind.ui.record

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.TranscribedTextRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { TranscribedTextRepository(database.transcribedTextDao()) }

    val latestSavedTextId: MutableLiveData<Long?> = MutableLiveData(null)
    fun saveTranscribedData(transcribedData: String) {
        viewModelScope.launch {
            val id = repository.insertTranscribedText(transcribedData)
            latestSavedTextId.postValue(id)
            val transcribedTexts = repository.getAllTranscribedTexts()
            Log.d("DatabaseTest after STT", "Getting all data from database...")
            for (text in transcribedTexts) {
                Log.d("DatabaseTest after STT", "Transcribed Text in database: ${text.content}, Markdown: ${text.mindmapMarkdown}")
            }
        }
    }
}
