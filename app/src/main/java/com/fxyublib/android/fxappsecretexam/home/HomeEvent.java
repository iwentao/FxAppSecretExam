package com.fxyublib.android.fxappsecretexam.home;

public class HomeEvent {
    public static int EVENT_UPDATE = 0;
    public static int EVENT_NEXT = 1;
    public static int EVENT_STATS = 2;
    public static int EVENT_PLAY_SOUND = 3;

    int what;
    public String data;
    public String title;
    public String content;

    public HomeEvent(int what) {
        this.what = what;
    }

    HomeEvent(int what, String data) {
        this.what = what;
        this.data = data;
    }
}
