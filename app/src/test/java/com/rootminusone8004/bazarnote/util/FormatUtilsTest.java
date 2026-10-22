package com.rootminusone8004.bazarnote.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatUtilsTest {

    @Test
    public void wholeNumbersHaveNoDecimals() {
        assertEquals("5", FormatUtils.formatNumber(5));
        assertEquals("0", FormatUtils.formatNumber(0));
        assertEquals("120", FormatUtils.formatNumber(120.0f));
    }

    @Test
    public void fractionsUseTwoDecimals() {
        assertEquals("5.25", FormatUtils.formatNumber(5.25));
        assertEquals("0.10", FormatUtils.formatNumber(0.1f));
        assertEquals("2.50", FormatUtils.formatNumber(2.5));
    }

    @Test
    public void usesDotAsDecimalSeparatorRegardlessOfLocale() {
        java.util.Locale previous = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.GERMANY);
            assertEquals("1.50", FormatUtils.formatNumber(1.5));
        } finally {
            java.util.Locale.setDefault(previous);
        }
    }
}
