package com.rootminusone8004.bazarnote.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shared executors replacing the deprecated {@code AsyncTask}.
 * <ul>
 *     <li>{@link #diskIO()} runs work serially on a single background thread (database and file access).</li>
 *     <li>{@link #mainThread()} posts work back to the UI thread.</li>
 * </ul>
 */
public final class AppExecutors {
    private static final ExecutorService DISK_IO = Executors.newSingleThreadExecutor();
    private static final Executor MAIN_THREAD = new MainThreadExecutor();

    private AppExecutors() {
    }

    public static Executor diskIO() {
        return DISK_IO;
    }

    public static Executor mainThread() {
        return MAIN_THREAD;
    }

    private static final class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            handler.post(command);
        }
    }
}
