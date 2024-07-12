package com.rootminusone8004.bazarnote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static final int ADD_PRICE_REQUEST = 3;

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(@Nullable List<Note> notes) {
                adapter.setNotes(notes);
            }
        });

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

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getItem());
                intent.putExtra(AddEditNoteActivity.EXTRA_QUANTITY, note.getQuantity());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRICE, note.getPrice());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });

        adapter.setOnItemCheckListener(new NoteAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getItem());
                intent.putExtra(AddEditNoteActivity.EXTRA_QUANTITY, note.getQuantity());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRICE_CHECK, 5);
                startActivityForResult(intent, ADD_PRICE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            int price = data.getIntExtra(AddEditNoteActivity.EXTRA_PRICE, 0);

            Note note = new Note(item, quantity, price);
            noteViewModel.insert(note);

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            int price = data.getIntExtra(AddEditNoteActivity.EXTRA_PRICE, 0);

            Note note = new Note(item, quantity, price);
            note.setId(id);
            noteViewModel.update(note);
        } else if (requestCode == ADD_PRICE_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String item = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            float quantity = data.getFloatExtra(AddEditNoteActivity.EXTRA_QUANTITY, 0);
            int price = data.getIntExtra(AddEditNoteActivity.EXTRA_PRICE, 0);
            Note note = new Note(item, quantity, price);
            note.setId(id);
            noteViewModel.update(note);
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_notes) {
            noteViewModel.deleteAllNotes();
            Toast.makeText(MainActivity.this, "All notes have been deleted", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.show_summation) {
            noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
                @Override
                public void onChanged(List<Note> notes) {
                    float sum = 0;
                    for (Note note : notes) {
                        sum += note.getMultiple();
                    }
                    Toast.makeText(MainActivity.this, String.valueOf(sum), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}