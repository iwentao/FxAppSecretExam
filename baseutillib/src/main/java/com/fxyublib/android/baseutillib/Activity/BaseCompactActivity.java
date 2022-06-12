package com.fxyublib.android.baseutillib.Activity;

import com.fxyublib.android.baseutillib.Application.AppManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseCompactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}
