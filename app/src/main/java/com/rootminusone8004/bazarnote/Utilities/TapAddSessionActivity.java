package com.rootminusone8004.bazarnote.Utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

public class TapAddSessionActivity {
    private static final String PREF_TAP_TARGET_ADD_SESSION_ACTIVITY_SHOWN = "tapTargetAddSessionActivityShown";

    private final Activity activity;
    private final SharedPreferences prefs;

    public TapAddSessionActivity(Activity activity) {
        this.activity = activity;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void startGuide() {
        boolean hasShownTapTarget = prefs.getBoolean(PREF_TAP_TARGET_ADD_SESSION_ACTIVITY_SHOWN, false);

        if (!hasShownTapTarget) {
            showFirstTapTarget();

            // Set the flag to true so that the guide isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_TAP_TARGET_ADD_SESSION_ACTIVITY_SHOWN, true);
            editor.apply();
        }
    }

    private void showFirstTapTarget() {
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                activity.findViewById(R.id.save_note),
                activity.getString(R.string.tap_target_save_session_title),
                activity.getString(R.string.tap_target_save_session_description)
            )
        );
    }
}
