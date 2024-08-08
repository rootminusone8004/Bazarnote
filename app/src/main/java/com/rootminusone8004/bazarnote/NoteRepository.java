package com.rootminusone8004.bazarnote;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

    public void insert(Note note, Context context){
        new InsertNoteAsyncTask(noteDao, context).execute(note);
    }

    public void update(Note note, Context context){
        new UpdateNoteAsyncTask(noteDao, context).execute(note);
    }

    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public void deleteAllSelectedNotes(int sessionId){
        new DeleteAllSelectedNotesAsyncTask(noteDao).execute(sessionId);
    }

    public LiveData<List<Note>> getAllSelectedNotes(int sessionId){
        this.allSelectedNotes = noteDao.getAllSelectedNotes(sessionId);
        return allSelectedNotes;
    }

    public static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Boolean>{
        private NoteDao noteDao;
        private Context context;

        private InsertNoteAsyncTask(NoteDao noteDao, Context context){
            this.noteDao = noteDao;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Note... notes) {
            try {
                noteDao.insert(notes[0]);
                return true;
            } catch (SQLiteConstraintException e) {
                Log.e("DatabaseError", "Card with this name already exists.", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(context, "Duplicate notes are not allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Boolean>{
        private NoteDao noteDao;
        private Context context;

        private UpdateNoteAsyncTask(NoteDao noteDao, Context context){
            this.noteDao = noteDao;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Note... notes) {
            try {
                noteDao.update(notes[0]);
                return true;
            } catch (SQLiteConstraintException e) {
                Log.e("DatabaseError", "Card with this name already exists.", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(context, "Duplicate notes are not allowed", Toast.LENGTH_SHORT).show();
            }
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

    public static class DeleteAllSelectedNotesAsyncTask extends AsyncTask<Integer, Void, Void>{
        private NoteDao noteDao;

        private DeleteAllSelectedNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            noteDao.deleteAllSelectedNotes(integers[0]);
            return null;
        }
    }

    public void insert(Session session, Context context){
        new InsertSessionAsyncTask(noteDao, context).execute(session);
    }

    public void update(Session session){
        new UpdateSessionAsyncTask(noteDao).execute(session);
    }

    public void delete(Session session){
        new DeleteSessionAsyncTask(noteDao).execute(session);
    }

    public void deleteAllSession(){
        new DeleteAllSessionsAsyncTask(noteDao).execute();
    }

    public LiveData<List<Session>> getAllSession(){
        return allSessions;
    }

    public static class InsertSessionAsyncTask extends AsyncTask<Session, Void, Boolean>{
        private NoteDao noteDao;
        private Context context;

        private InsertSessionAsyncTask(NoteDao noteDao, Context context){
            this.noteDao = noteDao;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Session... sessions) {
            try {
                noteDao.insert(sessions[0]);
                return true;
            } catch (SQLiteConstraintException e) {
                Log.e("DatabaseError", "Card with this name already exists.", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!success) {
                Toast.makeText(context, "Duplicate sessions are not allowed", Toast.LENGTH_SHORT).show();
            }
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
