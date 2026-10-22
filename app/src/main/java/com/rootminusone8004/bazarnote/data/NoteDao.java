package com.rootminusone8004.bazarnote.data;

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
    void update(Note note) throws SQLiteConstraintException;

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table WHERE sessionId = :sessionId")
    void deleteAllSelectedNotes(int sessionId);

    @Query("SELECT * FROM note_table WHERE sessionId = :sessionId ORDER BY multiple DESC")
    LiveData<List<Note>> getAllSelectedNotes(int sessionId);
}
