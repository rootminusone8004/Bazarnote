package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guide for the first note card. */
public class TapNoteAdapter {
    private static final String PREF_SHOWN = "tapTargetNoteAdapterShown";

    private final Activity activity;
    private final View itemView;

    public TapNoteAdapter(View itemView) {
        this.itemView = itemView;
        this.activity = (Activity) itemView.getContext();
    }

    public void startGuide() {
        if (!GuidePrefs.markShown(activity, PREF_SHOWN)) {
            return;
        }
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        itemView,
                        activity.getString(R.string.tap_target_note_card_title),
                        activity.getString(R.string.tap_target_note_card_description)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        showPriceButtonTarget();
                    }
                });
    }

    private void showPriceButtonTarget() {
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        itemView.findViewById(R.id.price_btn),
                        activity.getString(R.string.tap_target_note_card_button_title),
                        activity.getString(R.string.tap_target_note_card_button_description)));
    }
}
