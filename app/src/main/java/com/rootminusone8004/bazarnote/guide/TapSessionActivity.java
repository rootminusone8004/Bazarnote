package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guide for the session list screen. */
public class TapSessionActivity {
    private static final String PREF_SHOWN = "tapTargetSessionActivityShown";

    private final Activity activity;

    public TapSessionActivity(Activity activity) {
        this.activity = activity;
    }

    public void startGuide() {
        if (!GuidePrefs.markShown(activity, PREF_SHOWN)) {
            return;
        }
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        activity.findViewById(R.id.button_add_session),
                        activity.getString(R.string.tap_target_add_session_title),
                        activity.getString(R.string.tap_target_add_session_description)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        showCsvButtonTarget();
                    }
                });
    }

    private void showCsvButtonTarget() {
        View targetView = activity.findViewById(R.id.card_checkbox_show_btn);
        targetView.setVisibility(View.VISIBLE);

        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        targetView,
                        activity.getString(R.string.tap_target_csv_button_title),
                        activity.getString(R.string.tap_target_csv_button_description)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        targetView.setVisibility(View.GONE);
                    }
                });
    }
}
