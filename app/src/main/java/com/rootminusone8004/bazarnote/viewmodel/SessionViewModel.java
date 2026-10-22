package com.rootminusone8004.bazarnote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rootminusone8004.bazarnote.data.Session;
import com.rootminusone8004.bazarnote.data.SessionRepository;

import java.util.List;

public class SessionViewModel extends AndroidViewModel {
    private final SessionRepository repository;
    private final LiveData<List<Session>> allSessions;

    public SessionViewModel(@NonNull Application application) {
        super(application);
        this.repository = new SessionRepository(application);
        this.allSessions = repository.getAllSessions();
    }

    public LiveData<List<Session>> getAllSessions() {
        return allSessions;
    }

    public void insert(Session session, @Nullable Runnable onDuplicate) {
        repository.insert(session, onDuplicate);
    }

    public void update(Session session) {
        repository.update(session);
    }

    public void delete(Session session) {
        repository.delete(session);
    }

    public void deleteAllSessions() {
        repository.deleteAllSessions();
    }
}
