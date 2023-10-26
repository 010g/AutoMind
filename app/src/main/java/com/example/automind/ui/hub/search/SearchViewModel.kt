package com.example.automind.ui.hub.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.Note
import com.example.automind.data.Repository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application, val repository: Repository) : AndroidViewModel(application) {
    val searchResults = MutableLiveData<List<Note>>()

    fun searchNotesByTitle(query: String) {
        viewModelScope.launch {
            val results = repository.searchNotesByTitle(query)
            searchResults.value = results
        }
    }
}
