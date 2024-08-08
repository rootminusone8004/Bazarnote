package com.rootminusone8004.bazarnote;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends androidx.lifecycle.AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allSelectedNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NoteRepository(application);
    }

    public void insert(Note note, Context context) {
        repository.insert(note, context);
    }

    public void update(Note note, Context context){
        repository.update(note, context);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public void deleteAllSelectedNotes(int sessionId){
        repository.deleteAllSelectedNotes(sessionId);
    }

    public LiveData<List<Note>> getAllSelectedNotes(int sessionId){
        this.allSelectedNotes = repository.getAllSelectedNotes(sessionId);
        return allSelectedNotes;
    }
}
