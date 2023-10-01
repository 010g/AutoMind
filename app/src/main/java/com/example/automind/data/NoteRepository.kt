package com.example.automind.data

class NoteRepository(private val noteDao: NoteDao) {
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

}