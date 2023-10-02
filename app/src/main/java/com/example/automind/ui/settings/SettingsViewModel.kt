package com.example.automind.ui.settings

import android.app.Application
import androidx.lifecycle.*
import com.example.automind.data.AppDatabase
import com.example.automind.data.Repository
import com.example.automind.data.Setting
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    val repository by lazy { Repository(database.noteDao(),database.settingsDao()) }

    private val setting: MutableLiveData<Setting?> = MutableLiveData()

    val inputLanguage: MutableLiveData<String> = MutableLiveData()
    val outputLanguage: MutableLiveData<String> = MutableLiveData()
    val writingStyle: MutableLiveData<String> = MutableLiveData()
    val outputLength: MutableLiveData<Int> = MutableLiveData()

    init {
        viewModelScope.launch {
            setting.postValue(repository.getSetting(1))

            inputLanguage.postValue(setting.value?.inputLanguage ?: "Traditional Chinese" )
            outputLanguage.postValue(setting.value?.outputLanguage ?: "Traditional Chinese" )
            writingStyle.postValue(setting.value?.writingStyle ?: "Regular" )
            outputLength.postValue(setting.value?.outputLength ?: 50 )
        }
    }

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
