package com.fxyublib.android.baseutillib.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fxyublib.android.baseutillib.R;

/**
 * Created by yub on 2020/09/19.
 */
public class AZSideBar extends View {

    private Paint paint = new Paint();

    private int choose = -1;

    private boolean showBackground;
    public static String[] letters = {"搜", "#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private OnChooseLetterChangedListener onChooseLetterChangedListener;

    public AZSideBar(Context context) {
        this(context, null);
    }

    public AZSideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AZSideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBackground) {
            //setBackgroundResource(R.drawable.bg_sidebar_press);
            //setBackgroundResource(R.drawable.fab_add);
            //setBackgroundColor(Color.LTGRAY);
        }else {
            setBackgroundColor(Color.TRANSPARENT);
        }
        int height = getHeight();
        int width = getWidth();
        //平均每个字母占的高度
        int singleHeight = height / letters.length;
        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(25);
            if (i == choose) {
                paint.setColor(Color.parseColor("#FF2828"));
                paint.setFakeBoldText(true);
            }
            float x = width / 2 - paint.measureText(letters[i]) / 2;
            float y = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], x, y, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        int c = (int) (y / getHeight() * letters.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBackground = true;
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        //获取触摸位置的字符
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && onChooseLetterChangedListener != null) {
                    if (c > -1 && c < letters.length) {
                        //获取触摸位置的字符
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBackground = false;
                choose = -1;
                if (onChooseLetterChangedListener != null) {
                    //手指离开
                    onChooseLetterChangedListener.onNoChooseLetter();
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnChooseLetterChangedListener onChooseLetterChangedListener) {
        this.onChooseLetterChangedListener = onChooseLetterChangedListener;
    }
}
