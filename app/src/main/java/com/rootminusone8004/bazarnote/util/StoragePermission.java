package com.rootminusone8004.bazarnote.util;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.rootminusone8004.bazarnote.R;

/**
 * Makes sure the app may write CSV files to shared storage before running an action.
 * <p>
 * Create it in {@code onCreate} (it registers activity result launchers), then call {@link #runWithPermission()}.
 */
public class StoragePermission {
    private final AppCompatActivity activity;
    private final Runnable onGranted;
    private final ActivityResultLauncher<Intent> allFilesAccessLauncher;
    private final ActivityResultLauncher<String> legacyPermissionLauncher;

    /** @param onGranted action to run once storage access is available */
    public StoragePermission(AppCompatActivity activity, Runnable onGranted) {
        this.activity = activity;
        this.onGranted = onGranted;
        this.allFilesAccessLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> onPermissionResult(hasPermission()));
        this.legacyPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), this::onPermissionResult);
    }

    public void runWithPermission() {
        if (hasPermission()) {
            onGranted.run();
        } else {
            showExplanationDialog();
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void onPermissionResult(boolean granted) {
        if (granted) {
            Toast.makeText(activity, R.string.toast_permission_granted, Toast.LENGTH_SHORT).show();
            onGranted.run();
        } else {
            Toast.makeText(activity, R.string.toast_permisson_denied, Toast.LENGTH_SHORT).show();
        }
    }

    private void showExplanationDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.permission_alertbox_title)
                .setMessage(R.string.permission_alertbox_csv_write_message)
                .setPositiveButton(R.string.permission_alertbox_positive_button, (dialog, which) -> requestPermission())
                .setNegativeButton(R.string.permission_alertbox_negative_button, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            legacyPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        try {
            allFilesAccessLauncher.launch(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        } catch (ActivityNotFoundException e) {
            allFilesAccessLauncher.launch(new Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName())));
        }
    }
}
