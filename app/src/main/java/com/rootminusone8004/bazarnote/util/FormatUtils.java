package com.rootminusone8004.bazarnote.util;

import java.util.Locale;

public final class FormatUtils {
    private FormatUtils() {
    }

    /** Formats whole numbers without decimals ("5") and everything else with two ("5.25"). */
    public static String formatNumber(double value) {
        return String.format(Locale.ENGLISH, value % 1 == 0 ? "%.0f" : "%.2f", value);
    }
}
