package com.fxyublib.android.fxappsecretexam.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.fxyublib.android.baseutillib.Activity.BaseActivity;
import com.fxyublib.android.baseutillib.Date.DateUtils;
import com.fxyublib.android.baseutillib.System.DeviceId;
import com.fxyublib.android.baseutillib.View.ToastUtils;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.baseutillib.Dialog.MessageDialog;
import com.fxyublib.android.fxappsecretexam.data.MainDiscuss;
import com.fxyublib.android.fxappsecretexam.data.MainHttp;
import com.fxyublib.android.fxappsecretexam.data.MainViewModel;
import com.fxyublib.android.fxappsecretexam.data.PrefContants;
import com.fxyublib.android.fxappsecretexam.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import static com.fxyublib.android.baseutillib.Date.RelativeDateFormat.longToString;

public class QuestionActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "QuestionActivity";
    private ViewPager2 mViewPager2;
    private ViewPagerAdapter mViewPagerAdapter2;
    private MessageDialog mMyDialog = null;

    private List<Map<String, Object>> mListData;
    String m_title = "";
    int m_ctype = 0;
    int m_qtype = 0;
    int m_examSumMinute = 60;

    //
    ImageAdapter mImageAdapter;
    GridView mImageGrid;
    SlidingUpPanelLayout mSlidingLayout;
    private long baseTimer;
    private String mTimeUsed;
    private Timer mTimerExam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        EventBus.getDefault().register(this);

        initData();
        initView();
        initCenterView();
        initBottomView();

    }

    void initView() {
        //返回按钮
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        // 标题栏文字
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(m_title);
        setTitle(m_title);

        //标题栏的设置按钮
        TextView tv_settings = findViewById(R.id.tv_settings);
        tv_settings.setVisibility(View.VISIBLE);
        tv_settings.setOnClickListener(this);

        if(m_ctype == MainViewModel.CHAPTER_EXAM) {
            final TextView tv_time = findViewById(R.id.tv_time);
            tv_time.setVisibility(View.VISIBLE);

            LinearLayout clear_root = findViewById(R.id.clear_root);
            clear_root.setVisibility(View.GONE);

            final TextView practice_submit = findViewById(R.id.practice_submit);
            practice_submit.setVisibility(View.VISIBLE);
            practice_submit.setOnClickListener(this);

            this.baseTimer = SystemClock.elapsedRealtime();
            @SuppressLint("HandlerLeak") final Handler startTimehandler = new Handler(){
                public void handleMessage(@NonNull android.os.Message msg) {
                    tv_time.setText((String) msg.obj);
                }
            };

            mTimerExam = new Timer("开机计时器");
            mTimerExam.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    int time = (int)((SystemClock.elapsedRealtime() - baseTimer) / 1000);
                    int timeDiff = m_examSumMinute*60 - time;
                    if(timeDiff <= 0) {
                        mTimerExam.cancel();
                        return;
                    }

                    String mm = new DecimalFormat("00").format(timeDiff % 3600 / 60);
                    String ss = new DecimalFormat("00").format(timeDiff % 60);
                    String timeFormat = new String(mm + ":" + ss);

                    mm = new DecimalFormat("00").format(time % 3600 / 60);
                    ss = new DecimalFormat("00").format(time % 60);
                    mTimeUsed  = new String(mm + ":" + ss);

                    Message msg = new Message();
                    msg.obj = timeFormat;
                    startTimehandler.sendMessage(msg);
                }

            }, 0, 1000L);

        }
    }

    void initCenterView() {
        mViewPager2 = findViewById(R.id.viewpager2);
        mViewPagerAdapter2 = new ViewPagerAdapter(this, mListData, mViewPager2, m_ctype == MainViewModel.CHAPTER_EXAM);
        mViewPager2.setAdapter(mViewPagerAdapter2);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //Log.e(TAG, "onPageScrolled: "+position+"--->"+positionOffset+"--->"+positionOffsetPixels );
                //tv_page.setText((position + 1) +"/" + mListData.size());

                updateBottomView(position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //Log.e(TAG, "onPageSelected: "+position );
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                //Log.e(TAG, "onPageScrollStateChanged: "+state );
            }
        });
    }

    void initBottomView() {
        //清空记录按钮
        TextView clear_tv = findViewById(R.id.clear_tv);
        clear_tv.setOnClickListener(this);

        //收藏题目按钮
        CheckBox practice_answer_card_super_btn = findViewById(R.id.practice_answer_card_super_btn);
        practice_answer_card_super_btn.setOnClickListener(this);

        //底部折叠控件
        mSlidingLayout = findViewById(R.id.sliding_layout);
        mImageAdapter = new ImageAdapter(this, mListData);
        mImageGrid = findViewById(R.id.answer_sheet_gv);
        mImageGrid.setAdapter(mImageAdapter);
        mImageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(QuestionActivity.this, "你点击了~" + (position + 1) + "~项", Toast.LENGTH_SHORT).show();
                mViewPager2.setCurrentItem(position);
                mImageAdapter.setCurrentIndex(position);
                mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        //
        updateBottomView(-1);

        //
        if(m_ctype == MainViewModel.CHAPTER_ORDER_PRACTICE) {
            int pos = MyApp.getInt(PrefContants.PREF_PRACTICE_LASTPOS, 0);
            mViewPager2.setCurrentItem(pos);
            mImageAdapter.setCurrentIndex(pos);
        }
    }

    private void updateBottomView(int position) {
        TextView practice_indicator_text = findViewById(R.id.practice_indicator_text);
        practice_indicator_text.setText( (mViewPager2.getCurrentItem() + 1 )+ "/" + mListData.size());

        int rightCount = 0;
        int wrongCount = 0;
        for (int i = 0; i < mListData.size(); i++) {
            Map<String, Object> map = mListData.get(i);
            int done = (int) map.get("done");
            if (done == 1) rightCount++;
            if (done == 2) wrongCount++;
        }

        TextView practice_right_count_text = findViewById(R.id.practice_right_count_text);
        practice_right_count_text.setText(rightCount + "");

        TextView practice_error_count_text = findViewById(R.id.practice_error_count_text);
        practice_error_count_text.setText(wrongCount + "");

        if(position >= 0 && position < mListData.size()) {
            mImageAdapter.setCurrentIndex(position);
        }
        mImageAdapter.notifyDataSetChanged();

        if (position >= 0 && position < mListData.size()) {
            Map<String, Object> map = mListData.get(position);
            int collect = (int) map.get("collect");

            CheckBox practice_answer_card_super_btn = findViewById(R.id.practice_answer_card_super_btn);
            practice_answer_card_super_btn.setChecked(collect == 1);
        }
    }

    void initData() {
        Intent intent = getIntent();
        m_title = intent.getStringExtra("title");
        m_ctype = intent.getIntExtra("ctype", 0);
        m_qtype = intent.getIntExtra("qtype", 0);
        HomeViewModel.clearOnceDone();
        Log.d(TAG, "initData: " + m_title + "," + m_ctype + "," + m_qtype);

        if(m_ctype == MainViewModel.CHAPTER_EXAM) {
            mListData = HomeViewModel.getData_ExamQuestion(m_qtype == 0);
            if(m_qtype == 1) {
                m_examSumMinute = 15;
            }
        }
        else if(m_ctype == MainViewModel.CHAPTER_ORDER_PRACTICE) {
            mListData = HomeViewModel.getData_PracticeQuestion(0, 0, 0);
        }
        else if(m_ctype == MainViewModel.CHAPTER_RANDOM_PRACTICE) {
            mListData = HomeViewModel.getData_RandomPracticeQuestion();
        }
        else {
            mListData = HomeViewModel.getData_PracticeQuestion(m_ctype, m_qtype, 0);
        }
        mListData = MainDiscuss.addDiscussData(mListData);

        //播放声音
        if(m_ctype != MainViewModel.CHAPTER_ERROR && m_ctype != MainViewModel.CHAPTER_COLLECT) {
            HomeEvent eventNext = new HomeEvent(HomeEvent.EVENT_PLAY_SOUND, "0");
            EventBus.getDefault().post(eventNext);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(mTimerExam != null) mTimerExam.cancel();
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else {
            int sumOnce = HomeViewModel.getOnceDoneSum();
            if(sumOnce > 0) {

                if(m_ctype != MainViewModel.CHAPTER_EXAM) {


                    Log.d(TAG, "onBackPressed: send");
                    //sendPracticeOperatorToServer();

                    if(mTimerExam != null)
                        mTimerExam.cancel();
                    super.onBackPressed();
                }
                else {
                    showDialog_BackPressed();
                }
            }
            else {
                if(mTimerExam != null)
                    mTimerExam.cancel();
                super.onBackPressed();
            }
        }
    }

    void sendPracticeOperatorToServer() {
        int sum = mListData.size();
        String time = mTimeUsed;
        int ctype = m_ctype;

        int sumOnce = HomeViewModel.getOnceDoneSum();
        int rightOnce = sumOnce - HomeViewModel.getOnceDoneErr();
        int rate = HomeViewModel.getOnceDoneSum();
        if(rate > 0) rate = (int)(rightOnce*100/sumOnce);
        int dtTime = (int)(System.currentTimeMillis() - HomeViewModel.getOnceDoneTime())/1000;

        if(sumOnce > 0 && m_ctype != MainViewModel.CHAPTER_EXAM) {

            String costTime = longToString(dtTime, "mm:ss");
            String startTime = DateUtils.getDateNowString("MM-dd HH:mm");
            MainViewModel.addPracticeHistory(rate + "%", costTime
                    , startTime, sumOnce, sum);

            MainHttp.sendOperatorToServer(DeviceId.getAndroidID(MyApp.getAppContext())
                    , startTime, costTime
                    , m_ctype+ "", sumOnce+ "", rightOnce + "");

            finish();
        }
    }

    void showDialog_BottomSetting() {
        if(mMyDialog != null) mMyDialog.dismiss();

        View view = getLayoutInflater().inflate(R.layout.dialog_change_practce_style, null);
        mMyDialog = new MessageDialog(this, 0, 0, view, R.style.DialogTheme, Gravity.BOTTOM);
        mMyDialog.setCancelable(true);
        mMyDialog.show();

        CheckBox auto_paging_btn = mMyDialog.findViewById(R.id.auto_paging_btn);
        auto_paging_btn.setOnClickListener(this);
        auto_paging_btn.setChecked(MyApp.getBool(PrefContants.PREF_PRACTICE_AUTONEXT, true));

        if (m_ctype == MainViewModel.CHAPTER_EXAM) {
            TextView paging_title = mMyDialog.findViewById(R.id.paging_title);
            paging_title.setVisibility(View.GONE);
            auto_paging_btn.setVisibility(View.GONE);
        }

        SeekBar mSeekBarTest = mMyDialog.findViewById(R.id.seek_bar);
        final TextView tv_size_text = mMyDialog.findViewById(R.id.tv_size_text);
        float text_size = MyApp.getFloat(PrefContants.PREF_PRACTICE_TEXTSIZE, 18.0f);
        if(text_size == 16.0f) {
            tv_size_text.setText("小号");
            mSeekBarTest.setProgress(0);
        }
        else if(text_size == 18.0f) {
            tv_size_text.setText("标准");
            mSeekBarTest.setProgress(20);
        }
        else if(text_size == 19.5f) {
            tv_size_text.setText("中号");
            mSeekBarTest.setProgress(40);
        }
        else if(text_size == 21.0f) {
            tv_size_text.setText("大号");
            mSeekBarTest.setProgress(60);
        }
        else if(text_size == 22.5f) {
            tv_size_text.setText("偏大号");
            mSeekBarTest.setProgress(80);
        }
        else if(text_size == 24.0f) {
            tv_size_text.setText("特大号");
            mSeekBarTest.setProgress(100);
        }
        else {
            tv_size_text.setText("标准");
        }

        mSeekBarTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //SeekBar 滑动时的回调函数，其中 fromUser 为 true 时是手动调节

                float text_size = 0;
                if(progress < 20-5) {
                    tv_size_text.setText("小号");
                    text_size = 16.0f;
                }
                else if(progress < 40-5) {
                    tv_size_text.setText("标准");
                    text_size = 18.0f;
                }
                else if(progress < 60-5) {
                    tv_size_text.setText("中号");
                    text_size = 19.5f;
                }
                else if(progress < 80-5) {
                    tv_size_text.setText("大号");
                    text_size = 21.0f;
                }
                else if(progress < 100-5) {
                    tv_size_text.setText("偏大号");
                    text_size = 22.5f;
                }
                else {
                    tv_size_text.setText("特大号");
                    text_size = 24.0f;
                }

                MyApp.setFloat(PrefContants.PREF_PRACTICE_TEXTSIZE, text_size);
                mViewPagerAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //SeekBar 开始滑动的的回调函数
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SeekBar 停止滑动的回调函数
                int progress = seekBar.getProgress();
                if(progress < 20-5)
                    progress = 0;
                else if(progress < 40-5)
                    progress = 20;
                else if(progress < 60-5)
                    progress = 40;
                else if(progress < 80-5)
                    progress = 60;
                else if(progress < 100-5)
                    progress = 80;
                else
                    progress = 100;
                seekBar.setProgress(progress);

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back) {
            //finish();
            this.onBackPressed();
        }
        else if(v.getId() == R.id.cancelTv || v.getId() == R.id.closeIv) {
            mMyDialog.dismiss();
        }
        else if(v.getId() == R.id.auto_paging_btn) {
            CheckBox auto_paging_btn = (CheckBox) v;
            MyApp.setBool(PrefContants.PREF_PRACTICE_AUTONEXT, auto_paging_btn.isChecked());
        }
        else if(v.getId() == R.id.tv_settings) {
            showDialog_BottomSetting();
        }
        else if(v.getId() == R.id.clear_tv) {
            showDialog_ClearAllAnswer();
        }
        else if(v.getId() == R.id.practice_submit) {
            showDialog_Submit();
        }
        else if(v.getId() == R.id.practice_answer_card_super_btn) {
            CheckBox practice_answer_card_super_btn = findViewById(R.id.practice_answer_card_super_btn);
            boolean isChecked = practice_answer_card_super_btn.isChecked();

            int position = mViewPager2.getCurrentItem();
            Map<String, Object> map = mListData.get(position);
            int id = (int) map.get("id");
            map.put("collect", isChecked ? 1: 0);
            mListData.set(position, map);
            HomeViewModel.setCollect(id, isChecked ? 1: 0);
        }
    }

    void showDialog_Submit() {
        if(mMyDialog != null) mMyDialog.dismiss();

        final Map<String, Object> map = HomeViewModel.computeExamResult(mListData);
        final int undone = (int) map.get("undone");
        final int count = (int) map.get("count");
        map.put("costTime", mTimeUsed);
        map.put("sumTime", m_examSumMinute);
        map.put("ctype", m_ctype);

        View view = getLayoutInflater().inflate(R.layout.dialog_common_dialog, null);
        TextView dialog_message = view.findViewById(R.id.dialog_message);
        dialog_message.setText("您还有"+undone+"道题未做(总共"+count+"道题)，\n确认交卷吗？");

        mMyDialog = new MessageDialog(this, 0, 0, view, R.style.DialogTheme, Gravity.CENTER);
        mMyDialog.setCancelable(true);
        mMyDialog.show();

        //取消按钮的事件
        TextView dialog_btn_left = mMyDialog.findViewById(R.id.dialog_btn_left);
        dialog_btn_left .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMyDialog.dismiss();
            }
        });

        //确认按钮的事件
        TextView dialog_btn_right = mMyDialog.findViewById(R.id.dialog_btn_right);
        dialog_btn_right .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMyDialog.dismiss();
                QuestionActivity.this.finish();

                int score = (int) map.get("score");
                int rightdone = (int) map.get("rightdone");
                int done = (int) map.get("done");
                int ctype = (int) map.get("ctype");
                int sumTime = (int) map.get("sumTime");
                String costTime = (String) map.get("costTime");

                String timeNow = DateUtils.getDateNowString("MM-dd HH:mm");
                MainViewModel.addExamHistory(score + ""
                        , costTime
                        , timeNow
                        , done, count);

                MainHttp.sendOperatorToServer(DeviceId.getAndroidID(QuestionActivity.this)
                        , timeNow, costTime, ctype+ "", done+ "", rightdone + "");
            }
        });
    }

    void showDialog_BackPressed() {
        if(mMyDialog != null) mMyDialog.dismiss();

        Map<String, Object> map = HomeViewModel.computeExamResult(mListData);
        int done = (int) map.get("done");
        int isum = (int) map.get("count");
        final int score = (int) map.get("score");

        View view = getLayoutInflater().inflate(R.layout.dialog_common_dialog, null);
        TextView dialog_message = view.findViewById(R.id.dialog_message);
        dialog_message.setText("您已经回答了"+done+"道题，确定要放弃本次考试吗？");

        mMyDialog = new MessageDialog(this, 0, 0, view, R.style.DialogTheme, Gravity.CENTER);
        mMyDialog.setCancelable(true);
        mMyDialog.show();

        //取消按钮的事件
        TextView dialog_btn_left = mMyDialog.findViewById(R.id.dialog_btn_left);
        dialog_btn_left.setText("继续考试");
        dialog_btn_left .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMyDialog.dismiss();
            }
        });

        //确认按钮的事件
        TextView dialog_btn_right = mMyDialog.findViewById(R.id.dialog_btn_right);
        dialog_btn_right.setText("确认放弃");
        dialog_btn_right .setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMyDialog.dismiss();
                if(mTimerExam != null) mTimerExam.cancel();

                QuestionActivity.this.finish();
                QuestionActivity.super.onBackPressed();
            }
        });
    }

    void showDialog_ClearAllAnswer() {
        if(mMyDialog != null) mMyDialog.dismiss();

        View view = getLayoutInflater().inflate(R.layout.dialog_anwser_card_clear, null);
        mMyDialog = new MessageDialog(this, 0, 0, view, R.style.DialogTheme, Gravity.CENTER);
        mMyDialog.setCancelable(true);
        mMyDialog.show();

        TextView cancelTv = mMyDialog.findViewById(R.id.cancelTv);
        cancelTv.setOnClickListener(this);

        ImageView closeIv = mMyDialog.findViewById(R.id.closeIv);
        closeIv.setOnClickListener(this);

        TextView confirmTv = mMyDialog.findViewById(R.id.confirmTv);
        confirmTv .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyDialog.dismiss();

                //清空数据库的做题记录
                HomeViewModel.clearWhereDone(m_ctype, m_qtype, 0);

                //清空内存中的做题记录
                for (int i = 0; i < mListData.size(); i++) {
                    Map<String, Object> map = mListData.get(i);
                    map.put("done", 0);
                    Set<Integer> setlArr = new TreeSet<>();
                    map.put("selected", setlArr);
                    map.put("submit", 0);
                    mListData.set(i, map);
                }

                mViewPagerAdapter2.notifyDataSetChanged();
                updateBottomView(-1);
                Toast.makeText(QuestionActivity.this, "清空当前的做题记录！", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.d(TAG, "onKeyDown: " + keyCode);
//
//        //  KeyEvent.KEYCODE_HOME  KeyEvent.KEYCODE_APP_SWITCH
//        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
//            return true;
//        else
//            return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onUserLeaveHint() {
        Log.d(TAG, "onUserLeaveHint: send");
        sendPracticeOperatorToServer();

        super.onUserLeaveHint();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HomeEvent event) {
        Log.e(TAG, "onEventMainThread: " + event.what);

        if (event.what == HomeEvent.EVENT_UPDATE) {
            updateBottomView(Integer.parseInt(event.data));

            if(m_ctype == MainViewModel.CHAPTER_ORDER_PRACTICE) {
                MyApp.setInt(PrefContants.PREF_PRACTICE_LASTPOS, mViewPager2.getCurrentItem());
            }
        }
        else if (event.what == HomeEvent.EVENT_NEXT) {
            int position = Integer.parseInt(event.data);
            position++;
            if (position >= mListData.size()) {
                return;
            }

            //更新上视图
            mViewPager2.setCurrentItem(position);
            //更新下视图
            updateBottomView(position);
        }
    }


}
