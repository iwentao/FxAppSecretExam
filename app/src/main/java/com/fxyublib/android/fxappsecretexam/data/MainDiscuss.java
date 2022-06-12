package com.fxyublib.android.fxappsecretexam.data;

import android.util.Log;

import com.fxyublib.android.baseutillib.System.DeviceId;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MainDiscuss {
    private static final String TAG = "MainDiscuss";
    private static List<Map<String, Object>> mDiscussList;

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d(TAG, "toURLEncoded error:"+paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.d(TAG,"toURLEncoded error:"+paramString, localException);
        }

        return "";
    }

    public static String toURLDecoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.d(TAG,"toURLDecoded error:"+paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLDecoder.decode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.d(TAG,"toURLDecoded error:"+paramString, localException);
        }

        return "";
    }

    public static void sendDiscussToServer(String qid, String text ) {

    }

    public static void getData_Discuss() {

    }

    private static void parseData_Discuss(Response response) throws IOException {

    }

    public static List<Map<String, Object>> addDiscussData(List<Map<String, Object>> listData) {

        return listData;
    }

    public static void updateDiscussData(String qidFindStr, String text) {

    }

    public static String getDiscussData(String qidFindStr) {


        return "";
    }
}
