package com.rootminusone8004.bazarnote;

import static com.rootminusone8004.bazarnote.Utility.formatDoubleValue;
import static com.rootminusone8004.bazarnote.Utility.formatFloatValue;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import com.rootminusone8004.bazarnote.Utilities.TapSessionActivity;

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

    private SessionViewModel sessionViewModel;
    private SessionAdapter adapter;
    private FloatingActionButton checkboxShowButton;
    private TextView guideMessage;
    private boolean areCheckboxesVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_session);

        TapSessionActivity tapSessionActivity = new TapSessionActivity(this);
        tapSessionActivity.startGuide();

        guideMessage = findViewById(R.id.guiding_message);
        checkboxShowButton = findViewById(R.id.card_checkbox_show_btn);

        FloatingActionButton buttonAddSession = findViewById(R.id.button_add_session);
        buttonAddSession.setOnClickListener(v -> {
            Intent intent = new Intent(SessionActivity.this, AddSessionActivity.class);
            startActivityForResult(intent, ADD_SESSION_REQUEST);
        });

        RecyclerView recyclerView = findViewById(R.id.session_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new SessionAdapter();
        recyclerView.setAdapter(adapter);

        checkboxShowButton.setOnClickListener(v -> {
            Permission permission = new Permission(this, this, this::showSaveAsDialog);
            try {
                permission.checkPermissionAndWriteToCSV(null);
            } catch (Exception e) {
                Toast.makeText(SessionActivity.this, R.string.toast_something_wrong, Toast.LENGTH_SHORT).show();
            }
            adapter.hideAllCheckboxesWithTick();
            checkboxShowButton.setVisibility(View.GONE);
            guideMessage.setVisibility(View.GONE);
            areCheckboxesVisible = false;
            invalidateOptionsMenu();
        });

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
            double sum = data.getDoubleExtra(MainActivity.EXTRA_SESSION_SUM, 0.0f);
            int id = data.getIntExtra(MainActivity.EXTRA_SESSION_ID, -1);
            String name = data.getStringExtra(MainActivity.EXTRA_SESSION_NAME);
            String jsonInfo = data.getStringExtra(MainActivity.EXTRA_SESSION_JSON);
            Session session = new Session(name);
            session.setPrice(sum);
            session.setSessionId(id);
            session.setJsonInfo(jsonInfo);
            sessionViewModel.update(session);
        } else if (requestCode == Permission.STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, R.string.toast_permission_granted, Toast.LENGTH_SHORT).show();
                    showSaveAsDialog(null);
                } else {
                    Toast.makeText(this, R.string.toast_permisson_denied, Toast.LENGTH_SHORT).show();
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
            sessionViewModel.deleteAllSessions();
            return true;
        } else if (itemId == R.id.show_summation) {
            sessionViewModel.getAllSessions().observe(this, sessions -> {
                if (sessions.isEmpty()) {
                    Toast.makeText(SessionActivity.this, R.string.toast_no_sessions, Toast.LENGTH_SHORT).show();
                } else {
                    double sum = 0;
                    for (Session session : sessions) {
                        sum += session.getPrice();
                    }
                    Toast.makeText(SessionActivity.this, Utility.formatDoubleValue(sum), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        } else if (itemId == R.id.session_save_csv_file) {
            adapter.showAllCheckboxesWithTick();
            checkboxShowButton.setVisibility(View.VISIBLE);
            guideMessage.setVisibility(View.VISIBLE);
            areCheckboxesVisible = true;
            invalidateOptionsMenu();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setVisible(!areCheckboxesVisible);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void writeDataToCSV(String fileName) {
        sessionViewModel.getAllSessions().observe(SessionActivity.this, sessions -> {
            if (sessions.isEmpty()) {
                Toast.makeText(SessionActivity.this, R.string.toast_no_sessions, Toast.LENGTH_SHORT).show();
            } else {
                File mainDirectory = new File(Environment.getExternalStorageDirectory(), "Bazarnote");
                String timeStamp = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                File subDirectory = new File(mainDirectory, timeStamp);

                if (!subDirectory.exists()) {
                    subDirectory.mkdirs();
                }

                File csvFile = new File(subDirectory, fileName);
                csvFile = getUniqueFile(csvFile);

                List<String[]> data = new ArrayList<>();

                JsonObject jsonObject = new JsonObject();
                for (Session session : sessions) {
                    int sessionPosition = sessions.indexOf(session);
                    if (adapter.isCheckboxChecked(sessionPosition)) {
                        jsonObject.add(
                                session.getName(),
                                JsonParser.parseString(session.getJsonInfo()).getAsJsonArray()
                        );
                    }
                }

                double totalSum = 0.0;
                for (String category : jsonObject.keySet()) {
                    data.add(new String[]{"#####", category, "#####"});
                    data.add(new String[]{"Item", "Quantity", "Unit Price", "Real Price"});

                    JsonArray items = jsonObject.getAsJsonArray(category);
                    double totalPrice = 0.0;

                    for (int i = 0; i < items.size(); i++) {
                        JsonObject item = items.get(i).getAsJsonObject();
                        String itemName = item.get("Item").getAsString();
                        float quantity = item.get("Quantity").getAsFloat();
                        float price = item.get("Price").getAsFloat();
                        Double multiply = quantity * price * 1.0;
                        data.add(new String[]{
                                itemName,
                                formatFloatValue(quantity),
                                formatFloatValue(price),
                                formatDoubleValue(multiply)
                        });
                        totalPrice += multiply;
                    }

                    data.add(new String[]{"", "", "Total Price", formatDoubleValue(totalPrice)});
                    data.add(new String[]{""});
                    totalSum += totalPrice;
                }

                data.add(new String[]{"", "", "Total Sum", formatDoubleValue(totalSum)});

                try {
                    FileWriter writer = new FileWriter(csvFile);
                    CSVWriter csvWriter = new CSVWriter(writer);

                    for (String[] row : data) {
                        csvWriter.writeNext(row);
                    }

                    csvWriter.close();
                    Toast.makeText(SessionActivity.this, R.string.toast_csv_create_success, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SessionActivity.this, R.string.toast_csv_create_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static File getUniqueFile(File file) {
        String directoryPath = file.getParent();
        String fileName = file.getName();
        String name = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int count = 1;
        while (file.exists()) {
            String newName = name + "_" + count + extension;
            file = new File(directoryPath, newName);
            count++;
        }

        return file;
    }

    private void showSaveAsDialog(@Nullable Intent intent) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_as, null);

        final EditText input = dialogView.findViewById(R.id.edit_text_file_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.file_save_alertbox_title);
        builder.setView(dialogView);
        
        builder.setPositiveButton(R.string.file_save_alertbox_positive_button, (dialog, which) -> {
            String fileName = input.getText().toString().trim();
            if (!fileName.isEmpty()) {
                if (!fileName.endsWith(".csv")) {
                    fileName += ".csv";
                }
                try {
                    writeDataToCSV(fileName);
                } catch (Exception e) {
                    Toast.makeText(SessionActivity.this, R.string.toast_something_wrong, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_file_name_empty, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.permission_alertbox_negative_button, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}