package com.fxyublib.android.fxappsecretexam.data;

import android.util.Base64;
import android.util.Log;

import com.fxyublib.android.baseutillib.Date.DateUtils;
import com.fxyublib.android.baseutillib.System.Base64Utils;
import com.fxyublib.android.baseutillib.System.DeviceId;
import com.fxyublib.android.baseutillib.System.Md5Utils;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.home.QuestionActivity;
import com.fxyublib.android.fxappsecretexam.utils.HttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class MainHttp {
    private static final String TAG = "MainHttp";

    public static void sendLoginDataToServer(String username, String userid, String userMacAddr) {

    }

    public static void getData_Login() {

    }

    private static void parseData_Login(Response response) throws IOException {

    }

    public static void sendOperatorToServer(String userid,String  startTime, String costTime, String  ctype, String num_all,String num_ok) {

    }

    public static void getData_Operator() {

    }

    static void parseData_Operator(Response response) throws IOException {

    }


}
