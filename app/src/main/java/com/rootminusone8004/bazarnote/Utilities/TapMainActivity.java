package com.rootminusone8004.bazarnote.Utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

public class TapMainActivity {
    private static final String PREF_TAP_TARGET_MAIN_ACTIVITY_SHOWN = "tapTargetMainActivityShown";
    private static final String PREF_TAP_TARGET_MAIN_ACTIVITY_IN_MENU_SHOWN = "tapTargetMainActivityInMenuShown";

    private final Activity activity;
    private final SharedPreferences prefs;

    public TapMainActivity(Activity activity) {
        this.activity = activity;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    // Method to start the tap target guide
    public void startGuide() {
        boolean hasShownTapTarget = prefs.getBoolean(PREF_TAP_TARGET_MAIN_ACTIVITY_SHOWN, false);

        if (!hasShownTapTarget) {
            showFirstTapTarget();

            // Set the flag to true so that the guide isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_TAP_TARGET_MAIN_ACTIVITY_SHOWN, true);
            editor.apply();
        }
    }

    public void startGuideInMenu() {
        boolean hasShownTapTarget = prefs.getBoolean(PREF_TAP_TARGET_MAIN_ACTIVITY_IN_MENU_SHOWN, false);

        if (!hasShownTapTarget) {
            showFirstTapTargetInMenu();

            // Set the flag to true so that the guide isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_TAP_TARGET_MAIN_ACTIVITY_IN_MENU_SHOWN, true);
            editor.apply();
        }
    }

    private void showFirstTapTarget() {
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                activity.findViewById(R.id.button_add_note),
                activity.getString(R.string.tap_target_add_note_title),
                activity.getString(R.string.tap_target_add_note_description)
            )
        );
    }

    private void showFirstTapTargetInMenu() {
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                activity.findViewById(android.R.id.home),
                activity.getString(R.string.tap_target_add_note_title),
                activity.getString(R.string.tap_target_add_note_description)
            )
        );
    }
}
