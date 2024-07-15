package com.rootminusone8004.bazarnote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SessionViewModel extends AndroidViewModel {
private NoteRepository repository;
private LiveData<List<Session>> allSessions;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allSessions = repository.getAllSession();
    }

    public void insert(Session session){
        repository.insert(session);
    }

    public void update(Session session){
        repository.update(session);
    }

    public void delete(Session session){
        repository.delete(session);
    }

    public void deleteAllSessions(){
        repository.deleteAllSession();
    }

    public LiveData<List<Session>> getAllSessions(){
        return allSessions;
    }
}
