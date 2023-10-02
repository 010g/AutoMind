package com.example.automind.ui.settings

import androidx.lifecycle.*
import com.example.automind.data.Repository
import com.example.automind.data.Setting
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: Repository) : ViewModel() {

    private val setting: LiveData<Setting> = repository.getSetting(1)

    val inputLanguage: LiveData<String> = setting.map { it.inputLanguage }
    val outputLanguage: LiveData<String> = setting.map { it.outputLanguage }
    val writingStyle: LiveData<String> = setting.map { it.writingStyle }
    val outputLength: LiveData<Int> = setting.map { it.outputLength }

    fun updateSetting(updatedSetting: Setting) = viewModelScope.launch {
        repository.updateSetting(updatedSetting)
    }

    fun insertSetting(newSetting: Setting) = viewModelScope.launch {
        repository.insertSetting(newSetting)
    }

    fun updateInputLanguage(id: Int, language: String) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        repository.updateSetting(currentSetting.copy(inputLanguage = language))
    }

    fun updateOutputLanguage(id: Int, language: String) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        repository.updateSetting(currentSetting.copy(outputLanguage = language))
    }

    fun setOutputLength(newLength: Int) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        repository.updateSetting(currentSetting.copy(outputLength = newLength))
    }

    fun setWritingStyle(newStyle: String) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        repository.updateSetting(currentSetting.copy(writingStyle = newStyle))
    }

    fun updateOutputLength(newLength: Int) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        repository.updateSetting(currentSetting.copy(outputLength = newLength))
    }
}
