package com.rootminusone8004.bazarnote.ui;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rootminusone8004.bazarnote.R;
import com.rootminusone8004.bazarnote.data.Session;
import com.rootminusone8004.bazarnote.databinding.ActivitySessionBinding;
import com.rootminusone8004.bazarnote.databinding.DialogSaveAsBinding;
import com.rootminusone8004.bazarnote.guide.TapSessionActivity;
import com.rootminusone8004.bazarnote.ui.adapter.SessionAdapter;
import com.rootminusone8004.bazarnote.util.AppExecutors;
import com.rootminusone8004.bazarnote.util.CsvExporter;
import com.rootminusone8004.bazarnote.util.FormatUtils;
import com.rootminusone8004.bazarnote.util.StoragePermission;
import com.rootminusone8004.bazarnote.viewmodel.SessionViewModel;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/** Home screen: lists sessions, and lets the user pick some of them to export as a CSV file. */
public class SessionActivity extends AppCompatActivity {
    private static final String TAG = "SessionActivity";
    private static final String CSV_EXTENSION = ".csv";

    private ActivitySessionBinding binding;
    private SessionViewModel sessionViewModel;
    private SessionAdapter adapter;
    private StoragePermission storagePermission;
    private boolean areCheckboxesVisible = false;

    private final ActivityResultLauncher<Intent> addSessionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onSessionAdded);
    private final ActivityResultLauncher<Intent> noteTransferLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onSessionUpdated);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new TapSessionActivity(this).startGuide();

        storagePermission = new StoragePermission(this, this::showSaveAsDialog);

        setUpSessionList();
        binding.buttonAddSession.setOnClickListener(v ->
                addSessionLauncher.launch(new Intent(this, AddSessionActivity.class)));
        binding.cardCheckboxShowBtn.setOnClickListener(v -> {
            storagePermission.runWithPermission();
            setSelectionMode(false);
        });
    }

    private void setUpSessionList() {
        binding.sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.sessionRecyclerView.setHasFixedSize(true);

        adapter = new SessionAdapter();
        binding.sessionRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(session ->
                noteTransferLauncher.launch(MainActivity.newIntent(this, session)));

        sessionViewModel = new ViewModelProvider(this).get(SessionViewModel.class);
        sessionViewModel.getAllSessions().observe(this, adapter::submitList);

        SwipeToDelete.attach(binding.sessionRecyclerView,
                position -> sessionViewModel.delete(adapter.getSessionAt(position)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(!areCheckboxesVisible);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.delete_all_sessions) {
            sessionViewModel.deleteAllSessions();
        } else if (itemId == R.id.show_summation) {
            showTotal();
        } else if (itemId == R.id.session_save_csv_file) {
            adapter.showAllCheckboxesWithTick();
            setSelectionMode(true);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /** Switches between the normal list and the "pick sessions to export" mode. */
    private void setSelectionMode(boolean enabled) {
        if (!enabled) {
            adapter.hideAllCheckboxes();
        }
        int visibility = enabled ? View.VISIBLE : View.GONE;
        binding.cardCheckboxShowBtn.setVisibility(visibility);
        binding.guidingMessage.setVisibility(visibility);
        areCheckboxesVisible = enabled;
        invalidateOptionsMenu();
    }

    private void onSessionAdded(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        Session session = new Session(data.getStringExtra(AddSessionActivity.EXTRA_SESSION));
        session.setPrice(0.0);
        sessionViewModel.insert(session, () -> toast(R.string.toast_session_dumplicate));
    }

    /** Applies the total and snapshot sent back by {@link MainActivity} for the session it was showing. */
    private void onSessionUpdated(ActivityResult result) {
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) {
            return;
        }
        Session session = new Session(data.getStringExtra(MainActivity.EXTRA_SESSION_NAME));
        session.setSessionId(data.getIntExtra(MainActivity.EXTRA_SESSION_ID, -1));
        session.setPrice(data.getDoubleExtra(MainActivity.EXTRA_SESSION_SUM, 0.0));
        session.setJsonInfo(data.getStringExtra(MainActivity.EXTRA_SESSION_JSON));
        sessionViewModel.update(session);
    }

    private void showTotal() {
        List<Session> sessions = adapter.getCurrentList();
        if (sessions.isEmpty()) {
            toast(R.string.toast_no_sessions);
            return;
        }
        double sum = 0;
        for (Session session : sessions) {
            sum += session.getPrice();
        }
        Toast.makeText(this, FormatUtils.formatNumber(sum), Toast.LENGTH_SHORT).show();
    }

    private void showSaveAsDialog() {
        DialogSaveAsBinding dialogBinding = DialogSaveAsBinding.inflate(getLayoutInflater());

        new AlertDialog.Builder(this)
                .setTitle(R.string.file_save_alertbox_title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.file_save_alertbox_positive_button, (dialog, which) -> {
                    String fileName = dialogBinding.editTextFileName.getText().toString().trim();
                    if (fileName.isEmpty()) {
                        toast(R.string.toast_file_name_empty);
                    } else {
                        exportCheckedSessions(fileName.endsWith(CSV_EXTENSION) ? fileName : fileName + CSV_EXTENSION);
                    }
                })
                .setNegativeButton(R.string.permission_alertbox_negative_button, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void exportCheckedSessions(String fileName) {
        List<Session> sessions = adapter.getCurrentList();
        if (sessions.isEmpty()) {
            toast(R.string.toast_no_sessions);
            return;
        }

        List<Session> checked = new ArrayList<>();
        for (Session session : sessions) {
            if (session.isCheckboxChecked()) {
                checked.add(session);
            }
        }

        AppExecutors.diskIO().execute(() -> {
            int messageRes;
            try {
                File exportedFile = CsvExporter.export(checked, fileName);
                MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[]{exportedFile.getAbsolutePath()},
                        new String[]{"text/csv"},
                        null
                );
                messageRes = R.string.toast_csv_create_success;
            } catch (Exception e) {
                Log.e(TAG, "Could not write " + fileName, e);
                messageRes = R.string.toast_csv_create_failed;
            }
            int message = messageRes;
            AppExecutors.mainThread().execute(() -> toast(message));
        });
    }

    private void toast(int messageRes) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show();
    }
}
