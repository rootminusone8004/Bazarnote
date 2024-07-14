package com.rootminusone8004.bazarnote;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allSelectedNotes;
    private LiveData<List<Session>> allSessions;

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        this.noteDao = database.noteDao();
        this.allSessions = noteDao.getAllSessions();
    }

    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllSelectedNotes(int sessionId){
        this.allSelectedNotes = noteDao.getAllSelectedNotes(sessionId);
        return allSelectedNotes;
    }

    public static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    public static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    public static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void>{
        private NoteDao noteDao;

        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    public static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;

        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }

    public void insert(Session session){
        new InsertSessionAsyncTask(noteDao).execute(session);
    }

    public void update(Session session){
        new UpdateSessionAsyncTask(noteDao).execute(session);
    }

    public void delete(Session session){
        new DeleteSessionAsyncTask(noteDao).execute(session);
    }

    public void deleteAllSession(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Session>> getAllSession(){
        return allSessions;
    }

    public static class InsertSessionAsyncTask extends AsyncTask<Session, Void, Void>{
        private NoteDao noteDao;

        private InsertSessionAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Session... sessions) {
            noteDao.insert(sessions[0]);
            return null;
        }
    }

    public static class UpdateSessionAsyncTask extends AsyncTask<Session, Void, Void>{
        private NoteDao noteDao;

        private UpdateSessionAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Session... sessions) {
            noteDao.update(sessions[0]);
            return null;
        }
    }

    public static class DeleteSessionAsyncTask extends AsyncTask<Session, Void, Void>{
        private NoteDao noteDao;

        private DeleteSessionAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Session... sessions) {
            noteDao.delete(sessions[0]);
            return null;
        }
    }

    public static class DeleteAllSessionsAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;

        private DeleteAllSessionsAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllSessions();
            return null;
        }
    }
}
