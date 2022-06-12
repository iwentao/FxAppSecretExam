package com.fxyublib.android.baseutillib.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
* Created by yub
* 公共的蒙版ImageView,点击变暗效果
*/
@SuppressLint("AppCompatCustomView")
public class MaskableImageView extends ImageView {

    private boolean touchEffect = true;
    public final float[] BG_PRESSED = new float[] { 1, 0, 0, 0, -50, 0, 1,
            0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1, 0 };
    public final float[] BG_NOT_PRESSED = new float[] { 1, 0, 0, 0, 0, 0,
            1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };

    public MaskableImageView(Context context) {
        super(context);
    }

    public MaskableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    /**
     * 根据是否按下去来刷新bg和src
     * created by yub
     * @param pressed
     */
    private void updateView(boolean pressed){
        //如果没有点击效果
        if( !touchEffect ){
            return;
        }//end if
        if( pressed ){//点击
            /**
             * 通过设置滤镜来改变图片亮度@yub
             */
            this.setDrawingCacheEnabled(true);
            this.setColorFilter( new ColorMatrixColorFilter(BG_PRESSED) ) ;
            if(this.getDrawable()!= null)
                this.getDrawable().setColorFilter( new ColorMatrixColorFilter(BG_PRESSED) );
            else if(this.getBackground() != null)
                this.getBackground().setColorFilter( new ColorMatrixColorFilter(BG_PRESSED) );
        }else{//未点击
            this.setColorFilter( new ColorMatrixColorFilter(BG_NOT_PRESSED) ) ;
            if(this.getDrawable()!= null)
                this.getDrawable().setColorFilter( new ColorMatrixColorFilter(BG_NOT_PRESSED) );
            else if(this.getBackground() != null)
                this.getBackground().setColorFilter( new ColorMatrixColorFilter(BG_NOT_PRESSED) );
        }
    }
}
