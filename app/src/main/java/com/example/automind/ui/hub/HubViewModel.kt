package com.example.automind.ui.hub

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class HubViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    val repository by lazy { Repository(database.noteDao(),database.settingsDao()) }

    private val _isLikes = MutableLiveData<List<HorizontalItem>>()
    val isLikes: LiveData<List<HorizontalItem>> get() = _isLikes
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterDataByIsLike() {
        viewModelScope.launch {
            val allNotes = repository.getAllNotes()
            Log.d("all data in filterDataByIsLike", "Getting all data from database...")
            for (note in allNotes) {
                Log.d("all data in filterDataByIsLike", note.toString())
            }

            val notes = repository.getNoteByIsLike()
            if (notes != null) {
                val format = SimpleDateFormat("yyyy.MM.dd HH:mm")

                _isLikes.value = notes.map { HorizontalItem(it.id, format.format(Date(it.timestamp)), it.title, it.content, true) }
                Log.d("isLike in filterDataByIsLike", isLikes.value.toString())

            }
        }
    }

    fun updateIsLikeForId(id: Long, isSelected: Boolean): Job {
        return viewModelScope.launch {
            repository.updateIsLikeForId(id, !isSelected)
        }
    }
}

