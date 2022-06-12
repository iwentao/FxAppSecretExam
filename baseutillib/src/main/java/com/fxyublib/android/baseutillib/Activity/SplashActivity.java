package com.fxyublib.android.baseutillib.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.fxyublib.android.baseutillib.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SplashActivity";
    private Timer timer = new Timer();
    private int recLen = 3;
    private TextView tv_exit;
    private TextView tv_version;
    private static int _sleepMode = 0;
    private static String _actionName;
    private static String _versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate( savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //hideBottomUIMenu();
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();//隐藏标题栏

        setContentView(R.layout.activity_splash);
        initView();

        if(_sleepMode == 0) {
            Thread myThread=new Thread(){//创建子线程
                @Override
                public void run() {
                    try{
                        sleep(1000);//使程序休眠五秒
                        gotoActivity();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();//启动线程
        }
        else {
            timer.schedule(task, 1000, 1000);
        }

    }

    @SuppressLint("DefaultLocale")
    private void initView() {
        tv_exit = findViewById(R.id.tv_time);//跳过
        tv_exit.setOnClickListener(this);//跳过监听
        tv_exit.setText(String.format("跳过 %d", recLen));
        if(_sleepMode == 0) {
            tv_exit.setVisibility(View.GONE);
        }
        else {
            tv_exit.setVisibility(View.VISIBLE);
        }

        if(!TextUtils.isEmpty(_versionName)) {
            tv_version = findViewById(R.id.tv_version);
            tv_version.setText(_versionName);
        }

    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void gotoActivity() {
        if(_sleepMode == 1) {
            recLen = -1;
            if (timer != null)
                timer.cancel();
        }

//        Intent intent = new Intent();
//        intent.setClass(SplashActivity.this, MainActivity.class);
//        startActivity(intent);

        Intent intent = new Intent();
        intent.setAction(_actionName);
        startActivity(intent);
        finish();
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @SuppressLint("DefaultLocale")
                @Override
                public void run() {
                    recLen--;

                    if (recLen == 0) {
                        tv_exit.setText(" 跳过 ");
                        gotoActivity();
                    } else if (recLen >= 0) {
                        tv_exit.setText(String.format("跳过 %d", recLen));
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_time) {
            System.out.println("SplashActivity: onClick");
            gotoActivity();
        }
    }

    public static void setSleepMode(int mode) {
        _sleepMode = mode;
    }

    public static void setActionName(String name) {
        _actionName = name;
    }

    public static void setVersionName(String versionName) {
        _versionName = versionName;
    }

    public void in(View v) {
        //startActivity(new Intent(this, SecondActivity.class));
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void out(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
