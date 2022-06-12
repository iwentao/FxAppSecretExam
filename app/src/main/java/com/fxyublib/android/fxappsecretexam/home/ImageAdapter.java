package com.fxyublib.android.fxappsecretexam.home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fxyublib.android.fxappsecretexam.R;

import java.util.List;
import java.util.Map;

public class ImageAdapter extends BaseAdapter {
    private List<Map<String, Object>> mList;
    private LayoutInflater mInflater;
    private int mCurrentIndex = -1;

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    ImageAdapter(Context context, List<Map<String, Object>> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setCurrentIndex(int index) {
        mCurrentIndex = index;
    }

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return mList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_grid_question, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText((position + 1) + "");
        long ret = getItemId(position);
        Map<String, Object> map = mList.get(position);
        String name = map.get("name").toString();
        String answer = map.get("answer").toString();
        int qtype = (int) map.get("qtype");
        int done = (int) map.get("done");

        boolean isCurrent = (mCurrentIndex == position);
        if(done == 0 && !isCurrent) {
            viewHolder.title.setTextColor(Color.parseColor("#999999"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_gray);
        }
        else if(done == 0 ) {
            viewHolder.title.setTextColor(Color.parseColor("#999999"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_graysel);
        }
        else if(done == 1 && !isCurrent) {
            viewHolder.title.setTextColor(Color.parseColor("#3face7"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_right);
        }
        else if(done == 1 ) {
            viewHolder.title.setTextColor(Color.parseColor("#3face7"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_rightsel);
        }
        else if(done == 2 && !isCurrent) {
            viewHolder.title.setTextColor(Color.parseColor("#ff4d4d"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_wrong);
        }
        else if(done == 2 ) {
            viewHolder.title.setTextColor(Color.parseColor("#ff4d4d"));
            viewHolder.title.setBackgroundResource(R.drawable.circletext2_wrongsel);
        }

        return convertView;
    }

    class ViewHolder{
        public TextView title;
    }
}