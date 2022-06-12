package com.fxyublib.android.baseutillib.System;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

public class DeviceId {
    private static final byte[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static final String INVALID_MAC_ADDRESS = "02:00:00:00:00:00";

    private static final String INVALID_ANDROID_ID = "9774d56d682e549c";

    private static final String INVALID_Serial_No = "unknown";

    private static byte[] getMacInArray() {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            if (enumeration == null) {
                return null;
            }
            while (enumeration.hasMoreElements()) {
                NetworkInterface netInterface = enumeration.nextElement();
                if (netInterface.getName().equals("wlan0")) {
                    return netInterface.getHardwareAddress();
                }
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage(), e);
        }
        return null;
    }

    public static long getLongMac() {
        byte[] bytes = getMacInArray();
        if (bytes == null || bytes.length != 6) {
            return 0L;
        }
        long mac = 0L;
        for (int i = 0; i < 6; i++) {
            mac |= bytes[i] & 0xFF;
            if (i != 5) {
                mac <<= 8;
            }
        }
        return mac;
    }

    public static String getMacAddress() {
        String mac = formatMac(getMacInArray());
        if (TextUtils.isEmpty(mac) || mac.equals(INVALID_MAC_ADDRESS)) {
            return "";
        }
        return mac;
    }

    private static String formatMac(byte[] bytes) {
        if (bytes == null || bytes.length != 6) {
            return "";
        }
        byte[] mac = new byte[17];
        int p = 0;
        for (int i = 0; i <= 5; i++) {
            byte b = bytes[i];
            mac[p] = HEX_DIGITS[(b & 0xF0) >> 4];
            mac[p + 1] = HEX_DIGITS[b & 0xF];
            if (i != 5) {
                mac[p + 2] = ':';
                p += 3;
            }
        }
        return new String(mac);
    }

    public static String getAndroidID(Context context) {
        if (context != null) {
            @SuppressLint("HardwareIds")
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidId) && !INVALID_ANDROID_ID.equals(androidId)) {
                return androidId;
            }
        }
        return "";
    }

    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    public static String getSerialNo(Context context) {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    serial = Build.getSerial();
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
        }
        if (!TextUtils.isEmpty(serial) && !INVALID_Serial_No.equals(serial)) {
            return serial;
        }
        return "";
    }

    public static String getSubscriberId(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String imsi = mTelephonyMgr.getSubscriberId();
            if (!TextUtils.isEmpty(imsi)) {
                return imsi;
            }
            return imsi;
        }
        return "";
    }

    public static String getSimSerialNumber(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //返回SIM卡的序列号
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            String simSerialNumber = mTelephonyMgr.getSimSerialNumber();
            if (!TextUtils.isEmpty(simSerialNumber)) {
                return simSerialNumber;
            }
        }
        return "";
    }

    public static String getSplitNo(Context context) {
        String serial = "";
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        serial = getSerialNo(context);
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String getFingerprint() {
        //return Encryption.encrypByMD5(android.os.Build.FINGERPRINT);
        return "";
    }

}

