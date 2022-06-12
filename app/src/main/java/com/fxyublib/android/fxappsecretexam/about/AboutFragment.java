package com.fxyublib.android.fxappsecretexam.about;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.fxyublib.android.baseutillib.Network.MyUpdater;
import com.fxyublib.android.fxappsecretexam.R;
import com.fxyublib.android.fxappsecretexam.data.EventContants;
import com.fxyublib.android.fxappsecretexam.data.MainEvent;

import org.greenrobot.eventbus.EventBus;

public class AboutFragment extends Fragment {

    private AboutViewModel sendViewModel;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        TextView tv_version = root.findViewById(R.id.tv_version);
        tv_version.setText(MyUpdater.getVersionName(getActivity())
                + "." + getString(R.string.app_devdate) );

        tv_version.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainEvent event = new MainEvent(EventContants.EVENT_APP_CHECK_UPDATE, "1");
                EventBus.getDefault().post(event);
            }
        });

        return root;
    }
}