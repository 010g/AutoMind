package com.example.automind.ui.settings

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
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
            var initSetting = repository.getSetting(1)
            if (initSetting == null){
                Log.d("initSetting", "is null")
                initSetting = Setting(
                    id = 1,
                    inputLanguage = "Traditional Chinese",
                    outputLanguage = "Traditional Chinese",
                    writingStyle = "Regular",
                    outputLength = 50
                )
                repository.insertSetting(initSetting)
            }
            setting.postValue(initSetting)
            Log.d("setting in init", setting.value.toString())

            inputLanguage.postValue(setting.value?.inputLanguage ?: "Traditional Chinese" )
            outputLanguage.postValue(setting.value?.outputLanguage ?: "Traditional Chinese" )
            writingStyle.postValue(setting.value?.writingStyle ?: "Regular" )
            outputLength.postValue(setting.value?.outputLength ?: 50 )
        }
    }

    fun updateInputLanguage(language: String) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        val newSetting = currentSetting.copy(inputLanguage = language)
        setting.postValue(newSetting)
        val updatedRowNum = repository.updateSetting(newSetting)
        Log.d("updatedRowNum in updateInputLanguage", updatedRowNum.toString())
        inputLanguage.postValue(language)
        Log.d("updateInputLanguage", inputLanguage.value.toString())
    }

    fun updateOutputLanguage(language: String) = viewModelScope.launch {
        val currentSetting = setting.value ?: return@launch
        val newSetting = currentSetting.copy(outputLanguage = language)
        setting.postValue(newSetting)
        val updatedRowNum = repository.updateSetting(newSetting)
        Log.d("updatedRowNum in updateOutputLanguage", updatedRowNum.toString())
        outputLanguage.postValue(language)
        Log.d("updateOutputLanguage", outputLanguage.value.toString())
    }

    fun setOutputLength(newLength: Int) = viewModelScope.launch {
        Log.d("SettingsViewModel", "Setting output length to: $newLength")
        val currentSetting = setting.value ?: return@launch
        val newSetting = currentSetting.copy(outputLength = newLength)
        setting.postValue(newSetting)
        val updatedRowNum = repository.updateSetting(newSetting)
        Log.d("updatedRowNum in setOutputLength", updatedRowNum.toString())
        outputLength.postValue(newLength)
        Log.d("setOutputLength", outputLength.value.toString())
    }

    fun setWritingStyle(newStyle: String) = viewModelScope.launch {
        Log.d("SettingsViewModel", "Setting writing style to: $newStyle")
        val currentSetting = setting.value ?: return@launch
        val newSetting = currentSetting.copy(writingStyle = newStyle)
        setting.postValue(newSetting)
        val updatedRowNum = repository.updateSetting(newSetting)
        Log.d("updatedRowNum in setWritingStyle", updatedRowNum.toString())
        writingStyle.postValue(newStyle)
        Log.d("setWritingStyle", writingStyle.value.toString())
    }
}
