package com.fxyublib.android.baseutillib.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.fxyublib.android.baseutillib.R;

public class DetailTextDialog {
    private static AlertDialog mDialog = null;

    public static void show(final Context context, String text, boolean isHtml) {
        close();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        mDialog = builder.create();

        final View dialogView = View.inflate(context, R.layout.dialog_detailtext, null);
        mDialog.setView(dialogView);
        mDialog.show();

        TextView tv = (TextView) dialogView.findViewById(R.id.tv_content);
        if(isHtml) {
            tv.setText(Html.fromHtml(text));
        }
        else {
            tv.setText(text);
        }
    }

    public static void close() {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = null;
    }
}
