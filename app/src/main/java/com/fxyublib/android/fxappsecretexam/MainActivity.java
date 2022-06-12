package com.fxyublib.android.fxappsecretexam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fxyublib.android.baseutillib.Activity.ActivityUtils;
import com.fxyublib.android.baseutillib.Application.AppManager;
import com.fxyublib.android.baseutillib.Database.AssetsDBHelper;
import com.fxyublib.android.baseutillib.System.BuildHelper;
import com.fxyublib.android.baseutillib.System.DeviceId;
import com.fxyublib.android.baseutillib.View.ToolbarUtils;
import com.fxyublib.android.fxappsecretexam.data.EventContants;
import com.fxyublib.android.fxappsecretexam.data.MainDiscuss;
import com.fxyublib.android.fxappsecretexam.data.MainHttp;
import com.fxyublib.android.fxappsecretexam.data.MainUpdate;
import com.fxyublib.android.fxappsecretexam.data.MainEvent;
import com.fxyublib.android.fxappsecretexam.data.PrefContants;
import com.fxyublib.android.fxappsecretexam.home.HomeEvent;
import com.fxyublib.android.fxappsecretexam.data.MainViewModel;

import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    final private static String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private MainUpdate mMainData;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tv = ToolbarUtils.getTitleTextView(toolbar);
        if(tv != null) {
            tv.setTypeface(MyApp.getPrefFont());
            tv.setTextSize(18);
            tv.setTextColor(Color.parseColor("#FFFFFF"));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        TextView nav_header_username = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
        nav_header_username.setText(MyApp.get(PrefContants.PREF_APP_USERNAME, "未登录"));

        ImageView nav_header_image = navigationView.getHeaderView(0).findViewById(R.id.nav_header_image);
        nav_header_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainDialog.showDialog_Login(MainActivity.this, true);
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller
                    , @NonNull NavDestination destination, @Nullable Bundle arguments) {
                Log.e(TAG, "onDestinationChanged() called：" + destination.getLabel());

                if (destination.getId() == R.id.nav_home) {

                }
            }
        });

        EventBus.getDefault().register(this);

        mMainData = new MainUpdate();
        //请求权限
        mMainData.verifyStoragePermissions(this);
        //发送钉钉消息
        mMainData.sendDingdingMessage(this, true);

        //数据库管理类的初始化
        AssetsDBHelper.initManager(this);

        MainDialog.showDialog_Login(this, false);
        MainViewModel.loadData_History();
        MainDiscuss.getData_Discuss();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //发送钉钉消息
        mMainData.sendDingdingMessage(this, false);
        //自动更新检测
        mMainData.checkAppUpdate(this, false, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMainData.checkAppUpdate(this, true, false);
            } else {
                Log.e(TAG, "onRequestPermissionsResult: 申请权限已拒绝");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Intent intent = new Intent(MainActivity.this, MusicService.class);
        //startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();

       // Intent intent = new Intent(MainActivity.this, MusicService.class);
       // stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AssetsDBHelper.closeAllDatabase();
        AppManager.getAppManager().finishAllActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_resetdata:
                MainViewModel.clearAllTempData();

                HomeEvent eventNext = new HomeEvent(HomeEvent.EVENT_STATS);
                EventBus.getDefault().post(eventNext);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavDestination destination = navController.getCurrentDestination();
        assert destination != null;
        Log.e(TAG, "onBackPressed: " + destination.getLabel());
        if(destination.getId() != R.id.nav_home) {
            //navController.popBackStack();
            super.onBackPressed();
        }
        else if(ActivityUtils.isForeground(this)) {
            MainDialog.showDialog_CloseApp(this);
        }
        else {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MainEvent event) {
        Log.e(TAG, "onEventMainThread: " + event.what);

        if (event.what == EventContants.EVENT_SET_USENAME) {
            String userName = event.data;
            NavigationView navigationView = findViewById(R.id.nav_view);
            TextView tv_nav_username = navigationView.getHeaderView(0).findViewById(R.id.nav_header_username);
            tv_nav_username.setText(userName);

            MyApp.set(PrefContants.PREF_APP_USERNAME, userName);
            MainHttp.sendLoginDataToServer(userName, DeviceId.getAndroidID(this), BuildHelper.getMacAddress());

        }
        else if (event.what == EventContants.EVENT_APP_CHECK_UPDATE) {
            mMainData.checkAppUpdate(this, true, true);
        }
    }
}
