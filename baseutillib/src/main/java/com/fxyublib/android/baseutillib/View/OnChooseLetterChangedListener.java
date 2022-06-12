package com.fxyublib.android.baseutillib.View;

/**
 * Created by yub on 2020/09/19.
 */
public interface OnChooseLetterChangedListener {
    /**
     * 滑动时
     * @param s
     */
    void onChooseLetter(String s);

    /**
     * 手指离开
     */
    void onNoChooseLetter();
}
