package com.fxyublib.android.baseutillib.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxyublib.android.baseutillib.R;

//1,创建LoadingDialog继承Dialog并实现构造方法
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    //2,创建静态内部类Builder，将dialog的部分属性封装进该类
    public static class Builder{

        private Context context;
        //提示信息
        private String message;
        //是否展示提示信息
        private boolean isShowMessage=true;
        //是否按返回键取消
        private boolean isCancelable=true;
        //是否取消
        private boolean isCancelOutside=false;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置提示信息
         * @param message
         * @return
         */
        public Builder setMessage(String message){
            this.message=message;
            return this;
        }

        /**
         * 设置是否显示提示信息
         * @param isShowMessage
         * @return
         */
        public Builder setShowMessage(boolean isShowMessage){
            this.isShowMessage=isShowMessage;
            return this;
        }

        /**
         * 设置是否可以按返回键取消
         * @param isCancelable
         * @return
         */
        public Builder setCancelable(boolean isCancelable){
            this.isCancelable=isCancelable;
            return this;
        }

        /**
         * 设置是否可以取消
         * @param isCancelOutside
         * @return
         */
        public Builder setCancelOutside(boolean isCancelOutside){
            this.isCancelOutside=isCancelOutside;
            return this;
        }

        //创建Dialog
        public LoadingDialog create(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.dialog_loading,null);

            //设置带自定义主题的dialog
            LoadingDialog loadingDailog = new LoadingDialog(context, R.style.DialogFullscreen);
//            TextView msgText= (TextView) view.findViewById(R.id.tipTextView);
//            if(isShowMessage){
//                msgText.setText(message);
//            }else{
//                msgText.setVisibility(View.GONE);
//            }
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(isCancelable);
            loadingDailog.setCanceledOnTouchOutside(isCancelOutside);
            return loadingDailog;
        }
    }

    static LoadingDialog _dlgLoading = null;

    public static void show2(Context context, int secord) {

        LoadingDialog.Builder builder=new LoadingDialog.Builder(context)
                .setMessage("加载中...")
                .setCancelable(false);
        _dlgLoading = builder.create();
        _dlgLoading.show();

        if(secord == 0) {
            secord = 5;
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                close2();
            }
        },secord*1000);
    }

    public static void close2() {
        if(_dlgLoading != null) {
            _dlgLoading.cancel();
            _dlgLoading.dismiss();
            _dlgLoading = null;
        }
    }

    public static void cancelDelay(long delayMillis) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                close2();
            }
        },delayMillis);
    }
}
