package com.rootminusone8004.bazarnote.guide;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;

/** First-run guide for the first session card. */
public class TapSessionAdapter {
    private static final String PREF_SHOWN = "tapTargetSessionAdapterShown";

    private final Activity activity;
    private final View itemView;

    public TapSessionAdapter(View itemView) {
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
                        activity.getString(R.string.tap_target_session_card_title),
                        activity.getString(R.string.tap_target_session_card_description)),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        showPriceTarget();
                    }
                });
    }

    private void showPriceTarget() {
        TapTargetView.showFor(activity,
                TapTargetUtil.getDefaultTapTarget(
                        itemView.findViewById(R.id.text_view_session_sum),
                        activity.getString(R.string.tap_target_session_card_price_title),
                        activity.getString(R.string.tap_target_session_card_price_description)));
    }
}
