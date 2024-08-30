package com.rootminusone8004.bazarnote.Utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.rootminusone8004.bazarnote.R;
import com.rootminusone8004.bazarnote.SessionAdapter;

public class TapSessionAdapter {
    private static final String PREF_TAP_TARGET_SESSION_ADAPTER_SHOWN = "tapTargetSessionAdapterShown";

    private final Activity activity;
    private final SharedPreferences prefs;
    private final SessionAdapter.SessionHolder holder;

    public TapSessionAdapter(SessionAdapter.SessionHolder holder) {
        this.holder = holder;
        this.activity = (Activity) holder.itemView.getContext();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    // Method to start the tap target guide
    public void startGuide() {
        boolean hasShownTapTarget = prefs.getBoolean(PREF_TAP_TARGET_SESSION_ADAPTER_SHOWN, false);

        if (!hasShownTapTarget) {
            showFirstTapTarget();

            // Set the flag to true so that the guide isn't shown again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PREF_TAP_TARGET_SESSION_ADAPTER_SHOWN, true);
            editor.apply();
        }
    }

    private void showFirstTapTarget() {
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                holder.itemView,
                activity.getString(R.string.tap_target_session_card_title),
                activity.getString(R.string.tap_target_session_card_description)
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
        TapTargetView.showFor(activity,
            TapTargetUtil.getDefaultTapTarget(
                holder.itemView.findViewById(R.id.text_view_session_sum),
                activity.getString(R.string.tap_target_session_card_price_title),
                activity.getString(R.string.tap_target_session_card_price_description)
            )
        );
    }
}
