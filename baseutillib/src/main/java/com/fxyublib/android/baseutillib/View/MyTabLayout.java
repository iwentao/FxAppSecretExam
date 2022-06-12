package com.fxyublib.android.baseutillib.View;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.fxyublib.android.baseutillib.R;
import com.google.android.material.tabs.TabLayout;

public class MyTabLayout extends TabLayout {
    private float mTextSizeNormal = 14;
    private float mTextSizeSelect = 15;

    public MyTabLayout(Context context) {
        super(context);
        initTabLayout();
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabLayout();
    }

    public MyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTabLayout();
    }

    private void initTabLayout() {
        this.setSelectedTabIndicatorColor(getResources().getColor(R.color.tab_indicator_color));
        this.setTabIndicatorFullWidth(false);

        this.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(Tab tab) {
                setTabSelectStyle(tab);
            }

            @Override
            public void onTabUnselected(Tab tab) {
                setTabNormalStyle(tab);
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
    }

    public void bindTitle(String[] titles) {
        if(this.getTabCount() == 0) {
            for (String title : titles) {
                this.addTab(this.newTab().setText(title), false);
            }
        }

        for (int i = 0, size = titles.length; i < size; i++) {
            Tab tab = getTabAt(i);
            if(tab != null)
                tab.setCustomView(R.layout.tab_item);
            setTabNormalStyle(tab);
        }

        select(0);
    }

    void setTabSelectStyle(Tab tab) {
        if (tab != null && tab.getCustomView() != null) {
            TextView tab_text = tab.getCustomView().findViewById(R.id.tab_text);
            tab_text.setTextColor(getResources().getColor(R.color.tab_text_sel_color));
            tab_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSelect);
            tab_text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tab_text.setVisibility(View.VISIBLE);
            tab_text.setSingleLine(true);
            //tab_text.setTextAppearance(getActivity(), R.style.TabLayoutTextSelect);
        }
    }

    void setTabNormalStyle(Tab tab) {
        if (tab != null && tab.getCustomView() != null) {
            TextView tab_text = tab.getCustomView().findViewById(R.id.tab_text);
            tab_text.setTextColor(getResources().getColor(R.color.tab_text_color));
            tab_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeNormal);
            tab_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            tab_text.setVisibility(View.VISIBLE);
            tab_text.setSingleLine(true);
            //tab_text.setEllipsize(TextUtils.TruncateAt.END);
            //tab_text.setMaxEms(20);
            tab_text.setText(tab.getText());
        }
    }

    public void select(int index) {
        Tab tab = getTabAt(index);
        if(tab != null) {
            tab.select();
        }
    }

    public void setTextSize(float normalSize, float selectSize) {
        mTextSizeNormal = normalSize;
        mTextSizeSelect = selectSize;
    }
}
