package com.example.automind.ui.hub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HubViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Hub Fragment"
    }
    val text: LiveData<String> = _text
}