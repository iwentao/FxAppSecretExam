package com.fxyublib.android.fxappsecretexam.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.fxyublib.android.baseutillib.Database.AssetsDBHelper;
import com.fxyublib.android.fxappsecretexam.R;
import com.fxyublib.android.fxappsecretexam.data.MainViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

public class HomeViewModel extends ViewModel {
    private static String TAG = "HomeViewModel";

    private static int mDoneSum = 0;
    private static int mDoneErr = 0;
    private static long mDoneTime = 0;

    HomeViewModel() {
    }

    public static int getOnceDoneSum() {
        return mDoneSum;
    }

    public static int getOnceDoneErr() {
        return mDoneErr;
    }

    public static long getOnceDoneTime() {
        return mDoneTime;
    }

    static void clearOnceDone() {
        mDoneSum = 0;
        mDoneErr = 0;
        mDoneTime = System.currentTimeMillis();
    }

    static void setOnceDone(int done) {
        mDoneSum++;
        if(done == 2) mDoneErr++;
    }

    static void setCollect(int id, int value) {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");

        db.execSQL("update questions set collect="+value+" where id=" + id);
    }

    static void setDone(int id, int value) {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");

        db.execSQL("update questions set done="+value+" where id=" + id);
    }

    static void clearWhereDone(int ctype, int qtype, int otype) {
        String strWhere = getData_WhereSQL(ctype, qtype, otype);

        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        db.execSQL("update questions set done=0" + strWhere);
    }

    public static void clearAllDone() {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");

        db.execSQL("update questions set done=0");
    }

    private static int[] queryDatabase_ChapterModule(SQLiteDatabase db) {
        int[] arr = new int[4];
        Cursor c;
        c = db.rawQuery("SELECT ctype, count(*) as num FROM questions group by ctype", null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            do {
                int ctype = c.getInt(c.getColumnIndex("ctype"));
                int num = c.getInt(c.getColumnIndex("num"));
                arr[ctype] = num;

            } while (c.moveToNext());
        }
        c.close();
        return arr;
    }

    public static List<Map<String, Object>> getData_Chapter() {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        int[] arr = queryDatabase_ChapterModule(db);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("image", R.drawable.ic_1);
        map.put("name", "xxx基础知识");
        map.put("count", arr[1] + "题");
        map.put("otype", "顺序练习");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("image", R.drawable.ic_2);
        map.put("name", "xxx资格认定办法");
        map.put("count", arr[2]+ "题");
        map.put("otype", "顺序练习");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("image", R.drawable.ic_3);
        map.put("name", "xxx资格标准");
        map.put("count", arr[3]+ "题");
        map.put("otype", "顺序练习");
        list.add(map);

        return list;
    }

    private static int[] queryDatabase_SpecialModule(SQLiteDatabase db) {
        int[] arr = new int[10];
        Cursor c;
        c = db.rawQuery("SELECT qtype, count(*) as num FROM questions group by qtype", null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            do {
                int qtype = c.getInt(c.getColumnIndex("qtype"));
                int num = c.getInt(c.getColumnIndex("num"));
                arr[qtype] = num;

            } while (c.moveToNext());
        }
        c.close();
        return arr;
    }

