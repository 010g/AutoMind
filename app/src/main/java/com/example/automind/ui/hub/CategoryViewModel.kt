package com.example.automind.ui.hub

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.Note
import com.example.automind.data.NoteDao
import com.example.automind.data.NoteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { NoteRepository(database.noteDao()) }

    private val _works = MutableLiveData<List<CategoryItem>>()
    val works: LiveData<List<CategoryItem>> get() = _works

    private val _ideas = MutableLiveData<List<CategoryItem>>()
    val ideas: LiveData<List<CategoryItem>> get() = _ideas

    private val _personals = MutableLiveData<List<CategoryItem>>()
    val personals: LiveData<List<CategoryItem>> get() = _personals
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterDataByTag(tag: String) {
        viewModelScope.launch {
            val allNotes = repository.getAllNotes()
            Log.d("all data in filterDataByTag", "Getting all data from database...")
            for (note in allNotes) {
                Log.d("all data in filterDataByTag", note.toString())
            }

            val notes = repository.getNoteByTag(tag)
            Log.d("tag in filterDataByTag", tag)
            if (notes != null) {
                val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
                 when(tag){
                     "Work" -> {
                         _works.value = notes.map { CategoryItem(it.id, format.format(Date(it.timestamp)), it.title, it.content) }
                         Log.d("work in filterDataByTag", works.value.toString())
                     }
                     "Ideas" -> {
                         _ideas.value = notes.map { CategoryItem(it.id, format.format(Date(it.timestamp)), it.title, it.content) }
                         Log.d("ideas in filterDataByTag", ideas.value.toString())
                     }
                     "Personal" -> {
                         _personals.value = notes.map { CategoryItem(it.id, format.format(Date(it.timestamp)), it.title, it.content) }
                         Log.d("personal in filterDataByTag", personals.value.toString())
                     }
                }
            }
        }
    }
}

