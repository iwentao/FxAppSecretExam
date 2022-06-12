package com.fxyublib.android.fxappsecretexam.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fxyublib.android.baseutillib.View.CircleProgressBar;
import com.fxyublib.android.baseutillib.View.ToastUtils;
import com.fxyublib.android.fxappsecretexam.MyApp;
import com.fxyublib.android.fxappsecretexam.data.MainViewModel;
import com.fxyublib.android.fxappsecretexam.data.PrefContants;
import com.fxyublib.android.fxappsecretexam.R;
import com.fxyublib.android.fxappsecretexam.utils.OtherUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "HomeFragment";
    private CircleProgressBar mProgressBar_Practice;
    private CircleProgressBar mProgressBar_Exam;
    private Map<String, Object> mHistoryStats = null;

    private AssetManager aManager;
    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();
    private TextView tv_hint = null;

    public HomeFragment() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mSoundPool.release();   //回收SoundPool资源
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        aManager = requireActivity().getAssets();
        try {
            initSP();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        changeHintText();
        HomeEvent eventNext = new HomeEvent(HomeEvent.EVENT_STATS);
        EventBus.getDefault().post(eventNext);
    }

    private void changeHintText() {
        String[] arrHints;
        arrHints = getResources().getStringArray(R.array.hint_array);
        Random rand = new Random();
        if(tv_hint != null) tv_hint.setText(arrHints[rand.nextInt(arrHints.length)]);
    }

    private void initView(View root) {
        mProgressBar_Practice = root.findViewById(R.id.pb_done);
        mProgressBar_Practice.setMainTitle("顺序练习");
        mProgressBar_Practice.setOnClickListener(this);
        mProgressBar_Practice.setTypeface(MyApp.getPrefFont());

        ///////////////////////////////////////
        mProgressBar_Exam = root.findViewById(R.id.pb_average_score);
        mProgressBar_Exam.setMainTitle("模拟考试");
        mProgressBar_Exam.setSubTitle("上次成绩:0分");
        mProgressBar_Exam.setOnClickListener(this);
        mProgressBar_Exam.setTypeface(MyApp.getPrefFont());
        simulateProgress();

        tv_hint = root.findViewById(R.id.tv_hint);
        changeHintText();

        ///////////////////////////////////////
        TextView tv_zxlx = root.findViewById(R.id.tv_zxlx); //专项练习
        tv_zxlx.setOnClickListener(this);
        TextView tv_zjlx = root.findViewById(R.id.tv_zjlx); //章节练习
        tv_zjlx.setOnClickListener(this);

        TextView tv_wdct = root.findViewById(R.id.tv_wdct);  //我的错题
        tv_wdct.setOnClickListener(this);
        TextView tv_tbjq = root.findViewById(R.id.tv_tbjq);  //我的收藏
        tv_tbjq.setOnClickListener(this);

        TextView tv_tkcx = root.findViewById(R.id.tv_tkcx);  //题库搜索
        tv_tkcx.setOnClickListener(this);
        TextView tv_lkjl = root.findViewById(R.id.tv_lkjl);  //练考记录
        tv_lkjl.setOnClickListener(this);

        TextView tv_news = root.findViewById(R.id.tv_news);  //保密新闻
        tv_news.setOnClickListener(this);
        TextView tv_sjlx = root.findViewById(R.id.tv_sjlx);  //随机练习
        tv_sjlx.setOnClickListener(this);

        TextView tv_pdf = root.findViewById(R.id.tv_pdf);    //资料库
        tv_pdf.setOnClickListener(this);

        TextView tv_rank = root.findViewById(R.id.tv_rank);    //积分榜
        tv_rank.setOnClickListener(this);

        TextView tv_zyt = root.findViewById(R.id.tv_zyt);
        TextView tv_sst = root.findViewById(R.id.tv_sst);
        //OtherUtils.setTextViewColorMatrix(tv_wzt, 1, 0.2f);
        //OtherUtils.setTextViewColorMatrix(tv_yct, 1, 0.2f);
        OtherUtils.setTextViewColorMatrix(tv_zyt, 1, 0.2f);
        OtherUtils.setTextViewColorMatrix(tv_sst, 1, 0.2f);
    }

    private void initSP() throws Exception{

    }

    private void simulateProgress() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 101);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                mProgressBar_Practice.setProgress(progress);
                mProgressBar_Exam.setProgress(progress);

            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int progress = 0;

                if(mHistoryStats != null) {
                    int doneCount = (int) mHistoryStats.get("done");
                    int sumCount = (int) mHistoryStats.get("count");
                    progress = (int) doneCount * 100 / sumCount;
                }

                mProgressBar_Practice.setProgress(progress);
                mProgressBar_Exam.setProgress(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setRepeatCount(0);
        animator.setDuration(3000);
        animator.start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.pb_done) {
            MainViewModel.gotoActivity(getActivity(), "顺序练习 - 提示");
        }
        else if(v.getId() == R.id.pb_average_score) {
            MainViewModel.gotoActivity(getActivity(), "模拟考试 - 提示");
        }
        else if (v.getId() == R.id.tv_tkcx) {
            MainViewModel.gotoActivity(getActivity(), "题库查询");
        }
        else if(v.getId() == R.id.tv_zxlx) {
            MainViewModel.gotoActivity(getActivity(), "专项练习");
        }
        else if(v.getId() == R.id.tv_zjlx) {
            MainViewModel.gotoActivity(getActivity(), "章节练习");
        }
        else if(v.getId() == R.id.tv_sjlx) {
            MainViewModel.gotoActivity(getActivity(), "随机练习");
        }
        else if(v.getId() == R.id.tv_wdct) {
            if(mHistoryStats != null) {
                int wrongdone = (int) mHistoryStats.get("wrongdone");
                if(wrongdone == 0) {
                    ToastUtils.showShortToastInCenter(getActivity(), getString(R.string.tip_noerror));
                    return;
                }
            }

            MainViewModel.gotoActivity(getActivity(), "我的错题");
        }
        else if(v.getId() == R.id.tv_tbjq) {
            int collect = MainViewModel.getData_StatsCollect();
            if(collect == 0) {
                ToastUtils.showShortToastInCenter(getActivity(), getString(R.string.tip_nocollect));
                return;
            }

            MainViewModel.gotoActivity(getActivity(), "我的收藏");
        }
        else if(v.getId() == R.id.tv_lkjl) {
            MainViewModel.gotoActivity(getActivity(), "练考记录");
        }
        else if (v.getId() == R.id.tv_news) {
            MainViewModel.gotoActivity(getActivity(), "保密新闻");
        }
        else if (v.getId() == R.id.tv_pdf) {
            MainViewModel.gotoActivity(getActivity(), "资料库");
        }
        else if (v.getId() == R.id.tv_rank) {
            MainViewModel.gotoActivity(getActivity(), "积分榜");
        }
        else {
            if(v instanceof TextView) {
                TextView tv = (TextView) v;
                Toast.makeText(getActivity(), "(" + tv.getText() + ")正在建设中...", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "正在建设中...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(HomeEvent event) {
        Log.e(TAG, "onEventMainThread: " + event.what);

        if (event.what == HomeEvent.EVENT_STATS) {
            //显示顺序练习的汇总结果
            mHistoryStats = MainViewModel.getData_Stats();
            if(mHistoryStats != null && mProgressBar_Practice != null) {
                int doneCount = (int) mHistoryStats.get("done");
                int sumCount = (int) mHistoryStats.get("count");
                mProgressBar_Practice.setSubTitle(doneCount + "/" + sumCount);
            }

            //显示模拟考试的上次分数
            int score = MyApp.getInt(PrefContants.PREF_EXAM_SCORE, 0);
            mProgressBar_Exam.setSubTitle("上次成绩:"+score+"分");

        }
        else if(event.what == HomeEvent.EVENT_PLAY_SOUND) {
            int isSoundOpen = MyApp.getInt("sound", 1);
            int index = Integer.valueOf(event.data);
            if(isSoundOpen == 1) {
                mSoundPool.play(soundID.get(index), 1, 1, 0, 0, 1);
            }
        }
    }

}