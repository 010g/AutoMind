package com.example.automind.ui.detail.mindmap

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.Note
import com.example.automind.data.Repository
import kotlinx.coroutines.launch

class MindmapViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { Repository(database.noteDao(),database.settingsDao()) }

    val latestSavedTextId: MutableLiveData<Long?> = MutableLiveData(null)

    // LiveData to hold the Note data retrieved by latestSavedTextId
    private val _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?> get() = _selectedNote

    // Function to get data by latestSavedTextId
    fun getNoteByLatestSavedTextId() {
        viewModelScope.launch {
            val noteId = latestSavedTextId.value
            if(noteId != null) {
                _selectedNote.value = repository.getNoteById(noteId)
            } else {
                Log.e("MindmapViewModel", "latestSavedTextId is null")
            }
        }
    }
}
