package com.fxyublib.android.fxappsecretexam.data;

import java.util.HashMap;
import java.util.List;

public class MainEvent {
    public int what;
    public String data;
    public String title;
    public String content;

    public MainEvent(int what) {
        this.what = what;
    }

    public MainEvent(int what, String data) {
        this.what = what;
        this.data = data;
    }

}
