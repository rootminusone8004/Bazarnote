package com.rootminusone8004.bazarnote;

import android.database.sqlite.SQLiteConstraintException;

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
    void insert(Note note) throws SQLiteConstraintException;

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table WHERE sessionId = :sessionId")
    void deleteAllSelectedNotes(int sessionId);

    @Query("SELECT * FROM note_table WHERE sessionId = :sessionId ORDER BY multiple DESC")
    LiveData<List<Note>> getAllSelectedNotes(int sessionId);

    @Insert
    void insert(Session session) throws SQLiteConstraintException;

    @Update
    void update(Session session);

    @Delete
    void delete(Session session);

    @Query("DELETE FROM session_table")
    void deleteAllSessions();

    @Query("SELECT * FROM session_table")
    LiveData<List<Session>> getAllSessions();
}
