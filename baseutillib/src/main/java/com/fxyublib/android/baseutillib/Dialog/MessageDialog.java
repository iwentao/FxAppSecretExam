package com.fxyublib.android.baseutillib.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MessageDialog extends Dialog {
    public MessageDialog(Context context, int width, int height, View layout, int style, int gravity) {
        super(context, style);
        setContentView(layout);

        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();

        if(gravity == Gravity.CENTER) {
            params.gravity = Gravity.CENTER;
        }
        else {
            params.gravity = Gravity.BOTTOM;
            params.width = ViewHelper.getScreenWidth(context);
        }

        window.setAttributes(params);
    }

    public static class ViewHelper {

        public static int getScreenHigth(Context context){
            return context.getResources().getDisplayMetrics().heightPixels;
        }

        public static int getScreenWidth(Context context){
            return context.getResources().getDisplayMetrics().widthPixels;
        }
    }
}

