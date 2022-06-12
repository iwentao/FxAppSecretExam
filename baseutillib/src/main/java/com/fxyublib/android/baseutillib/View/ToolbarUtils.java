package com.fxyublib.android.baseutillib.View;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;

public class ToolbarUtils {
    /**
     * 获取TitleTextView
     * @return
     */
    static public TextView getTitleTextView(Toolbar toolbar){
        try {
            Field field = Toolbar.class.getDeclaredField("mTitleTextView");
            field.setAccessible(true);
            return (TextView) field.get(toolbar);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 动态设置Toolbar高度
    static public void reSetToolbarHeight(Toolbar toolbar) {
        try {
            Field f = Toolbar.class.getDeclaredField("mNavButtonView");
            f.setAccessible(true);
            ImageButton mNavButtonView = (ImageButton) f.get(toolbar);
            if (mNavButtonView != null) {
                Toolbar.LayoutParams l = (Toolbar.LayoutParams) mNavButtonView.getLayoutParams();
                l.gravity = Gravity.CENTER_VERTICAL;
                l.height += 100;
                l.width += 100;
                mNavButtonView.setLayoutParams(l);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * 重置Toolbar高度
     * @param height
     */
    static public void setToolbarHeight(Toolbar vToolbar, int height){
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        vToolbar.setLayoutParams(params);
    }
}
