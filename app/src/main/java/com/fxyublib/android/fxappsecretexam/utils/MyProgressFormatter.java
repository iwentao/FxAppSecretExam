package com.fxyublib.android.fxappsecretexam.utils;

import com.fxyublib.android.baseutillib.View.CircleProgressBar;

public final class MyProgressFormatter implements CircleProgressBar.ProgressFormatter {
    private static final String DEFAULT_PATTERN = "顺序练习\n%d%%";

    @Override
    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));
    }
}
