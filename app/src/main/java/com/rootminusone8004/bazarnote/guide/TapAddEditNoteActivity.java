package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guides for the add/edit note screen (one for notes, one for adding a price). */
public class TapAddEditNoteActivity {
    private static final String PREF_NOTE_SHOWN = "tapTargetAddNoteActivityShown";
    private static final String PREF_PRICE_SHOWN = "tapTargetAddPriceShown";

    private final Activity activity;

    public TapAddEditNoteActivity(Activity activity) {
        this.activity = activity;
    }

    public void startGuide() {
        showSaveTarget(PREF_NOTE_SHOWN, R.string.tap_target_save_note_title, R.string.tap_target_save_note_description);
    }

    public void startGuideInPrice() {
        showSaveTarget(PREF_PRICE_SHOWN, R.string.tap_target_save_price_title, R.string.tap_target_save_price_description);
    }

    private void showSaveTarget(String prefKey, int titleRes, int descriptionRes) {
        if (!GuidePrefs.markShown(activity, prefKey)) {
            return;
        }
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        activity.findViewById(R.id.save_note),
                        activity.getString(titleRes),
                        activity.getString(descriptionRes)));
    }
}
