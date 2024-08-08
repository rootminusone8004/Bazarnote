package com.rootminusone8004.bazarnote;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionActivity extends AppCompatActivity {
    public static final int ADD_SESSION_REQUEST = 4;
    public static final int NOTE_TRANSFER_REQUEST = 5;
    public static final int STORAGE_PERMISSION_CODE = 1;

    private SessionViewModel sessionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_session);

        checkAndRequestPermissions();

        FloatingActionButton buttonAddSession = findViewById(R.id.button_add_session);
        buttonAddSession.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, AddSessionActivity.class);
            startActivityForResult(intent, ADD_SESSION_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.session_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        SessionAdapter adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);

        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        sessionViewModel.getAllSessions().observe(this, sessions -> adapter.submitList(sessions));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                sessionViewModel.delete(adapter.getSessionAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(session -> {
            Intent intent = new Intent(SessionActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_SESSION_ID, session.getSessionId());
            intent.putExtra(MainActivity.EXTRA_SESSION_NAME, session.getName());
            startActivityForResult(intent, NOTE_TRANSFER_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_SESSION_REQUEST && resultCode == RESULT_OK) {
            String sessionName = data.getStringExtra(AddSessionActivity.EXTRA_SESSION);
            Session session = new Session(sessionName);
            session.setPrice(0.0f);
            sessionViewModel.insert(session, SessionActivity.this);
        } else if (requestCode == NOTE_TRANSFER_REQUEST && resultCode == RESULT_OK) {
            float sum = data.getFloatExtra(MainActivity.EXTRA_SESSION_SUM, 0.0f);
            int id = data.getIntExtra(MainActivity.EXTRA_SESSION_ID, -1);
            String name = data.getStringExtra(MainActivity.EXTRA_SESSION_NAME);
            Session session = new Session(name);
            session.setPrice(sum);
            session.setSessionId(id);
            sessionViewModel.update(session);
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied. Closing the app.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_sessions) {
            sessionViewModel.deleteAllSessions();    // it will delete all sessions
            return true;
        } else if (itemId == R.id.show_summation) {
            sessionViewModel.getAllSessions().observe(this, sessions -> {
                if (sessions.isEmpty()) {
                    Toast.makeText(SessionActivity.this, "No sessions here", Toast.LENGTH_SHORT).show();
                } else {
                    float sum = 0;
                    for (Session session : sessions) {
                        sum += session.getPrice();
                    }
                    Toast.makeText(SessionActivity.this, String.valueOf(sum), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (itemId == R.id.session_save_csv_file) {
            writeDataToCSV();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showPermissionExplanationDialog();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionExplanationDialog();
            }
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This app needs storage permissions to create and save CSV files. Please grant the required permissions.")
                .setPositiveButton("OK", (dialog, which) -> requestStoragePermission())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }

    private void writeDataToCSV() {
        sessionViewModel.getAllSessions().observe(SessionActivity.this, sessions -> {
            if (sessions.isEmpty()) {
                Toast.makeText(SessionActivity.this, "No sessions are here", Toast.LENGTH_SHORT).show();
            } else {
                File mainDirectory = new File(Environment.getExternalStorageDirectory(), "Bazarnote");
                String timeStamp = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                File subDirectory = new File(mainDirectory, timeStamp);

                if (!subDirectory.exists()) {
                    subDirectory.mkdirs();
                }

                String fileName = "Summary.csv";
                File csvFile = new File(subDirectory, fileName);

                List<String[]> data = new ArrayList<>();
                int sum = 0;
                data.add(new String[]{"Session", "Price"});
                for (Session session : sessions) {
                    data.add(new String[]{session.getName(), String.valueOf(session.getPrice())});
                    sum += session.getPrice();
                }
                data.add(new String[]{"Total Price", String.valueOf(sum)});

                try {
                    FileWriter writer = new FileWriter(csvFile);
                    CSVWriter csvWriter = new CSVWriter(writer);

                    for (String[] row : data) {
                        csvWriter.writeNext(row);
                    }

                    csvWriter.close();
                    Toast.makeText(SessionActivity.this, "CSV file created successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SessionActivity.this, "Error creating CSV file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}