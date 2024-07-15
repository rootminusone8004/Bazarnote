package com.rootminusone8004.bazarnote;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table WHERE sessionId = :sessionId ORDER BY price DESC")
    LiveData<List<Note>> getAllSelectedNotes(int sessionId);

    @Insert
    void insert(Session session);

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Query("DELETE FROM session_table")
    void deleteAllSessions();

    @Query("SELECT * FROM session_table")
    LiveData<List<Session>> getAllSessions();
}
