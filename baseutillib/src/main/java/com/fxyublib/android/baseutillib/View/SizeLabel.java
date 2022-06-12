package com.fxyublib.android.baseutillib.View;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import org.xml.sax.XMLReader;

public class SizeLabel implements Html.TagHandler {
    private int size;
    private int startIndex = 0;
    private int stopIndex = 0;
    private static float density = 1;

    public SizeLabel(float density, int size) {
        //float d = context.getResources().getDisplayMetrics().density;
        this.density = density;
        this.size = size;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(tag.toLowerCase().equals("size")) {
            if(opening){
                startIndex = output.length();
            }else{
                stopIndex = output.length();
                output.setSpan(new AbsoluteSizeSpan(dip2px(size)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static int dip2px(float dpValue) {
        final float scale = density;
        return (int) (dpValue * scale + 0.5f);
    }
}
