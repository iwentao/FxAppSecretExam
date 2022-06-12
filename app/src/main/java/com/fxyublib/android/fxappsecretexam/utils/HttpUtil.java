package com.fxyublib.android.fxappsecretexam.utils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    // 异步请求方法
    public static void sendRequestWithOkhttp(String address,okhttp3.Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    // 同步请求方法
    public static String sendRequestWithOkhttp(String url) throws IOException {
        OkHttpClient client=new OkHttpClient();
        Request request = new Request.Builder().url(url) .build();
        Response response= client.newCall(request).execute();
        String message=response.body().string();
        return message;
    }
}
