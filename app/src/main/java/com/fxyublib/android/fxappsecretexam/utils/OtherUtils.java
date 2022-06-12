package com.fxyublib.android.fxappsecretexam.utils;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.fxyublib.android.fxappsecretexam.R;

public class OtherUtils {
    public static void setTextViewColorMatrix(TextView tv, int index, float alpha) {
        float progressR = 1;
        float progressG = 1;
        float progressB = 1;
        float progressA = alpha;
        ColorMatrix matrix = new ColorMatrix();
        float[] src = new float[]{
                progressR, 0, 0, 0, 0,
                0, progressG, 0, 0, 0,
                0, 0, progressB, 0, 0,
                0, 0, 0, progressA, 0};
        matrix.set(src);

        Drawable[] drawables = tv.getCompoundDrawables();
        if (index < 0 || index >= drawables.length) {
            return;
        }

        Drawable drawable = drawables[index];
        if (drawable != null) {
            //drawable.setBounds( tv.getCompoundDrawables()[index].getBounds());
            drawable.setColorFilter(new ColorMatrixColorFilter(matrix));
            tv.setCompoundDrawables(null, drawable, null, null);
        }
    }
}
