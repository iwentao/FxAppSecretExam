package com.fxyublib.android.fxappsecretexam.home;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.data.MainDiscuss;
import com.fxyublib.android.fxappsecretexam.data.MainHttp;
import com.fxyublib.android.fxappsecretexam.data.PrefContants;
import com.fxyublib.android.fxappsecretexam.R;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: yub
 * @Description:
 * @Time:
 **/
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private List<Map<String, Object>>  mData;
    private LayoutInflater mInflater;
    private ViewPager2 mViewPager2;
    private String mAnswer;
    private boolean mIsExamMode = false;

    private int[] colorArray = new int[]{
            android.R.color.darker_gray,
            android.R.color.holo_blue_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
    };

    ViewPagerAdapter(Context context, List<Map<String, Object>> data, ViewPager2 viewPager2, boolean isExamMode) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mViewPager2 = viewPager2;
        this.mIsExamMode = isExamMode;
    }

    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycleview_item, parent, false);
        return new ViewHolder(view);
    }

    private void changeViewVisiable(ViewPagerAdapter.ViewHolder holder, Map<String, Object> map) {
        int qtype = (int) map.get("qtype");

        holder.ll_A.setVisibility(View.GONE);
        holder.ll_B.setVisibility(View.GONE);
        holder.ll_C.setVisibility(View.GONE);
        holder.ll_D.setVisibility(View.GONE);

        if(qtype == 1 || qtype == 2) {
            holder.ll_A.setVisibility(View.VISIBLE);
            holder.ll_B.setVisibility(View.VISIBLE);
            holder.ll_C.setVisibility(View.VISIBLE);
            holder.ll_D.setVisibility(View.VISIBLE);
        }
        else if(qtype == 3) {

        }

        if(qtype == 2 || qtype == 4 || qtype == 5) {
            holder.btn_submit.setVisibility(View.VISIBLE);
        }
        else {
            holder.btn_submit.setVisibility(View.GONE);
        }
    }

    private void changeViewText(ViewPagerAdapter.ViewHolder holder, Map<String, Object> map, int position) {
        String name = map.get("name").toString();
        String answer = map.get("answer").toString();
        int qtype = (int) map.get("qtype");
        int done = (int) map.get("done");
        int submit = (int) map.get("submit");
        String discuss  = map.get("discuss").toString();
        Set<String> selArr = (Set<String>) map.get("selected");

        holder.tv_Ah.setText("A");
        holder.tv_Bh.setText("B");
        holder.tv_Ch.setText("C");
        holder.tv_Dh.setText("D");

        float text_size = MyApp.getFloat(PrefContants.PREF_PRACTICE_TEXTSIZE, 18.0f);
        holder.tv_A.setTextSize(text_size);
        holder.tv_B.setTextSize(text_size);
        holder.tv_C.setTextSize(text_size);
        holder.tv_D.setTextSize(text_size);
        holder.tv_Ah.setTextSize(text_size - 2);
        holder.tv_Bh.setTextSize(text_size - 2);
        holder.tv_Ch.setTextSize(text_size - 2);
        holder.tv_Dh.setTextSize(text_size - 2);
        holder.tv_name.setTextSize(text_size);
        holder.tv_answer.setTextSize(text_size - 2);
        holder.tv_discuss.setTextSize(text_size - 2);

        if(qtype == 1 || qtype == 2) {
            String A = map.get("A").toString();
            String B = map.get("B").toString();
            String C = map.get("C").toString();
            String D = map.get("D").toString();

            holder.tv_A.setText(A);
            holder.tv_B.setText(B);
            holder.tv_C.setText(C);
            holder.tv_D.setText(D);
        }

        //题目解析
        holder.tv_discuss.setText(Html.fromHtml("<strong>笔记：</strong>\u00A0" + discuss));

        //题目内容
        String[] question_array = mInflater.getContext().getResources().getStringArray(R.array.question_array);
        String html = "<b><font color=\"#0000aa\">("+question_array[qtype-1]+")</font></b>\u00A0" + "第" + (position+1) + "题：" + name;
        if(qtype == 4) {

        }
        else {
            holder.tv_name.setText(Html.fromHtml(html));
        }


        //题目答案
        if(submit == 0) { //selArr == null || selArr.size() == 0 ||
            holder.tv_answer.setVisibility(View.GONE);
            holder.tv_discuss.setVisibility(View.GONE);
        }
        else {
            holder.tv_answer.setVisibility(View.VISIBLE);
            holder.tv_discuss.setVisibility(View.VISIBLE);
            String color = (done == 1) ? "\"#3face7\"" : "\"#FF0000\"";
            html = "<strong>答案：</strong>\u00A0<b><font color=\"#3face7\">"+answer+"</font></b>";

            if(selArr != null && selArr.size() > 0) {
                html += "\u00A0\u00A0\u00A0您选择：\u00A0<b><font color="+color+">"
                        +formatStringSet(selArr)+"</font></b>";
            }
            else if(qtype == 4) {

            }

            holder.tv_answer.setText(Html.fromHtml(html));
            holder.btn_submit.setVisibility(View.GONE);
        }
    }

    private void changeViewColor(ViewPagerAdapter.ViewHolder holder, Map<String, Object> map, int position) {
        String no = map.get("no").toString();
        String name = map.get("name").toString();
        String answer = map.get("answer").toString();
        int qtype = (int) map.get("qtype");

        holder.tv_Ah.setBackgroundResource(R.drawable.circletext_gray);
        holder.tv_Ah.setTextColor(Color.parseColor("#000000"));
        holder.tv_A.setTextColor(Color.parseColor("#000000"));
        holder.tv_Bh.setBackgroundResource(R.drawable.circletext_gray);
        holder.tv_Bh.setTextColor(Color.parseColor("#000000"));
        holder.tv_B.setTextColor(Color.parseColor("#000000"));
        holder.tv_Ch.setBackgroundResource(R.drawable.circletext_gray);
        holder.tv_Ch.setTextColor(Color.parseColor("#000000"));
        holder.tv_C.setTextColor(Color.parseColor("#000000"));
        holder.tv_Dh.setBackgroundResource(R.drawable.circletext_gray);
        holder.tv_Dh.setTextColor(Color.parseColor("#000000"));
        holder.tv_D.setTextColor(Color.parseColor("#000000"));

        Set<String> selArr = (Set<String>) map.get("selected");
        Set<String> answerArr = new HashSet<>();

        if(qtype == 1 || qtype == 2 || qtype == 3) {
            for (int i = 0; i < answer.length(); i++) {
                String answerId = answer.substring(i, i+1);
                answerArr.add(answerId);
            }
        }

        if(selArr != null) {
            for (String selected : selArr) {
                TextView tv_image = null;
                TextView tv_text = null;

                if (selected.equals("A")) {
                    tv_image = holder.tv_Ah;
                    tv_text = holder.tv_A;
                }
                else if (selected.equals("B")) {
                    tv_image = holder.tv_Bh;
                    tv_text = holder.tv_B;
                }
                else if (selected.equals("C")) {
                    tv_image = holder.tv_Ch;
                    tv_text = holder.tv_C;
                }
                else if (selected.equals("D")) {
                    tv_image = holder.tv_Dh;
                    tv_text = holder.tv_D;
                }
                else return;

                if (qtype == 2 ) { //多选题

                }
                else if (answerArr.contains(selected)) {
                    tv_image.setBackgroundResource(R.drawable.jiakao_practice_icon_true_day);
                    tv_image.setTextColor(Color.parseColor("#FFFFFF"));
                    tv_image.setText("");
                    tv_text.setTextColor(Color.parseColor("#3face7"));
                } else {
                    tv_image.setBackgroundResource(R.drawable.jiakao_practice_icon_false_day);
                    tv_image.setTextColor(Color.parseColor("#FFFFFF"));
                    tv_image.setText("");
                    tv_text.setTextColor(Color.parseColor("#ff4d4d"));
                }
            }
        }

    }

    private boolean checkAnswer() {
        return true;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        Map<String, Object> map=mData.get(position);

        //
        changeViewVisiable(holder, map);
        changeViewText(holder, map, position);
        changeViewColor(holder, map, position);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_answer;
        TextView tv_discuss;
        EditText edt_input;

        NestedScrollView relativeLayout;

        TextView tv_A;
        TextView tv_B;
        TextView tv_C;
        TextView tv_D;

        TextView tv_Ah;
        TextView tv_Bh;
        TextView tv_Ch;
        TextView tv_Dh;

        RelativeLayout ll_A;
        RelativeLayout ll_B;
        RelativeLayout ll_C;
        RelativeLayout ll_D;
        Button btn_submit;

        ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.container);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_answer = itemView.findViewById(R.id.tv_answer);
            tv_discuss = itemView.findViewById(R.id.tv_discuss);
            edt_input = itemView.findViewById(R.id.et_input);

            tv_A = itemView.findViewById(R.id.tv_a1);
            tv_B = itemView.findViewById(R.id.tv_b1);
            tv_C = itemView.findViewById(R.id.tv_c1);
            tv_D = itemView.findViewById(R.id.tv_d1);

            tv_Ah = itemView.findViewById(R.id.tv_a0);
            tv_Bh = itemView.findViewById(R.id.tv_b0);
            tv_Ch = itemView.findViewById(R.id.tv_c0);
            tv_Dh = itemView.findViewById(R.id.tv_d0);

            ll_A = itemView.findViewById(R.id.ll_a);
            ll_B = itemView.findViewById(R.id.ll_b);
            ll_C = itemView.findViewById(R.id.ll_c);
            ll_D = itemView.findViewById(R.id.ll_d);

            btn_submit = itemView.findViewById(R.id.btn_submit);
            btn_submit.setVisibility(View.GONE);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mViewPager2.getCurrentItem();
                    Map<String, Object> map=mData.get(position);
                    int qtype = (int) map.get("qtype");
                    int submit = (int) map.get("submit");
                    String answer = map.get("answer").toString();
                    Set<String> setlArr = (Set<String>) map.get("selected");

                    String sel = "";
                    if(v.getId() == R.id.tv_a0|| v.getId() == R.id.ll_a) sel = "A";
                    else if(v.getId() == R.id.tv_b0|| v.getId() == R.id.ll_b) sel = "B";
                    else if(v.getId() == R.id.tv_c0|| v.getId() == R.id.ll_c) sel = "C";
                    else if(v.getId() == R.id.tv_d0 || v.getId() == R.id.ll_d) sel = "D";
                    else return;
                    int newDone = answer.equals(sel) ? 1 : 2;

                    //单选题和判断题，如果选择一个，则禁止继续操作
                    if((qtype == 1 || qtype == 3) && setlArr.size() > 0) {
                        return;
                    }
                    else if(qtype == 2 && submit == 1) { //多选题提交之后，则禁止继续操作
                        return;
                    }

                    //保存数据到内存
                    if(qtype == 1 || qtype == 3) { //单选题和判断题只选择一次
                        setlArr.add(sel);
                        map.put("selected", setlArr);
                        map.put("done", newDone);
                        map.put("submit", 1);
                        mData.set(position, map);

                        //保存数据到数据库
                        HomeViewModel.setDone((Integer) map.get("id"), newDone);
                        HomeViewModel.setOnceDone(newDone);
                        playSound(newDone);

                        //题目做对，自动跳到下一题
                        boolean isAutoNext = MyApp.getBool(PrefContants.PREF_PRACTICE_AUTONEXT, true);
                        if(newDone == 1 && isAutoNext || mIsExamMode) {
                            HomeViewModel.delayNextQuestion(position);
                        }
                    }
                    else {  //多选题，点击"提交"按钮再判断对错
                        if(setlArr.contains(sel)) {
                            setlArr.remove(sel);
                        }
                        else {
                            setlArr.add(sel);
                        }
                        map.put("selected", setlArr);
                        mData.set(position, map);

                        playSound(3);
                    }

                    //刷新界面
                    HomeEvent event = new HomeEvent(HomeEvent.EVENT_UPDATE, position + "");
                    EventBus.getDefault().post(event);
                    notifyDataSetChanged();

                }
            };

            View.OnClickListener onClickListener2 = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = mViewPager2.getCurrentItem();
                    Map<String, Object> map = mData.get(position);
                    String id = map.get("id").toString();
                    int qtype = (int) map.get("qtype");
                    int submit = (int) map.get("submit");
                    String answer = map.get("answer").toString();
                    Set<String> selArr = (Set<String>) map.get("selected");

                    if(selArr.size() == 0 && qtype == 2) {
                        Toast.makeText(mInflater.getContext(), "请选择了答案后，再提交", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int newDone = 1;
                    if(qtype == 2) {
                        newDone = answer.equals(formatStringSet(selArr)) ? 1 : 2;
                    }
                    else if(qtype == 4) {

                    }

                    map.put("submit", 1);
                    map.put("done", newDone);
                    mData.set(position, map);

                    HomeViewModel.setDone((Integer) map.get("id"), newDone);
                    HomeViewModel.setOnceDone(newDone);
                    playSound(newDone);

                    //题目做对，自动跳到下一题
                    boolean isAutoNext = MyApp.getBool(PrefContants.PREF_PRACTICE_AUTONEXT, true);
                    if(newDone == 1 && qtype == 2 && isAutoNext || mIsExamMode) {
                        HomeViewModel.delayNextQuestion(position);
                    }

                    //刷新界面
                    HomeEvent event = new HomeEvent(HomeEvent.EVENT_UPDATE, position + "");
                    EventBus.getDefault().post(event);
                    notifyDataSetChanged();
                }
            };

            tv_Ah.setOnClickListener(onClickListener);
            tv_Bh.setOnClickListener(onClickListener);
            tv_Ch.setOnClickListener(onClickListener);
            tv_Dh.setOnClickListener(onClickListener);

            ll_A.setOnClickListener(onClickListener);
            ll_B.setOnClickListener(onClickListener);
            ll_C.setOnClickListener(onClickListener);
            ll_D.setOnClickListener(onClickListener);

            btn_submit.setOnClickListener(onClickListener2);

            View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int position = mViewPager2.getCurrentItem();
                    final Map<String, Object> map = mData.get(position);
                    final String id = map.get("id").toString();
                    String text = MainDiscuss.getDiscussData(id);

                    //Toast.makeText(MyApp.getAppContext(), "hello", Toast.LENGTH_LONG).show();
                    final EditText inputServer = new EditText(mInflater.getContext());
                    inputServer.setMaxLines(30);
                    inputServer.setMinLines(3);
                    inputServer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
                    inputServer.setText(text);

                    AlertDialog.Builder builder = new AlertDialog.Builder(mInflater.getContext(),R.style.AlertDialogCustom);
                    builder.setTitle("添加个人笔记")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(inputServer)
                            .setNegativeButton("Cancel", null);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String discuss = inputServer.getText().toString();
                            MainDiscuss.sendDiscussToServer(id, discuss);
                            MainDiscuss.updateDiscussData(id, discuss);
                            tv_discuss.setText(Html.fromHtml("<strong>笔记：</strong>\u00A0" + discuss));

                            map.put("discuss", discuss);
                            mData.set(position, map);
                        }
                    });
                    builder.show();
                    return false;
                }
            };
            tv_discuss.setOnLongClickListener(onLongClickListener);
        }
    }

    private static String formatStringSet(Set<String> selArr) {
        StringBuilder str = new StringBuilder();
        for (String selected : selArr) {
            str.append(selected);
        }
        return str.toString();
    }

    private void playSound(int done) {
        HomeEvent eventNext = new HomeEvent(HomeEvent.EVENT_PLAY_SOUND, done + "");
        EventBus.getDefault().post(eventNext);
    }
}

