package com.fxyublib.android.baseutillib.System;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;


import java.util.UUID;

/*
IMEI（International Mobile Equipment Identity）：
    相当于手机的身份证号码；是由15位数字组成的”电子串号”
    ，其组成结构为TAC（6位数字）+FAC（两位数字）+SNR（6位数字）+SP （1位数字），每一个手机组装完毕后都会被赋予一组号码，这便是 IMEI 。
IMSI（International Mobile Subscriber Identification Number）：
    区别移动用户的标志，存储在 SIM 卡中，用于区别移动用户的有效信息。
 */

/**
 * 获取手机信息工具类
 *
 * @author yub
 * @createtime 2020-1-1 12:06:03
 * @remarks
 */
public class MobileInfoUtils {

    /**
     * 获取手机IMEI号((International Mobile Equipment Identity,国际移动身份识别码)
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            assert telephonyManager != null;
            @SuppressLint({"MissingPermission", "HardwareIds"})
            String imei = telephonyManager.getDeviceId();

            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();

            // android 10以上已经获取不了imei了 用 android id代替
            return getFakeDeviceId() + "-"
                    + Settings.System.getString(context.getContentResolver()
                    , Settings.Secure.ANDROID_ID);
        }

    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI(Context context){
        try {
            TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            assert telephonyManager != null;
            @SuppressLint({"MissingPermission", "HardwareIds"})
            String imsi=telephonyManager.getSubscriberId();

            if(null==imsi){
                imsi="";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return getFakeDeviceId2(context);
        }
    }

    private static String getFakeDeviceId() {
        return "35" + //we make this look like a valid IMEI
            Build.BOARD.length()%10 +
            Build.BRAND.length()%10 +
            Build.CPU_ABI.length()%10 +
            Build.DEVICE.length()%10 +
            Build.DISPLAY.length()%10 +
            Build.HOST.length()%10 +
            Build.ID.length()%10 +
            Build.MANUFACTURER.length()%10 +
            Build.MODEL.length()%10 +
            Build.PRODUCT.length()%10 +
            Build.TAGS.length()%10 +
            Build.TYPE.length()%10 +
            Build.USER.length()%10;
    }

    /**
     * 获取设备唯一标识符
     *
     * @return 唯一标识符
     */
    @SuppressLint("HardwareIds")
    private static String getFakeDeviceId2(Context context) {
        String m_szDevIDShort = getFakeDeviceId();
        String serial = "serial";// 默认serial可随便定义
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // 由于 Android Q 唯一标识符权限的更改会导致
                    // android.os.Build.getSerial() 返回 unknown,
                    // 但是 m_szDevIDShort 是由硬件信息拼出来的，所以仍然保证了UUID 的唯一性和持久性。
                    serial = android.os.Build.getSerial();// Android Q 中返回 unknown
                }
            } else {
                serial = Build.SERIAL;
            }
        } catch (Exception ignored) {
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    //生成15位唯一性的订单号
    private static String getUUID() {
        //随机生成一位整数
        int random = (int) (Math.random() * 9 + 1);
        String valueOf = String.valueOf(random);
        //生成uuid的hashCode值
        int hashCode = UUID.randomUUID().toString().hashCode();
        //可能为负数
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        String value = valueOf + String.format("%014d", hashCode);
        return value;
    }
}

