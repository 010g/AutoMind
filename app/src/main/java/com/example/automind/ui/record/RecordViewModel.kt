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
    val markdownContent: MutableLiveData<String> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val tag: MutableLiveData<String> = MutableLiveData()
    val isLike: MutableLiveData<Boolean> = MutableLiveData(false)

    var hasOriginal = false
    var hasSummary = false
    var hasList = false
    var hasMarkdown = false

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
        markdownContent.postValue(newContent)
    }

    fun updateNoteContent(id: Long, content: String, summary: String, list: String, mindmapMarkdown: String?) {
        viewModelScope.launch {
            repository.updateNoteContent(id, content, summary, list, mindmapMarkdown)
            Log.d("RecordViewModel", "NoteContent updated for Id: $id, New content: $content, New summary: $summary, New list: $list, New mindmapMarkdown: $mindmapMarkdown")
        }
    }



    fun generatePrompt(
        type: String,
        question: String,
        inputLanguage: String,
        outputLanguage: String,
        writingStyle: String,
        outputLength: String? = null
    ): String {
        return when (type) {
            "Mindmap" ->  """
I want to make a mindmap through markmap with transforming the markdown format text by using $writingStyle style.
Please summarize the $inputLanguage input text and give back the  $outputLanguage markdown format of the keywords.

Example 1 for input language being Traditional Chinese and output language being Traditional Chinese: 
INPUT: 
設計這款應用程式的動機是，希望使用者可以透過語音輸入，讓人工智慧自動分析整理成不同的筆記型態，包含逐字稿、總結、清單還有心智圖 四種格式。
OUTPUT:
### 設計這款應用程式的動機
1. **透過支援語音輸入**
2. **利用人工智慧**
3. **為使用者提供筆記整理**
    - 逐字稿
    - 摘要
    - 清單
    - 心智圖

     
Example 2 for input language being English and output language being English:
INPUT:
Tomorrow, I have a total of three things to do: exams, sports, and eating. The exam subjects include English, Chinese, and Mathematics. Mathematics is divided into trigonometric functions and permutations and combinations. For sports, I will go for a run in the morning, play basketball in the afternoon, and swim in the evening. As for meals, I will have a sandwich for breakfast, dumplings for lunch, and hot pot for dinner.
OUTPUT:
### Tomorrow's Plan
1. **Exams**
   - English
   - Chinese
   - Mathematics
     - Trigonometric functions
     - Permutations and combinations
2. **Sports**
   - Go for a run in the morning
   - Play basketball in the afternoon
   - Swim in the evening
3. **Eating**
   - A sandwich for breakfast
   - Dumplings for lunch
   - Hot pot for dinner
   
Example 3 for input language being Traditional Chinese and output language being English: 
INPUT: 
明天，我總共有三件事要做：考試、運動和用餐。考試科目包括英語、中文和數學。數學又分為三角函數和排列組合。至於運動，我早上會去跑步，下午打籃球，晚上游泳。至於用餐，早餐吃三明治，午餐吃餃子，晚餐吃火鍋。
OUTPUT:
### Tomorrow's Plan
1. **Exams**
   - English
   - Chinese
   - Mathematics
     - Trigonometric functions
     - Permutations and combinations
2. **Sports**
   - Go for a run in the morning
   - Play basketball in the afternoon
   - Swim in the evening
3. **Eating**
   - A sandwich for breakfast
   - Dumplings for lunch
   - Hot pot for dinner
   
Example 4 for input language being English and output language being Traditional Chinese:
INPUT: 
Tomorrow, I have a total of three things to do, which are exercise, eating, and studying. The subjects for studying are English, mathematics, and Chinese. As for meals, I will have bananas for breakfast, a bento for lunch, and hot pot for dinner. Regarding exercise, I will swim in the morning, play basketball in the afternoon, and go for a run in the evening. In English subjects, there are present tense, past tense, and future tense.
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
    
INPUT:
$question
OUTPUT:
"""
            "Summary" -> """
Create a concise $outputLanguage summary with $writingStyle style in $outputLength words for the following $inputLanguage text: $question.
"""
            "List" -> """
Summarize the key words of the following $inputLanguage text in $outputLanguage and using bullet points list with $writingStyle style:\n\n- $question.
"""
            else -> ""
        }
    }
}


