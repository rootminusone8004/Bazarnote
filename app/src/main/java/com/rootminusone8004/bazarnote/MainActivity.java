package com.rootminusone8004.bazarnote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.rootminusone8004.bazarnote.Utilities.TapMainActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 6;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static final int ADD_PRICE_REQUEST = 3;
    private static final int PICK_CSV_FILE = 7;

    public static final String EXTRA_SESSION_ID = "com.rootminusone8004.bazarnote.EXTRA_SESSION_ID";
    public static final String EXTRA_SESSION_NAME = "com.rootminusone8004.bazarnote.EXTRA_SESSION_NAME";
    public static final String EXTRA_SESSION_SUM = "com.rootminusone8004.bazarnote.EXTRA_SESSION_SUM";
    public static final String EXTRA_SESSION_JSON = "com.rootminusone8004.bazarnote.EXTRA_SESSION_JSON";

    private NoteViewModel noteViewModel;
    private Intent sessionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);
        Intent sessionIntent = getIntent();

        TapMainActivity tapMainActivity = new TapMainActivity(this);
        tapMainActivity.startGuide();

        String title = sessionIntent.getStringExtra(EXTRA_SESSION_NAME);
        setTitle(title);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        noteViewModel.getAllSelectedNotes(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1)).observe(this, notes -> adapter.submitList(notes));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getItem());
            intent.putExtra(AddEditNoteActivity.EXTRA_QUANTITY, note.getQuantity());
            intent.putExtra(AddEditNoteActivity.EXTRA_PRICE, note.getPrice());
            startActivityForResult(intent, EDIT_NOTE_REQUEST);
        });

        adapter.setOnItemAddPriceListener(note -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
            intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getItem());
            intent.putExtra(AddEditNoteActivity.EXTRA_QUANTITY, note.getQuantity());
            intent.putExtra(AddEditNoteActivity.EXTRA_PRICE_CHECK, 5);
            startActivityForResult(intent, ADD_PRICE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sessionIntent = getIntent();

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            float price = data.getFloatExtra(AddEditNoteActivity.EXTRA_PRICE, 0);

            Note note = new Note(item, quantity, price);
            note.setSessionId(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1));
            noteViewModel.insert(note, MainActivity.this);    // this will save notes
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, R.string.toast_note_cant_update, Toast.LENGTH_SHORT).show();
                return;
            }

            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            float price = data.getFloatExtra(AddEditNoteActivity.EXTRA_PRICE, 0);

            Note note = new Note(item, quantity, price);
            note.setId(id);
            note.setSessionId(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1));
            noteViewModel.update(note, MainActivity.this);
        } else if (requestCode == ADD_PRICE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, R.string.toast_note_cant_update, Toast.LENGTH_SHORT).show();
                return;
            }

            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            float price = data.getFloatExtra(AddEditNoteActivity.EXTRA_PRICE, 0);
            Note note = new Note(item, quantity, price);
            note.setId(id);
            note.setSessionId(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1));
            noteViewModel.update(note, MainActivity.this);
        } else if(requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri fileUri = data.getData();
                List<Note> notes = extractInfoFromCSV(fileUri);
                saveNotesToDatabase(notes);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this, R.string.toast_no_backpress, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent sessionIntent = getIntent();
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_notes) {
            int sessionId = sessionIntent.getIntExtra(EXTRA_SESSION_ID, -1);
            noteViewModel.deleteAllSelectedNotes(sessionId);    // delete all the notes
            return true;
        } else if (itemId == R.id.show_summation) {
            noteViewModel.getAllSelectedNotes(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1)).observe(this, notes -> {
                if (notes.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_notes, Toast.LENGTH_SHORT).show();
                } else {
                    double sum = 0;
                    for (Note note : notes) {
                        sum += note.getMultiple();
                    }
                    Toast.makeText(MainActivity.this, Utility.formatDoubleValue(sum), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (itemId == R.id.import_notes) {
            pickCSVFile();
            return true;
        } else if (itemId == android.R.id.home) {
            int id = sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1);
            String name = sessionIntent.getStringExtra(EXTRA_SESSION_NAME);
            noteViewModel.getAllSelectedNotes(id).observe(this, notes -> {

                double sum = 0;
                JsonArray jsonArray = new JsonArray();
                for (Note note : notes) {
                    sum += note.getMultiple();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("Item", note.getItem());
                    jsonObject.addProperty("Quantity", note.getQuantity());
                    jsonObject.addProperty("Price", note.getPrice());
                    jsonArray.add(jsonObject);
                }

                Gson gson = new Gson();
                String jsonString = gson.toJson(jsonArray);

                Intent passIntent = new Intent();
                passIntent.putExtra(EXTRA_SESSION_SUM, sum);
                passIntent.putExtra(EXTRA_SESSION_ID, id);
                passIntent.putExtra(EXTRA_SESSION_NAME, name);
                passIntent.putExtra(EXTRA_SESSION_JSON, jsonString);
                setResult(RESULT_OK, passIntent);
                finish();
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void pickCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV"), PICK_CSV_FILE);
    }

    private void readCSV(Uri uri) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Note> extractInfoFromCSV(Uri fileUri) {
        List<Note> notes = new ArrayList<>();

        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            CSVReader reader = new CSVReader(inputStreamReader);

            String[] nextLine;
            boolean dataStarted = false;

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 2) {
                    if (!dataStarted) {
                        if (nextLine[0].equalsIgnoreCase("Item") && nextLine[1].equalsIgnoreCase("Quantity")) {
                            dataStarted = true; // Skip Header
                        }
                        continue;
                    }

                    String item = nextLine[0];
                    float quantity;

                    try {
                        quantity = Float.parseFloat(nextLine[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    notes.add(new Note(item, quantity, 0));
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notes;
    }

    private void saveNotesToDatabase(List<Note> notes) {
        for (Note note : notes) {
            note.setSessionId(sessionIntent.getIntExtra(EXTRA_SESSION_ID, 1));
            noteViewModel.insert(note, this);
        }
    }
}