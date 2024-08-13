package com.rootminusone8004.bazarnote;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

@FunctionalInterface
interface CSV {
    void execute(@Nullable Intent intent);
}

public class Permission {
    public static final int STORAGE_PERMISSION_CODE = 1;

    private Context context;
    private Activity activity;
    private CSV csv;

    public Permission(Context context, Activity activity, CSV csv){
        this.context = context;
        this.activity = activity;
        this.csv = csv;
    }

    public void checkPermissionAndWriteToCSV(@Nullable Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showPermissionExplanationDialog();
            } else {
                csv.execute(intent);
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                showPermissionExplanationDialog();
            } else {
                csv.execute(intent);
            }
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.permission_alertbox_title)
                .setMessage(R.string.permission_alertbox_csv_write_message)
                .setPositiveButton(R.string.permission_alertbox_positive_button, (dialog, which) -> requestStoragePermission())
                .setNegativeButton(R.string.permission_alertbox_negative_button, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(activity, intent, STORAGE_PERMISSION_CODE, null);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(activity, intent, STORAGE_PERMISSION_CODE, null);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }
}
