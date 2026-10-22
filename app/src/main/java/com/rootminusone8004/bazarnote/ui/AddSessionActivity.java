package com.rootminusone8004.bazarnote.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.rootminusone8004.bazarnote.R;
import com.rootminusone8004.bazarnote.databinding.ActivityAddSessionBinding;
import com.rootminusone8004.bazarnote.guide.TapAddSessionActivity;

public class AddSessionActivity extends AppCompatActivity {
    public static final String EXTRA_SESSION = "com.rootminusone8004.bazarnote.EXTRA_SESSION";

    private ActivityAddSessionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }
        setTitle(R.string.header_add_session);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note_menu, menu);

        // Wait for the toolbar to lay out so the save action can be highlighted.
        new Handler(Looper.getMainLooper()).post(() -> {
            if (findViewById(R.id.save_note) != null) {
                new TapAddSessionActivity(this).startGuide();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            saveSession();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSession() {
        String session = binding.editSessionItem.getText().toString();

        if (session.trim().isEmpty()) {
            Toast.makeText(this, R.string.toast_field_fill_indicate, Toast.LENGTH_SHORT).show();
            return;
        }

        setResult(RESULT_OK, new Intent().putExtra(EXTRA_SESSION, session));
        finish();
    }
}
