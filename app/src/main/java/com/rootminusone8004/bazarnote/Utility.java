package com.rootminusone8004.bazarnote;

import java.util.Locale;

public class Utility {
    public static String formatFloatValue(float value) {
        if (value % 1 == 0) {
            return String.format(Locale.ENGLISH, "%.0f", value);
        } else {
            return String.format(Locale.ENGLISH, "%.2f", value);
        }
    }

    public static String formatDoubleValue(double value) {
        if (value % 1 == 0) {
            return String.format(Locale.ENGLISH, "%.0f", value);
        } else {
            return String.format(Locale.ENGLISH, "%.2f", value);
        }
    }
}
