package com.rootminusone8004.bazarnote.data;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

/** Data access for {@link Session}s. All writes run off the main thread. */
public class SessionRepository {
    private final SessionDao sessionDao;
    private final LiveData<List<Session>> allSessions;

    public SessionRepository(Application application) {
        this.sessionDao = NoteDatabase.getInstance(application).sessionDao();
        this.allSessions = sessionDao.getAllSessions();
    }

    public LiveData<List<Session>> getAllSessions() {
        return allSessions;
    }

    /** @param onDuplicate called on the main thread when a session with the same name already exists */
    public void insert(Session session, @Nullable Runnable onDuplicate) {
        DbWriter.execute(() -> sessionDao.insert(session), onDuplicate);
    }

    public void update(Session session) {
        DbWriter.execute(() -> sessionDao.update(session), null);
    }

    public void delete(Session session) {
        DbWriter.execute(() -> sessionDao.delete(session), null);
    }

    public void deleteAllSessions() {
        DbWriter.execute(sessionDao::deleteAllSessions, null);
    }
}
