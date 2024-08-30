package com.rootminusone8004.bazarnote.Utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

public class TapSessionActivity {
    private static final String PREF_TAP_TARGET_SESSION_ACTIVITY_SHOWN = "tapTargetSessionActivityShown";

    private final Activity activity;
    private final SharedPreferences prefs;

    public TapSessionActivity(Activity activity) {
        this.activity = activity;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    // Method to start the tap target guide
    public void startGuide() {
        boolean hasShownTapTarget = prefs.getBoolean(PREF_TAP_TARGET_SESSION_ACTIVITY_SHOWN, false);

        if (!hasShownTapTarget) {
            showFirstTapTarget();

            // Set the flag to true so that the guide isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_TAP_TARGET_SESSION_ACTIVITY_SHOWN, true);
            editor.apply();
        }
    }

    private void showFirstTapTarget() {
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                activity.findViewById(R.id.button_add_session),
                activity.getString(R.string.tap_target_add_session_title),
                activity.getString(R.string.tap_target_add_session_description)
            ), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    showLastTapTarget();
                }
            }
        );
    }

    private void showLastTapTarget() {
        View targetView = activity.findViewById(R.id.card_checkbox_show_btn);
        targetView.setVisibility(View.VISIBLE);

        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                    targetView,
                    activity.getString(R.string.tap_target_csv_button_title),
                    activity.getString(R.string.tap_target_csv_button_description)
            ), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    targetView.setVisibility(View.GONE);
                }
            }
        );
    }
}
