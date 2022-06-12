package com.fxyublib.android.fxappsecretexam.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.fxyublib.android.baseutillib.Database.AssetsDBHelper;
import com.fxyublib.android.baseutillib.View.ToastUtils;
import com.fxyublib.android.fxappsecretexam.MainDialog;
import com.fxyublib.android.fxappsecretexam.home.QuestionActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel {
    private static String TAG = "MainViewModel";
    private static List<HashMap<String, Object>> mExamHistoryList;
    private static List<HashMap<String, Object>> mPracticeHistoryList;

    public static int CHAPTER_NORMAL = 0;
    public static int CHAPTER_ERROR = 10;
    public static int CHAPTER_COLLECT = 11;
    public static int CHAPTER_EXAM = 12;
    public static int CHAPTER_ORDER_PRACTICE = 13;
    public static int CHAPTER_RANDOM_PRACTICE = 14;

    public static void clearAllTempData() {
        //清空数据中的做题记录和收藏标志
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        db.execSQL("update questions set done=0, collect=0 ");

        //清空手机存储的临时数据
    }

    public static void gotoActivity(Activity activity, String moduleName) {
        gotoActivity(activity, moduleName, 0, 0);
    }

    public static void gotoActivity(Context context, String moduleName, int ctype, int qtype) {
        Activity activity = (Activity)context;
        gotoActivity(activity, moduleName, ctype, qtype);
    }

    public static void gotoActivity(Activity activity, String moduleName, int ctype, int qtype) {
        if (moduleName.equals("积分榜")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("资料库")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("题库查询")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("练考记录")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("保密新闻")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("专项练习")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("章节练习")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.contains("专项练习 -")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.contains("章节练习 -")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("我的错题")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);

        }
        else if (moduleName.equals("我的收藏")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);

        }
        else if (moduleName.equals("模拟考试 - 提示")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("模拟考试")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
        else if (moduleName.equals("顺序练习 - 提示")) {
            MainDialog.showDialog_PrePractice(activity);
        }
        else if (moduleName.equals("顺序练习")) {
            ToastUtils.showToast(activity, "欢迎使用该功能！", Toast.LENGTH_SHORT);

            Intent intent = new Intent(activity, QuestionActivity.class);
            intent.putExtra("title", "顺序练习(全部)");
            intent.putExtra("ctype", CHAPTER_ORDER_PRACTICE);
            activity.startActivity(intent);
        }
        else if (moduleName.equals("随机练习")) {
            ToastUtils.showToast(activity, "试用版程序没有该功能！", Toast.LENGTH_SHORT);
        }
    }

    @SuppressLint("Range")
    private static int queryDatabase_StatsCollect(SQLiteDatabase db) {
        int ret = 0;
        Cursor c;
        c = db.rawQuery("SELECT count(*) as num FROM questions where collect=1", null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            ret = c.getInt(c.getColumnIndex("num"));
        }
        c.close();
        return ret;
    }

    @SuppressLint("Range")
    private static int queryDatabase_StatsAllNum(SQLiteDatabase db) {
        int ret = 0;
        Cursor c;
        c = db.rawQuery("SELECT count(*) as num FROM questions", null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            ret = c.getInt(c.getColumnIndex("num"));
        }
        c.close();
        return ret;
    }

    @SuppressLint("Range")
    private static int[] queryDatabase_StatsDone(SQLiteDatabase db) {
        int[] arr = new int[4]; //0, 1, 2
        Cursor c;
        c = db.rawQuery("SELECT done, count(*) as num FROM questions group by done", null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            do {
                int done = c.getInt(c.getColumnIndex("done"));
                int num = c.getInt(c.getColumnIndex("num"));
                arr[done] = num;
            } while (c.moveToNext());
        }
        c.close();
        return arr;
    }

    public static Map<String, Object> getData_Stats() {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        int allnum = 0;//queryDatabase_StatsAllNum(db);
        int[] arr = queryDatabase_StatsDone(db);
        for (int value : arr) {
            allnum += value;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("count", allnum);
        map.put("undone", arr[0]);
        map.put("done", allnum - arr[0]);
        map.put("rightdone", arr[1]);
        map.put("wrongdone", arr[2]);
        Log.d(TAG, map.toString());
        return map;
    }

    public static int getData_StatsCollect() {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        return queryDatabase_StatsCollect(db);
    }

    public static List<HashMap<String, Object>> getData_ExamHistory() {
        return mExamHistoryList;
    }

    public static List<HashMap<String, Object>> getData_PracticeHistory() {
        return mPracticeHistoryList;
    }

    public static void loadData_History() {
    }

    public static void addExamHistory(String score, String useTime, String startTime, int done, int sum) {
    }

    public static void addPracticeHistory(String score, String useTime, String startTime, int done, int sum) {
    }
}
