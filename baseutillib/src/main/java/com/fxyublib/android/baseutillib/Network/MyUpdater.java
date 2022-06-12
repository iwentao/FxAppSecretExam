package com.fxyublib.android.baseutillib.Network;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.fxyublib.android.baseutillib.BuildConfig;
import com.fxyublib.android.baseutillib.R;
import com.fxyublib.android.baseutillib.View.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fxyublib.android.baseutillib.Application.AppUtils.getPackageName;

public class MyUpdater {
    // 应用程序Context
    private Context mContext;
     // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private int mProgressPos;// 当前进度
    private boolean interceptFlag = false;// 用户取消下载
    private boolean isShowToast = false;

    private static final int DOWN_NONE = 1;
    private static final int DOWN_CHECKVER = 2;
    private static final int DOWN_UPDATE = 3;
    private static final int DOWN_OVER = 4;
    private static final int DOWN_ALREADY_NEWER = 5;

    private static final String mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.apk";
    private static String mServerUrl = "";
    private static String mUpdateLogFile = "update.json";

    // 下载安装包的网络路径
    private String mApkUrl;
    private String mAppVersion;
    private String mAppPackageName;
    private Map<String, Object> mUpdateData;

    // 通知处理刷新界面的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_NONE:

                    break;
                case DOWN_CHECKVER:
                    showNoticeDialog();
                    break;
                case DOWN_UPDATE:
                    mProgressBar.setProgress(mProgressPos);
                    mProgressText.setText(mProgressPos + "%");
                    break;
                case DOWN_OVER:
                    installApk();
                    //installApk(mContext, mSavePath);
                    break;
                case DOWN_ALREADY_NEWER:
                    if(isShowToast) ToastUtils.showShortToast(mContext, "版本已最新！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public MyUpdater(Context context, String serverUrl, String updateLogFile, boolean isShowToast) {
        this.mContext = context;
        this.mServerUrl = serverUrl;
        this.mUpdateLogFile = updateLogFile;
        this.isShowToast = isShowToast;
        this.mAppPackageName = getPackageName(context);
        this.mApkUrl = "";
        this.mAppVersion = getVersionName(context);

        Log.d("MyUpdater", "PackageName => " + mAppPackageName);
    }

    // 显示更新程序对话框，供主程序调用
    public void checkUpdateInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if(mServerUrl.isEmpty())
                        return;

                    //http://120.79.54.166:12345/update.json
                    String updateUrl = mServerUrl + "/" + mUpdateLogFile;
                    mUpdateData = getUpdateInfo(updateUrl);
                    if(mUpdateData.size() == 0)
                        return;

                    String version = (String) mUpdateData.get("version");
                    if(version == null || version.isEmpty())
                        return;

                    float verNew = Float.valueOf(version);
                    float verApp = Float.valueOf(mAppVersion);
                    System.out.println("verNew:" + verNew+ ",verApp:" + verApp);

                    if(version.equals(mAppVersion) || verNew <= verApp) {
                        mHandler.sendEmptyMessage(DOWN_ALREADY_NEWER);
                        return;
                    }

                    mHandler.sendEmptyMessage(DOWN_CHECKVER);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 询问是否更新
     */
    private void showNoticeDialog() {
        String version = (String) mUpdateData.get("version");
        String date = (String) mUpdateData.get("date");
        String filename = (String) mUpdateData.get("filename");
        String data = (String) mUpdateData.get("data");

        System.out.println("version" + version);
        System.out.println("AppVersion" + mAppVersion);
        System.out.println("date" + date);
        System.out.println("filename" + filename);
        System.out.println("data" + data);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        String updateMsg = "有最新的软件包(v"+mAppVersion+" -> v"+version+")，请下载！\n\n";
        updateMsg += data;
        builder.setMessage(updateMsg);

        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });

        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();

    }

    /**
     * 更新进度对话框
     */
    private void showDownloadDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);

        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.dialog_progressbar, null);
        mProgressBar = v.findViewById(R.id.progressBar);
        mProgressText = v.findViewById(R.id.progressPos);
        builder.setCancelable(false);
        builder.setView(v);// 设置对话框的内容为一个View
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
                System.exit(0);//强制更新，取消更新便退出程序
            }
        });

        Dialog downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        downloadApk();
    }

    /**
     * 启动下载apk线程
     */
    private void downloadApk() {
        Thread downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 打开apk启动安装，并在安装完成后自动打开
     */
    private void installApk() {
        File apkfile = new File(mSavePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后自动打开
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //AndroidManifest provider authorities
            //Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", apkfile);
            Uri apkUri = FileProvider.getUriForFile(mContext, this.mAppPackageName + ".fileProvider", apkfile);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
//            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
//                    "application/vnd.android.package-archive");// File.toString()会返回路径信息
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    private static void installApk(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(file);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {

                String filename = (String) mUpdateData.get("filename");
                mApkUrl = mServerUrl + "/" + filename;
                url = new URL(mApkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                FileOutputStream outStream = new FileOutputStream(mSavePath);
                int count = 0;
                byte[] buf = new byte[1024];
                do {
                    int numread = ins.read(buf);
                    count += numread;
                    mProgressPos = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    outStream.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消停止下载
                outStream.close();
                ins.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Map<String, Object> getUpdateInfo(String url) throws IOException, JSONException {
        Map<String, Object> dataArr = new HashMap<>();

        Document doc = null;
        doc = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)")
                .timeout(5000)
                .get();

        //{"version":"1.1","filename":"FxAppLottery-debug-1.1-20200731.apk","date":"20200731","msg":"ok","data":"1.部分bug修复\n2.增加新功能"}
        String jsonText = doc.text();
        if(jsonText.isEmpty()) {
            return dataArr;
        }

        JSONObject jsonObject = new JSONObject(jsonText);
        String version = jsonObject.optString("version");
        String filename = jsonObject.optString("filename");
        String date = jsonObject.optString("date");
        String data = jsonObject.optString("data");

        dataArr.put("version", version);
        dataArr.put("filename", filename);
        dataArr.put("date", date);
        dataArr.put("data", data);

        return dataArr;
    }

    //获取当前版本号
    private static String getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            int versioncode = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
