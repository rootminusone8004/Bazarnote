package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guide for the "add session" screen. */
public class TapAddSessionActivity {
    private static final String PREF_SHOWN = "tapTargetAddSessionActivityShown";

    private final Activity activity;

    public TapAddSessionActivity(Activity activity) {
        this.activity = activity;
    }

    public void startGuide() {
        if (!GuidePrefs.markShown(activity, PREF_SHOWN)) {
            return;
        }
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        activity.findViewById(R.id.save_note),
                        activity.getString(R.string.tap_target_save_session_title),
                        activity.getString(R.string.tap_target_save_session_description)));
    }
}
