package com.rootminusone8004.bazarnote.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rootminusone8004.bazarnote.R;
import com.rootminusone8004.bazarnote.data.Note;
import com.rootminusone8004.bazarnote.data.Session;
import com.rootminusone8004.bazarnote.databinding.ActivityMainBinding;
import com.rootminusone8004.bazarnote.guide.TapMainActivity;
import com.rootminusone8004.bazarnote.ui.adapter.NoteAdapter;
import com.rootminusone8004.bazarnote.util.AppExecutors;
import com.rootminusone8004.bazarnote.util.CsvImporter;
import com.rootminusone8004.bazarnote.util.FormatUtils;
import com.rootminusone8004.bazarnote.util.SessionJson;
import com.rootminusone8004.bazarnote.viewmodel.NoteViewModel;

import java.io.InputStream;
import java.util.List;

/** Shows and edits the notes of one session. Going "up" returns the session's updated total and snapshot. */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_SESSION_ID = "com.rootminusone8004.bazarnote.EXTRA_SESSION_ID";
    public static final String EXTRA_SESSION_NAME = "com.rootminusone8004.bazarnote.EXTRA_SESSION_NAME";
    public static final String EXTRA_SESSION_SUM = "com.rootminusone8004.bazarnote.EXTRA_SESSION_SUM";
    public static final String EXTRA_SESSION_JSON = "com.rootminusone8004.bazarnote.EXTRA_SESSION_JSON";

    private static final String TAG = "MainActivity";
    private static final String[] CSV_MIME_TYPES = {
            "text/csv",
            "text/comma-separated-values",
            "text/tab-separated-values",
            "text/plain",
            "application/csv",
            "application/vnd.ms-excel",
            "application/octet-stream"
    };
    private static final int NO_SESSION = -1;

    private ActivityMainBinding binding;
    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private int sessionId;
    private String sessionName;
    /** Latest notes of the session; {@code null} until the first load has finished. */
    @Nullable
    private List<Note> notes;

    private final ActivityResultLauncher<Intent> addNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onNoteAdded);
    private final ActivityResultLauncher<Intent> updateNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onNoteUpdated);
    private final ActivityResultLauncher<Intent> pickCsvLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    importNotes(result.getData().getData());
                }
            });

    public static Intent newIntent(Context context, Session session) {
        return new Intent(context, MainActivity.class)
                .putExtra(EXTRA_SESSION_ID, session.getSessionId())
                .putExtra(EXTRA_SESSION_NAME, session.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionId = getIntent().getIntExtra(EXTRA_SESSION_ID, NO_SESSION);
        sessionName = getIntent().getStringExtra(EXTRA_SESSION_NAME);
        setTitle(sessionName);

        new TapMainActivity(this).startGuide();

        setUpNoteList();
        binding.buttonAddNote.setOnClickListener(v ->
                addNoteLauncher.launch(AddEditNoteActivity.newAddIntent(this)));

        // The toolbar arrow saves the session; the system back button is intentionally disabled.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                toast(R.string.toast_no_backpress);
            }
        });
    }

    private void setUpNoteList() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(note ->
                updateNoteLauncher.launch(AddEditNoteActivity.newEditIntent(this, note)));
        adapter.setOnItemAddPriceListener(note ->
                updateNoteLauncher.launch(AddEditNoteActivity.newAddPriceIntent(this, note)));
        adapter.setOnItemToggleListener((note, isIncluded) -> {
            note.setIncluded(isIncluded);
            noteViewModel.update(note, null);
        });

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllSelectedNotes(sessionId).observe(this, loaded -> {
            notes = loaded;
            adapter.submitList(loaded);
        });

        SwipeToDelete.attach(binding.recyclerView, position -> noteViewModel.delete(adapter.getNoteAt(position)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_notes) {
            noteViewModel.deleteAllSelectedNotes(sessionId);
        } else if (itemId == R.id.show_summation) {
            showTotal();
        } else if (itemId == R.id.import_notes) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, CSV_MIME_TYPES);
            pickCsvLauncher.launch(Intent.createChooser(intent, getString(R.string.menu_import_csv)));
        } else if (itemId == android.R.id.home) {
            finishWithSessionSummary();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showTotal() {
        if (notes == null || notes.isEmpty()) {
            toast(R.string.toast_no_notes);
        } else {
            Toast.makeText(this, FormatUtils.formatNumber(totalOf(notes)), Toast.LENGTH_SHORT).show();
        }
    }

    private void finishWithSessionSummary() {
        if (notes == null) {
            // Notes have not loaded yet; leave the stored session untouched rather than overwriting it.
            finish();
            return;
        }
        setResult(RESULT_OK, new Intent()
                .putExtra(EXTRA_SESSION_SUM, totalOf(notes))
                .putExtra(EXTRA_SESSION_ID, sessionId)
                .putExtra(EXTRA_SESSION_NAME, sessionName)
                .putExtra(EXTRA_SESSION_JSON, SessionJson.fromNotes(notes)));
        finish();
    }

    private void onNoteAdded(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        insertNote(AddEditNoteActivity.noteFromResult(data));
    }

    private void onNoteUpdated(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        if (!data.hasExtra(AddEditNoteActivity.EXTRA_ID)) {
            toast(R.string.toast_note_cant_update);
            return;
        }
        Note note = AddEditNoteActivity.noteFromResult(data);
        note.setSessionId(sessionId);
        noteViewModel.update(note, this::showDuplicateNoteMessage);
    }

    private void insertNote(Note note) {
        note.setSessionId(sessionId);
        noteViewModel.insert(note, this::showDuplicateNoteMessage);
    }

    /** Reads the picked CSV off the main thread and adds its rows as notes of this session. */
    private void importNotes(@Nullable Uri uri) {
        if (uri == null) {
            return;
        }
        AppExecutors.diskIO().execute(() -> {
            try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                for (Note note : CsvImporter.parseNotes(inputStream)) {
                    insertNote(note);
                }
            } catch (Exception e) {
                Log.e(TAG, "Could not import notes from " + uri, e);
                AppExecutors.mainThread().execute(() -> toast(R.string.toast_something_wrong));
            }
        });
    }

    private void showDuplicateNoteMessage() {
        toast(R.string.toast_note_dumplicate);
    }

    private void toast(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
    }

    private static double totalOf(List<Note> notes) {
        double sum = 0;
        for (Note note : notes) {
            if (note.isIncluded()) {
                sum += note.getMultiple();
            }
        }
        return sum;
    }
}