    public static List<Map<String, Object>> getData_Special() {
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        SQLiteDatabase db = manager.getDatabase("secret.db");
        int[] arr = queryDatabase_SpecialModule(db);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "单选题");
        map.put("count", arr[1] + "题");
        map.put("qtype", 1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "多选题");
        map.put("count", arr[2]+ "题");
        map.put("qtype", 2);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "判断题");
        map.put("count", arr[3]+ "题");
        map.put("qtype", 3);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "填空题");
        map.put("count", arr[4]+ "题");
        map.put("qtype", 4);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "简答题");
        map.put("count", arr[5]+ "题");
        map.put("qtype", 5);
        list.add(map);

        return list;
    }

    static String getData_WhereSQL(int ctype, int qtype, int otype) {
        String strWhere = "";
        if(ctype == 0) {
            if(qtype == 0) { //顺序练习 和 随机练习
                strWhere = "";
            }
            else { //专项练习
                strWhere = " where qtype=" + qtype;
            }
        }
        else if (ctype == 10) { //我的错题
            strWhere = " where done=2";
        }
        else if (ctype == 11) { //我的收藏
            strWhere = " where collect=1";
        }
        else if (ctype == 12) { //未做题
            strWhere = " where done=0";
        }
        else { //章节练习
            strWhere = " where ctype=" + ctype;
        }
        return strWhere;
    }

    private static List<Map<String, Object>> getData_SQLCursor(String strSQL) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        AssetsDBHelper manager = AssetsDBHelper.getManager();
        if(manager == null) return list;
        SQLiteDatabase db = manager.getDatabase("secret.db");
        if(db == null) return list;

        Cursor c;
        c = db.rawQuery(strSQL, null);

        if (c.getCount() > 0 && c.moveToFirst()) {
            do {
                Map<String, Object> map=new HashMap<String, Object>();

                int id = c.getInt(c.getColumnIndex("id"));
                int no = c.getInt(c.getColumnIndex("no"));
                int ctype1 = c.getInt(c.getColumnIndex("ctype"));
                int qtype1 = c.getInt(c.getColumnIndex("qtype"));
                String name = c.getString(c.getColumnIndex("name"));
                String answer = c.getString(c.getColumnIndex("answer"));

                int done = c.getInt(c.getColumnIndex("done"));
                int collect = c.getInt(c.getColumnIndex("collect"));
                String analysis = c.getString(c.getColumnIndex("analysis"));

                map.put("id", id);
                map.put("no", no);
                map.put("name", name);
                map.put("ctype", ctype1);
                map.put("qtype", qtype1);
                map.put("answer", answer);
                map.put("done", done);
                map.put("collect", collect);
                map.put("analysis", analysis);
                map.put("discuss", "");

                Set<Integer> setlArr = new TreeSet<>();
                map.put("selected", setlArr);
                map.put("submit", 0);

                if (qtype1 == 1 || qtype1 == 2) {
                    String A = c.getString(c.getColumnIndex("A")).replace("A.", "");
                    String B = c.getString(c.getColumnIndex("B")).replace("B.", "");
                    String C = c.getString(c.getColumnIndex("C")).replace("C.", "");
                    String D = c.getString(c.getColumnIndex("D")).replace("D.", "");
                    map.put("A", A);
                    map.put("B", B);
                    map.put("C", C);
                    map.put("D", D);
                }
                else if(qtype1 == 3) {
                    map.put("A", "正确");
                    map.put("B", "错误");
                    map.put("C", null);
                    map.put("D", null);

                    if(answer.equals("1")) {
                        map.put("answer", "A");
                    }
                    else {
                        map.put("answer", "B");
                    }
                }
                else if(qtype1 == 4) {
                    map.put("answer", answer.replace(",", "，"));
                    map.put("name", name.replace("_____", "____"));
                }
                else if(qtype1 == 5) {
                    answer = answer.replace("#答：", "<br>");
                    if(answer.startsWith("<br>\n#"))
                        answer = answer.replace("<br>\n#", "<br>");
                    answer = answer.replace("#", "<br><br>");
                    map.put("answer", answer);
                }

                list.add(map);
            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    //单选30（30*2=60），多选5（5*3=15），判断25(1*25=25）
    //单选15（15*2=30），多选5（5*3=15），判断10（1*10=10），填空15（2*15=30），简答3（5*3=15）
    //
    static List<Map<String, Object>> getData_ExamQuestion(boolean isNormal) {
        List<Map<String, Object>> data = new ArrayList<>();

        if(isNormal) {
            //单选题1
            String strSQL = "SELECT * FROM questions where qtype=1 ORDER BY RANDOM() limit 15";
            data.addAll(getData_SQLCursor(strSQL));

            //多选题2
            strSQL = "SELECT * FROM questions where qtype=2 ORDER BY RANDOM() limit 5";
            data.addAll(getData_SQLCursor(strSQL));

            //判断题3
            strSQL = "SELECT * FROM questions where qtype=3 ORDER BY RANDOM() limit 10";
            data.addAll(getData_SQLCursor(strSQL));

            //填空题4
            strSQL = "SELECT * FROM questions where qtype=4 ORDER BY RANDOM() limit 15";
            data.addAll(getData_SQLCursor(strSQL));

            //简答题5
            strSQL = "SELECT * FROM questions where qtype=5 ORDER BY RANDOM() limit 3";
            data.addAll(getData_SQLCursor(strSQL));
        }
        else {
            //单选题1
            String strSQL = "SELECT * FROM questions where qtype=1 ORDER BY RANDOM() limit 30";
            data.addAll(getData_SQLCursor(strSQL));

            //多选题2
            strSQL = "SELECT * FROM questions where qtype=2 ORDER BY RANDOM() limit 5";
            data.addAll(getData_SQLCursor(strSQL));

            //判断题3
            strSQL = "SELECT * FROM questions where qtype=3 ORDER BY RANDOM() limit 25";
            data.addAll(getData_SQLCursor(strSQL));
        }

        data = initExamData(data);
        return data;
    }

    static List<Map<String, Object>> getData_RandomPracticeQuestion() {
        List<Map<String, Object>> data = new ArrayList<>();

        //单选题1
        String strSQL = "SELECT * FROM questions where qtype=1 ORDER BY RANDOM() ";
        data.addAll(getData_SQLCursor(strSQL));

        //多选题2
        strSQL = "SELECT * FROM questions where qtype=2 ORDER BY RANDOM() ";
        data.addAll(getData_SQLCursor(strSQL));

        //判断题3
        strSQL = "SELECT * FROM questions where qtype=3 ORDER BY RANDOM() ";
        data.addAll(getData_SQLCursor(strSQL));

        //填空题4
        strSQL = "SELECT * FROM questions where qtype=4 ORDER BY RANDOM()";
        data.addAll(getData_SQLCursor(strSQL));

        //简答题5
        strSQL = "SELECT * FROM questions where qtype=5 ORDER BY RANDOM()";
        data.addAll(getData_SQLCursor(strSQL));

        //data = initExamData(data);
        return data;
    }

    public static List<Map<String, Object>> getData_PracticeQuestion(int ctype, int qtype, int otype) {
        String strSQL = "SELECT * FROM questions ";
        String strWhere = getData_WhereSQL(ctype, qtype, otype);
        strSQL += strWhere;

        Log.d("HomeViewModel", strSQL);
        return getData_SQLCursor(strSQL);
    }

    public static List<Map<String, Object>> getData_Question(String where) {
        String strSQL = "SELECT * FROM questions " + where;
        return getData_SQLCursor(strSQL);
    }

    static void delayNextQuestion(final int position) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                HomeEvent eventNext = new HomeEvent(HomeEvent.EVENT_NEXT, position + "");
                EventBus.getDefault().post(eventNext);
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 500);//3秒后执行TimeTask的run方法
    }

    static List<Map<String, Object>> initExamData(List<Map<String, Object>> listData) {
        for (int i = 0; i < listData.size(); i++) {
            Map<String, Object> item = listData.get(i);
            int idone = (int) item.get("done");
            int qtype = (int) item.get("qtype");
            item.put("done", 0);
            listData.set(i, item);
        }

        return listData;
    }

    static Map<String, Object> computeExamResult(List<Map<String, Object>> listData) {
        Map<String, Object> map = new HashMap<String, Object>();
        int done = 0; //1+2
        int undone = 0; //0
        int rightdone = 0; //1
        int wrongdone = 0; //2
        int count = listData.size(); //all
        int score = 0;

        for (int i = 0; i < listData.size(); i++) {
            Map<String, Object> item = listData.get(i);
            int idone = (int) item.get("done");
            int qtype = (int) item.get("qtype");

            if(idone == 0) undone++;
            else  if(idone == 1) rightdone++;
            else  if(idone == 2) wrongdone++;


            if(idone == 1) {
                if(qtype == 1) score += 2;
                else if(qtype == 2) score += 3;
                else if(qtype == 3) score += 1;
                else if(qtype == 4) score += 2;
                else if(qtype == 5) score += 5;
            }
        }

        map.put("count", count);
        map.put("undone", undone);
        map.put("done", count - undone);
        map.put("rightdone", rightdone);
        map.put("wrongdone", wrongdone);
        map.put("score", score);
        Log.d(TAG, map.toString());
        return map;
    }
}