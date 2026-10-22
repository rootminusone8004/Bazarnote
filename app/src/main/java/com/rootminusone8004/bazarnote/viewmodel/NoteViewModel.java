package com.rootminusone8004.bazarnote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rootminusone8004.bazarnote.data.Note;
import com.rootminusone8004.bazarnote.data.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository repository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NoteRepository(application);
    }

    public LiveData<List<Note>> getAllSelectedNotes(int sessionId) {
        return repository.getAllSelectedNotes(sessionId);
    }

    public void insert(Note note, @Nullable Runnable onDuplicate) {
        repository.insert(note, onDuplicate);
    }

    public void update(Note note, @Nullable Runnable onDuplicate) {
        repository.update(note, onDuplicate);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllSelectedNotes(int sessionId) {
        repository.deleteAllSelectedNotes(sessionId);
    }
}
