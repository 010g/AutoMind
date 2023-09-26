package com.example.automind.ui.record

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.Note
import com.example.automind.data.NoteRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { NoteRepository(database.noteDao()) }

    val latestSavedTextId: MutableLiveData<Long?> = MutableLiveData(null)

    // Added LiveData objects
    val originalText: MutableLiveData<String> = MutableLiveData()
    val summaryText: MutableLiveData<String> = MutableLiveData()
    val listText: MutableLiveData<String> = MutableLiveData()
    val markdownContent: MutableLiveData<String> = MutableLiveData()

    var hasOriginal = false
    var hasSummary = false
    var hasList = false
    var hasMarkdown = false

    fun clearLiveData() {
        hasOriginal = false
        hasSummary = false
        hasList = false
        hasMarkdown = false
    }


    fun updateOriginalText(text: String) {
        originalText.postValue(text)
        Log.d("ViewModel", "Posted value to originalText: $text")
    }
    fun saveNoteData(
        tag: String,
        title: String,
        isLike: Boolean,
        text: String,
        summary: String,
        list: String,
        markdownContent: String
    ) {
        viewModelScope.launch {
            val id = repository.insertNote(
                tag,
                title,
                isLike,
                text,
                summary,
                list,
                markdownContent,
            )
            latestSavedTextId.postValue(id)
            val notes = repository.getAllNotes()
            Log.d("DatabaseTest after STT", "Getting all data from database...")
            for (note in notes) {
                Log.d("DatabaseTest after STT", "Transcribed Text in database: ${note.content}")
            }
        }
    }
}
