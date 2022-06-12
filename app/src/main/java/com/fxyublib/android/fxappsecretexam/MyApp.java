package com.fxyublib.android.fxappsecretexam;

import com.fxyublib.android.baseutillib.Activity.SplashActivity;
import com.fxyublib.android.baseutillib.Application.BaseApp;
import com.fxyublib.android.baseutillib.Network.MyUpdater;
import com.fxyublib.android.baseutillib.View.TypefaceUtil;

public class MyApp extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();

        SplashActivity.setActionName("com.fxyublib.android.action.fxappsecretexam");
        SplashActivity.setVersionName("  "+MyApp.getAppContext().getString(R.string.app_longname)
                +" v" + MyUpdater.getVersionName(MyApp.getAppContext())
                + "." + MyApp.getAppContext().getString(R.string.app_devdate));
        SplashActivity.setSleepMode(0);

        //AppTheme，<item name="android:typeface">monospace</item>
        TypefaceUtil.replaceSystemDefaultFont(this,"fonts/FZBIAOYSJW.TTF");
        MyApp.getFont("FZBIAOYSJW.TTF");
    }
}
