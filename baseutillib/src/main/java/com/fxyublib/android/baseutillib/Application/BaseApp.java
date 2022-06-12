package com.fxyublib.android.baseutillib.Application;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BaseApp extends Application {
    private static final String TAG = "BaseApp";
    private static String PREF_NAME = "pref.app";
    public static String PREF_FONTNAME = "pref.fontName";
    public static String PREF_FONTSIZE = "pref.fontSize";

    @SuppressLint("StaticFieldLeak")
    public static Context _context;

    private static String _IMEI;
    private static String _IMSI;
    private static String _appCacheDir;
    private static Typeface _appFont;
    private static boolean sIsAtLeastGB;
    private static Map<String, Typeface> mMapTypeface = new HashMap<String, Typeface>();

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        String processName = getProcessName(this);
        if (processName!= null) {
            String pkName = this.getPackageName();
            //只有主进程，才执行后续逻辑
            if(!processName.equals(pkName)) {
                return;
            }
        }

        Log.d(TAG, "onCreate");
        super.onCreate();

        _context = getApplicationContext();
        printFileDirs();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(_context, _appCacheDir + "/crash");
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    private String getProcessName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return null;
            }
            for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
                if (proInfo.pid == android.os.Process.myPid()) {
                    if (proInfo.processName != null) {
                        return proInfo.processName;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Context getAppContext() {
        return _context;
    }

    public static String getAppCacheDir() {
        return _appCacheDir;
    }

    private void printFileDirs() {
        List<String> dirs = new ArrayList<String>();

        dirs.add(Objects.requireNonNull(_context.getExternalCacheDir()).getPath());             // /storage/emulated/0/Android/data/com.fxyublib.android.FxTangPoetry/cache
        dirs.add(Objects.requireNonNull(_context.getExternalFilesDir("test")).getPath()); // /storage/emulated/0/Android/data/com.fxyublib.android.FxTangPoetry/files/test

        dirs.add(_context.getCacheDir().getPath());         // /data/user/0/com.fxyublib.android.FxTangPoetry/cache
        dirs.add(_context.getApplicationInfo().dataDir );   // /data/user/0/com.fxyublib.android.FxTangPoetry
        dirs.add(_context.getObbDir().getAbsolutePath());

        dirs.add(_context.getDatabasePath("test").getPath());                // /data/user/0/com.fxyublib.android.FxTangPoetry/databases/test
        dirs.add(_context.getFilesDir().getPath());                                 // /data/user/0/com.fxyublib.android.FxTangPoetry/files
        dirs.add(_context.getPackageResourcePath());                                // /data/app/com.fxyublib.android.FxTangPoetry-1/base.apk
        dirs.add(_context.getPackageCodePath());                                    // /data/app/com.fxyublib.android.FxTangPoetry-1/base.apk
        dirs.add(_context.getDir("test", Context.MODE_PRIVATE).getPath());  // /data/user/0/com.fxyublib.android.FxTangPoetry/app_test

        dirs.add(Environment.getDataDirectory().getAbsolutePath());             // /data
        dirs.add(Environment.getDownloadCacheDirectory().getAbsolutePath());    // /cache
        dirs.add(Environment.getExternalStorageDirectory().getAbsolutePath());  // /storage/emulated/0
        dirs.add(Environment.getExternalStoragePublicDirectory("test").getAbsolutePath()); // /storage/emulated/0/test
        dirs.add(Environment.getRootDirectory().getAbsolutePath());             // /system

        for (int i = 0; i < dirs.size(); i++) {
            Log.i(TAG, "dir: " + dirs.get(i) );
        }

        _appCacheDir = dirs.get(0);
    }

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }

    public static Typeface getFont(String fontName) {
        String key = "fonts/" + fontName;
        Typeface font = (Typeface)mMapTypeface.get(key);

        if (font == null) {
            try{
                font = Typeface.createFromAsset(_context.getAssets(), key);
                mMapTypeface.put(key, font);
                setPrefFont(fontName);
            }
            catch (RuntimeException ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        return font;
    }

    public static Typeface getPrefFont() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        if(prefs != null) {
            String select = prefs.getString(PREF_FONTNAME, "DEFAULT");
            if (select.toUpperCase().contains(".TTF")) {
                return getFont(select);
            }
        }

        return null;
    }

    public static void setPrefFont(String fontName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        if(prefs != null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_FONTNAME, fontName);
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void apply(SharedPreferences.Editor editor) {
        if (sIsAtLeastGB) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences() {
        SharedPreferences pre = _context.getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    public static void setFloat(String key, float value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    public static void setInt(String key, int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void setLong(String key, long value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        apply(editor);
    }

    public static void setBool(String key, boolean value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void set(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static boolean getBool(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }
}