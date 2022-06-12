package com.fxyublib.android.fxappsecretexam.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxyublib.android.baseutillib.Network.MyUpdater;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.R;
import com.fxyublib.android.fxappsecretexam.data.EventContants;
import com.fxyublib.android.fxappsecretexam.data.MainEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class SettingsFragment extends Fragment {
    TextView tv_sound;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.more_option, container, false);

        //version
        final TextView tv_version = root.findViewById(R.id.tv_version);
        tv_version.setText("当前版本 v" + MyUpdater.getVersionName(getActivity()));

        RelativeLayout option_check_update = root.findViewById(R.id.option_check_update);
        option_check_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainEvent event = new MainEvent(EventContants.EVENT_APP_CHECK_UPDATE, "1");
                EventBus.getDefault().post(event);
            }
        });

        //sound
        tv_sound = root.findViewById(R.id.tv_sound);
        updateSoundState();

        RelativeLayout option_control_sound = root.findViewById(R.id.option_control_sound);
        option_control_sound.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int isSoundOpen = MyApp.getInt("sound", 1);
                isSoundOpen = (isSoundOpen + 1)%2;
                MyApp.setInt("sound", isSoundOpen);
                updateSoundState();
            }
        });

        //
        final ImageView iv_reward = root.findViewById(R.id.iv_reward);
        TextView option_view_in_market = root.findViewById(R.id.option_view_in_market);
        option_view_in_market.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //startWeixinApp();
                iv_reward.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    void updateSoundState() {
        int isSoundOpen = MyApp.getInt("sound", 1);
        if(isSoundOpen == 1)
            tv_sound.setText("开");
        else
            tv_sound.setText("关");
    }

    public static boolean isWeixinAvilible(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startWeixinApp() {
        if(!isWeixinAvilible(getActivity())) {
            return;
        }

        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        getActivity().startActivity(intent);

        //String url="weixin://";
       // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

    }
}