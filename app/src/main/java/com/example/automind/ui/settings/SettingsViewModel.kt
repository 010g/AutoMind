package com.example.automind.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _inputLanguage = MutableLiveData<String>()
    val inputLanguage: LiveData<String> get() = _inputLanguage

    private val _outputLanguage = MutableLiveData<String>()
    val outputLanguage: LiveData<String> get() = _outputLanguage

    private val _writingStyle = MutableLiveData<String>()
    val writingStyle: LiveData<String> get() = _writingStyle

    private val _outputLength = MutableLiveData<Int>()
    val outputLength: LiveData<Int> get() = _outputLength



    fun setInputLanguage(language: String) {
        _inputLanguage.value = language
    }
    fun setOutputLanguage(language: String) {
        _outputLanguage.value = language
    }
    fun setWritingStyle(style: String) {
        _writingStyle.value = style
    }

    fun setOutputLength(length: Int) {
        _outputLength.value = length
        // you might want to save the value to SharedPreferences or a database here
    }
}