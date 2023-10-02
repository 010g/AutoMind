package com.example.automind.data

import android.nfc.Tag
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note): Long

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Long): Note?

    @Query("SELECT * FROM notes WHERE tag = :noteTag")
    suspend fun getNoteByTag(noteTag: String): List<Note>?

    @Query("SELECT * FROM notes WHERE isLike = 1")
    suspend fun getNoteByIsLike(): List<Note>?

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("UPDATE notes SET mindmapMarkdown = :markdown WHERE id = :id")
    suspend fun updateMindmapMarkdownForId(id: Long, markdown: String)

    @Query("UPDATE notes SET title = :title  WHERE id = :id")
    suspend fun updateTitleForId(id: Long, title: String)

    @Query("UPDATE notes SET tag = :tag  WHERE id = :id")
    suspend fun updateTagForId(id: Long, tag: String)

    @Query("UPDATE notes SET isLike = :isLike WHERE id = :id")
    suspend fun updateIsLikeForId(id: Long, isLike: Boolean)

    @Query("UPDATE notes SET content = :content, summary = :summary, list = :list, mindmapMarkdown = :mindmapMarkdown WHERE id = :id")
    suspend fun updateNoteContent(id: Long, content: String, summary: String, list: String, mindmapMarkdown: String?)

}