package com.rootminusone8004.bazarnote.data;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

/** Data access for {@link Note}s. All writes run off the main thread. */
public class NoteRepository {
    private final NoteDao noteDao;

    public NoteRepository(Application application) {
        this.noteDao = NoteDatabase.getInstance(application).noteDao();
    }

    public LiveData<List<Note>> getAllSelectedNotes(int sessionId) {
        return noteDao.getAllSelectedNotes(sessionId);
    }

    /** @param onDuplicate called on the main thread when a note with the same name already exists */
    public void insert(Note note, @Nullable Runnable onDuplicate) {
        DbWriter.execute(() -> noteDao.insert(note), onDuplicate);
    }

    /** @param onDuplicate called on the main thread when another note already uses the new name */
    public void update(Note note, @Nullable Runnable onDuplicate) {
        DbWriter.execute(() -> noteDao.update(note), onDuplicate);
    }

    public void delete(Note note) {
        DbWriter.execute(() -> noteDao.delete(note), null);
    }

    public void deleteAllSelectedNotes(int sessionId) {
        DbWriter.execute(() -> noteDao.deleteAllSelectedNotes(sessionId), null);
    }
}
