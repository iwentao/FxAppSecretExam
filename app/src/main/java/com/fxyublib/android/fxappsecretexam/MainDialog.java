package com.fxyublib.android.fxappsecretexam;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.fxyublib.android.baseutillib.Application.AppManager;
import com.fxyublib.android.baseutillib.Dialog.MessageDialog;
import com.fxyublib.android.fxappsecretexam.data.EventContants;
import com.fxyublib.android.fxappsecretexam.data.MainEvent;
import com.fxyublib.android.fxappsecretexam.data.MainViewModel;
import com.fxyublib.android.fxappsecretexam.data.PrefContants;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class MainDialog {
    static private MessageDialog mMyDialog = null;

    static void showDialog_CloseApp(final Activity activity) {

    }

    static void showDialog_Login(final Context context, boolean isModifyMode) {
        String username = MyApp.get(PrefContants.PREF_APP_USERNAME, "");
        if(!TextUtils.isEmpty(username) && !isModifyMode) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("请输入你的昵称");    //设置对话框标题
        builder.setIcon(R.drawable.head_view_student);   //设置对话框标题前的图标

        final EditText edit = new EditText(context);
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        edit.setText(username);

        builder.setView(edit);
        builder.setPositiveButton("确认", null);
        builder.setCancelable(false);    //设置按钮是否可以按返回键取消,false则不可以取消
        final AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(false); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edit.getText().toString().trim();
                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(context, "昵称不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                MainEvent event = new MainEvent(EventContants.EVENT_SET_USENAME, username);
                EventBus.getDefault().post(event);
                dialog.dismiss();
            }
        });
    }

    //
    public static void showDialog_PreExam(final Activity activity) {

    }

    public static void showDialog_PrePractice(final Activity activity) {
        if(mMyDialog != null) mMyDialog.dismiss();

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_common_dialog, null);
        TextView dialog_message = view.findViewById(R.id.dialog_message);
        StringBuilder sb = new StringBuilder();

        Map<String, Object> map = MainViewModel.getData_Stats();
        int done = (int)map.get("done");
        int wrongdone = (int)map.get("wrongdone");
        int errorRate = 0;
        if(done > 0) {
            errorRate = wrongdone*100/done;
        }
        sb.append("总题数：").append(map.get("count")).append("题\n");
        sb.append("未做题：").append(map.get("undone")).append("题\n");
        sb.append("累计错题数：").append(map.get("wrongdone")).append("题\n");
        sb.append("累计答错率：").append(errorRate).append("%\n");

        dialog_message.setText(sb.toString());
        mMyDialog = new MessageDialog(activity, 0, 0, view, R.style.DialogTheme, Gravity.CENTER);
        mMyDialog.setCancelable(false);
        mMyDialog.show();

        //取消按钮的事件
        TextView dialog_btn_left = mMyDialog.findViewById(R.id.dialog_btn_left);
        dialog_btn_left.setText("我知道了");
        dialog_btn_left .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainViewModel.gotoActivity(activity, "顺序练习");
                mMyDialog.dismiss();
            }
        });

        TextView dialog_btn_right = mMyDialog.findViewById(R.id.dialog_btn_right);
        dialog_btn_right.setVisibility(View.GONE);
    }

}
