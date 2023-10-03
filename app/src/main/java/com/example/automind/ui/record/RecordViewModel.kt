package com.example.automind.ui.record

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.automind.data.AppDatabase
import com.example.automind.data.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val database by lazy { AppDatabase.getDatabase(application) }
    private val repository by lazy { Repository(database.noteDao(),database.settingsDao()) }

    val latestSavedTextId: MutableLiveData<Long?> = MutableLiveData(null)

    // Added LiveData objects
    val originalText: MutableLiveData<String> = MutableLiveData()
    val summaryText: MutableLiveData<String> = MutableLiveData()
    val listText: MutableLiveData<String> = MutableLiveData()
    //val markdownContent: MutableLiveData<String> = MutableLiveData()
    private val _markdownContent = MutableLiveData<String>()
    val markdownContent: LiveData<String> get() = _markdownContent

    var hasOriginal = false
    var hasSummary = false
    var hasList = false
    var hasMarkdown = false

    var isLike = false

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
    ):Job {
        Log.d("saveNoteData before coroutine", "")
        return viewModelScope.launch {
            val id = repository.insertNote(
                tag,
                title,
                isLike,
                text,
                summary,
                list,
                markdownContent,
            )
            Log.d("saveNoteData", "$id, $tag, $title")
            latestSavedTextId.value = id
            Log.d("latestSavedTextId after STT", latestSavedTextId.value.toString())
            val notes = repository.getAllNotes()
            Log.d("DatabaseTest after STT", "Getting all data from database...")
            for (note in notes) {
                Log.d("DatabaseTest after STT", "Transcribed Text in database: ${note.content}")
            }
        }
    }

    fun updateTitleForId(id: Long, title: String): Job {
        return viewModelScope.launch {
            repository.updateTitleForId(id, title)
        }
    }

    fun updateTagForId(id: Long, tag: String): Job {
        return viewModelScope.launch {
            repository.updateTagForId(id, tag)
        }
    }

    fun updateIsLike(noteId: Long, isLike: Boolean): Job {
        return viewModelScope.launch {
            repository.updateIsLikeForId(noteId, isLike)
        }
    }

    suspend fun deleteNoteById(id: Long): Job {
        return viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }

    fun updateMarkdownContent(newContent: String) {
        _markdownContent.postValue(newContent)
    }

    fun updateNoteContent(id: Long, content: String, summary: String, list: String, mindmapMarkdown: String?) {
        viewModelScope.launch {
            repository.updateNoteContent(id, content, summary, list, mindmapMarkdown)
            Log.d("RecordViewModel", "NoteContent updated for Id: $id, New content: $content, New summary: $summary, New list: $list, New mindmapMarkdown: $mindmapMarkdown")
        }
    }



    fun generateMindmapPrompt(
        question: String,
        inputLanguage: String,
        outputLanguage: String,
        writingStyle: String
    ): String {
        val promptTemplate = """
I want to make a mindmap through markmap with transforming the markdown format text by using $writingStyle style.
Please summarize the $inputLanguage input text and give back the  $outputLanguage markdown format of the keywords.

Example 1: 
INPUT: 
明天我總共要做三件事情分別為運動吃飯和學習，學習的科目有英文數學和中文，吃飯的部分早上要吃香蕉午餐吃便當晚餐吃火鍋，運動的話早上要游泳下午打籃球晚上跑步，英文科目又分為現在式過去式和未來式。
OUTPUT:
### 明天的計畫
1. **運動**
   - 早上：游泳
   - 下午：打籃球
   - 晚上：跑步           
2. **吃飯**
   - 早餐：香蕉
   - 午餐：便當
   - 晚餐：火鍋       
3. **學習**
   - 學科：
     - 英文
       - 現在式
       - 過去式
       - 未來式
     - 數學
     - 中文
     
Example 2:
INPUT:
明天我總共要做三件事考試運動和吃飯，考試的科目有英文中文和數學，數學分成三角函數和排列組合，運動的話早上跑步，下午打籃球晚上游泳，吃飯的部分早上吃三明治午餐吃水餃晚上吃火鍋。
OUTPUT:
### 明天的計畫
1. **考試**
   - 考試科目：
     - 英文
     - 中文
     - 數學
       - 三角函數
       - 排列組合
2. **運動**
   - 早上：跑步
   - 下午：打籃球
   - 晚上：游泳
3. **吃飯**
   - 早餐：三明治
   - 午餐：水餃
   - 晚餐：火鍋
   
INPUT:
$question
OUTPUT:
"""
        return promptTemplate
    }



    fun generateSummaryPrompt(
        question: String,
        inputLanguage: String,
        outputLanguage: String,
        outputLength: String,
        writingStyle: String
    ): String {
        val promptTemplate = """
Create a concise $outputLanguage summary with $writingStyle style in $outputLength words for the following $inputLanguage text: $question
"""
        return promptTemplate
    }


    fun generateListPrompt(
        question: String,
        inputLanguage: String,
        outputLanguage: String,
        writingStyle: String
    ): String {
        val promptTemplate = """
Summarize the key words of the following $inputLanguage text in $outputLanguage and using bullet points list with $writingStyle style: $question
"""
        return promptTemplate
    }
}


