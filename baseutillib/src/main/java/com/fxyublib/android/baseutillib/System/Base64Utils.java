package com.fxyublib.android.baseutillib.System;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Base64Utils {

    public static String getBase64(String str) {
        return getBase64(str, Base64.NO_WRAP);
    }

    public static String getFromBase64(String str) {
        return getFromBase64(str, Base64.NO_WRAP);
    }

    public static String getBase64Url(String str) {
        return getBase64(str, Base64.NO_WRAP | Base64.URL_SAFE | Base64.NO_PADDING);
    }

    public static String getFromBase64Url(String str) {
        return getFromBase64(str, Base64.NO_WRAP | Base64.URL_SAFE | Base64.NO_PADDING);
    }

    // 编码
    public static String getBase64(String str, int flag) {
        String result = "";
        if( str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), flag),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解码
    public static String getFromBase64(String str, int flag) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.decode(str, flag), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * bitmap转成base64
     *
     * @param bitmap bitmap
     * @return base64
     */
    public String bitmapToBase64(Bitmap bitmap) {
        // 将Bitmap转换成字符串
        String base64;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return base64;
    }

    /**
     * base64转成bitmap
     *
     * @param base64 base64
     * @return bitmap
     */
    public Bitmap base64ToBitmap(String base64) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

