package com.fxyublib.android.fxappsecretexam.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.fxyublib.android.baseutillib.Network.DingDing;
import com.fxyublib.android.baseutillib.Network.MyUpdater;
import com.fxyublib.android.baseutillib.System.BuildHelper;
import com.fxyublib.android.baseutillib.System.DeviceId;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.R;


public class MainUpdate {
    final private static String TAG = "MainUpdate";
    private static Handler handler;
    private int mDingdingInterval = 60*60; //60分钟更新时间间隔
    private int mAppUpdateInterval = 60*30; //半小时更新时间间隔

    @SuppressLint("HandlerLeak")
    public void checkAppUpdate(final Context context, boolean isForceUpdate, boolean isShowToast) {

    }

    public void sendDingdingMessage(final Context context, boolean isForceUpdate) {

    }

    static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, permissions,100);
        }
        else {
            checkAppUpdate(activity, true, false);
        }
    }
}
