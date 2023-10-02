package com.example.automind.data

import androidx.lifecycle.LiveData

class Repository(
    private val noteDao: NoteDao,
    private val settingsDao: SettingsDao
    ) {
    suspend fun insertNote(
        tag: String,
        title: String,
        isLike: Boolean,
        content: String,
        summary: String,
        list: String,
        mindmapMarkdown: String,
    ): Long {
        val note = Note(
            tag = tag,
            title = title,
            isLike = isLike,
            content = content,
            summary = summary,
            list = list,
            mindmapMarkdown = mindmapMarkdown,
        )
        return noteDao.insertNote(note)
    }

    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAllNotes()
    }

    suspend fun getNoteById(noteId: Long): Note?{
        return noteDao.getNoteById(noteId)
    }

    suspend fun getNoteByTag(noteTag: String): List<Note>?{
        return noteDao.getNoteByTag(noteTag)
    }

    suspend fun getNoteByIsLike(): List<Note>?{
        return noteDao.getNoteByIsLike()
    }


    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    suspend fun deleteNoteById(id: Long) {
        noteDao.deleteNoteById(id)
    }


    suspend fun updateMindmapMarkdownForId(id: Long, markdown: String) {
        noteDao.updateMindmapMarkdownForId(id, markdown)
    }

    suspend fun updateTitleForId(id:Long, title:String){
        noteDao.updateTitleForId(id,title)
    }

    suspend fun updateTagForId(id:Long, tag:String){
        noteDao.updateTagForId(id,tag)
    }

    suspend fun updateIsLikeForId(id: Long, isLike: Boolean) {
        noteDao.updateIsLikeForId(id, isLike)
    }

    suspend fun updateNoteContent(id: Long, content: String, summary: String, list: String, mindmapMarkdown: String?) {
        noteDao.updateNoteContent(id, content, summary, list, mindmapMarkdown)
    }


    //settings
    suspend fun insertSetting(setting: Setting) {
        settingsDao.insert(setting)
    }

    fun getSetting(id: Int): LiveData<Setting> {
        return settingsDao.getSetting(id)
    }

    suspend fun updateSetting(setting: Setting) {
        settingsDao.update(setting)
    }
}