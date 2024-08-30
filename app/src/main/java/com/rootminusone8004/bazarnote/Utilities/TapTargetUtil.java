package com.rootminusone8004.bazarnote.Utilities;

import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.rootminusone8004.bazarnote.R;

public class TapTargetUtil {

    public static TapTarget getDefaultTapTarget(View target, String title, String description) {
        return TapTarget.forView(target, title, description)
            .outerCircleColor(R.color.customOrange)
            .outerCircleAlpha(0.96f)
            .targetCircleColor(R.color.customDarkBackground)
            .titleTextSize(20)
            .titleTextColor(R.color.white)
            .descriptionTextSize(16)
            .descriptionTextColor(R.color.white)
            .textColor(R.color.white)
            .dimColor(R.color.black)
            .drawShadow(true)
            .cancelable(false)
            .tintTarget(true)
            .transparentTarget(true);
    }
}
