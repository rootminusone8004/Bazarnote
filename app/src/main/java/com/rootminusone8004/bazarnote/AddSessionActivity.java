package com.rootminusone8004.bazarnote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.rootminusone8004.bazarnote.Utilities.TapAddSessionActivity;

import java.util.Objects;

public class AddSessionActivity extends AppCompatActivity {
    public static final String EXTRA_SESSION = "com.rootminusone8004.bazarnote.EXTRA_SESSION";

    private EditText editTextSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_add_session);

        editTextSession = findViewById(R.id.edit_session_item);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(R.string.header_add_session);
    }

    private void saveSession() {
        String session = editTextSession.getText().toString();

        if (session.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_field_fill_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent sessionData = new Intent();
        sessionData.putExtra(EXTRA_SESSION, session);

        setResult(RESULT_OK, sessionData);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);

        new Handler().post(() -> {
            View saveNote = findViewById(R.id.save_note);
            if (saveNote != null) {
                TapAddSessionActivity tapAddSessionActivity = new TapAddSessionActivity(this);
                tapAddSessionActivity.startGuide();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_note) {
            saveSession();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}