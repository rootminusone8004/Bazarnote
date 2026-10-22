package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guide for the note list screen. */
public class TapMainActivity {
    private static final String PREF_SHOWN = "tapTargetMainActivityShown";

    private final Activity activity;

    public TapMainActivity(Activity activity) {
        this.activity = activity;
    }

    public void startGuide() {
        if (!GuidePrefs.markShown(activity, PREF_SHOWN)) {
            return;
        }
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        activity.findViewById(R.id.button_add_note),
                        activity.getString(R.string.tap_target_add_note_title),
                        activity.getString(R.string.tap_target_add_note_description)));
    }
}
