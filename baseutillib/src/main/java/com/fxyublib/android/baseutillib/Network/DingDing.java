package com.fxyublib.android.baseutillib.Network;
import com.alibaba.fastjson.JSONObject;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request.Builder;

public class DingDing {
    private static OkHttpClient client = new OkHttpClient();

    public void test(){
        sendToDingDing("test", "test", "https://oapi.dingtalk.com/robot/send?access_token=xxx");
    }

    /**
     * 发送钉钉消息
     * @param title 标题
     * @param message 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return
     */
    public static boolean sendToDingDing(String title, String message, String webhook) {
        try {
            JSONObject text = new JSONObject();
            text.put("text", message);
            text.put("title", title);

            JSONObject json = new JSONObject();
            json.put("msgtype", "markdown");
            json.put("markdown", text);
            String jsonString = json.toString();

            String type = "application/json; charset=utf-8";
            RequestBody body = RequestBody.create(MediaType.parse(type), jsonString);
            Builder builder = new Request.Builder().url(webhook);
            builder.addHeader("Content-Type", type).post(body);

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String string = Objects.requireNonNull(response.body()).string();
            System.out.println(String.format("send ding message:%s", string));
            //logger.info("send ding message:{}", string);
            JSONObject res = JSONObject.parseObject(string);
            return res.getIntValue("errcode") == 0;
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("发送钉钉消息错误！ ", e);
            return false;
        }
    }

}
