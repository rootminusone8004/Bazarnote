package com.rootminusone8004.bazarnote.guide;

import android.content.Context;
import android.content.SharedPreferences;

/** Remembers which onboarding guides have already been shown. */
final class GuidePrefs {
    // Same file that PreferenceManager.getDefaultSharedPreferences() uses, so existing flags keep working.
    private static final String PREFS_SUFFIX = "_preferences";

    private GuidePrefs() {
    }

    /**
     * @return {@code true} the first time it is called for {@code key} (and records it), {@code false} afterwards
     */
    static boolean markShown(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName() + PREFS_SUFFIX, Context.MODE_PRIVATE);
        if (prefs.getBoolean(key, false)) {
            return false;
        }
        prefs.edit().putBoolean(key, true).apply();
        return true;
    }
}
