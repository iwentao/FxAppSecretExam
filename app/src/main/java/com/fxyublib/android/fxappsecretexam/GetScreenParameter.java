package com.fxyublib.android.fxappsecretexam;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

public class GetScreenParameter {
    public static int m_W;
    public static int m_H;
    public static float m_density;
    public static int m_densityDpi;

    //方法一：已过时，可使用，但不建议使用
    public static void getResolution1(Context mContext) {
        Display mDisplay = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay();
        int W = mDisplay.getWidth();
        int H = mDisplay.getHeight();
    }

    //方法二：通过getWindowManager来获取屏幕尺寸的
    public static void getResolution2(Context mContext) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        int H = mDisplayMetrics.heightPixels;
        // 屏幕密度（0.75 / 1.0 / 1.5）
        float density = mDisplayMetrics.density;
        // 就是屏幕密度 * 160而已,屏幕密度DPI（120 / 160 / 240）
        int densityDpi = mDisplayMetrics.densityDpi;
    }

    //方法三：通过getResources来获取屏幕尺寸的，大部分用这个
    public static void getResolution3(Context mContext) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        m_W = mDisplayMetrics.widthPixels;
        m_H = mDisplayMetrics.heightPixels;
        m_density = mDisplayMetrics.density;
        m_densityDpi = mDisplayMetrics.densityDpi;
    }
}
