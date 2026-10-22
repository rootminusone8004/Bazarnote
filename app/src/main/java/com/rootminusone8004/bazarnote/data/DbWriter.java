package com.rootminusone8004.bazarnote.data;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.rootminusone8004.bazarnote.util.AppExecutors;

/** Runs database writes on the background executor and reports unique-constraint conflicts on the UI thread. */
final class DbWriter {
    private static final String TAG = "DatabaseError";

    private DbWriter() {
    }

    /**
     * @param write      the write operation to run off the main thread
     * @param onConflict invoked on the main thread if the write violates a unique constraint (may be null)
     */
    static void execute(Runnable write, @Nullable Runnable onConflict) {
        AppExecutors.diskIO().execute(() -> {
            try {
                write.run();
            } catch (SQLiteConstraintException e) {
                Log.e(TAG, "Entry with this name already exists.", e);
                if (onConflict != null) {
                    AppExecutors.mainThread().execute(onConflict);
                }
            }
        });
    }
}
