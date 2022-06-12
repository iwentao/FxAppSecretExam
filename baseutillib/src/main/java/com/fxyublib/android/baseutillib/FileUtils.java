package com.fxyublib.android.baseutillib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileUtils {
    private static final String TAG = "FileUtils";

    public static String getFromRaw(Context context, int resid){
        try {
            InputStreamReader inputReader = new InputStreamReader( context.getResources().openRawResource(resid));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 读取assets本地json
     * @param context
     * @param fileName
     * @return
     */

    public static String getFromAssets(Context context, String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( context.getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            StringBuilder Result= new StringBuilder();
            while((line = bufReader.readLine()) != null)
                Result.append(line);
            return Result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /** * 从Assets中读取图片 */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName){
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is=am.open(fileName);
            image= BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static void copyAssetsToFiles(Context context) {
        String[] files;
        try {
            //注意：在assets文件夹下影藏了三个带文件的文件夹，分别是images、sounds、webkit
            //返回数组files里面会包含这三个文件夹
            files = context.getResources().getAssets().list("");
        } catch (IOException e1) {
            return;
        }

        String File_PATH = context.getFilesDir().getPath() + "/";
        File mWorkingPath = new File(File_PATH);
        if (!mWorkingPath.exists()) {
            mWorkingPath.mkdirs();
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String fileName = files[i];
                File outFile = new File(mWorkingPath, fileName);
                if (outFile.exists()) {
                    continue;
                }

                InputStream in = context.getAssets().open(fileName);
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /** * 将图片存到本地 */
    public static Uri saveBitmap(Bitmap bm, String picName) {
        try {
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath()+"/renji/"+picName+".jpg";
            File f = new File(dir);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Uri uri = Uri.fromFile(f);
            return uri;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();    }
        return null;
    }

    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    public static String saveTxt(String dir, String fileName, String content) {
        FileOutputStream fos = null;
        String strFullFilePath = "";

        try {
            /* 判断sd的外部设置状态是否可以读写 */
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.e(TAG, Environment.getExternalStorageDirectory().getPath());
                File fileDir = new File(Environment.getExternalStorageDirectory().getPath(), dir);
                if (!fileDir.exists()) {
                    try {
                        fileDir.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                File file = new File(fileDir, fileName + ".txt");
                strFullFilePath = file.getAbsolutePath();

                // 先清空内容再写入
                fos = new FileOutputStream(file);
                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return strFullFilePath;
    }

    public static String readTxt(String dir, String fileName) {
        FileInputStream fis = null;
        String result = null;

        try {
            File fileDir = new File(Environment.getExternalStorageDirectory().getPath(), dir);
            File file = new File(fileDir, fileName + ".txt");
            if (file.exists()) {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                result = new String(buffer, "UTF-8");
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void saveCacheTxt(Context c, String dirName, String fileName, String text) {
        FileOutputStream fos = null;

        try {
            File dir = new File(c.getExternalCacheDir(), dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName + ".txt");

            // 先清空内容再写入
            fos = new FileOutputStream(file);
            byte[] buffer = text.getBytes();
            fos.write(buffer);
            fos.close();

        } catch (Exception ex) {
            ex.printStackTrace();

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String readCacheTxt(Context c, String dirName, String fileName) {
        FileInputStream fis = null;
        String result = null;

        try {
            File dir = new File(c.getExternalCacheDir(), dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName + ".txt");
            if (file.exists()) {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                result = new String(buffer, "UTF-8");
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 在对sd卡进行读写操作之前调用这个方法
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    //保存数据到SD卡文件
    public static void saveJsonDataToSDcard(Context c, String dirName, String fileName, List<HashMap<String,Object>> list){
        boolean isAvailable = false;    //SD是否可读
        FileOutputStream fileOutputStream = null;
        //创建File对象
        //File file = new File(Environment.getExternalStorageDirectory(),fileName);
        File dir = new File(c.getExternalCacheDir(), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName + ".json");

        //将list转成String类型
        List<String> cache = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            // 取出当前的Map，转化为JSONObject
            JSONObject obj = new JSONObject(list.get(i));
            // 转化为字符串并添加进新的List中
            cache.add(obj.toString());
        }
        // 可存储的字符串数据
        String listStr = cache.toString();

        //判断SD卡是否可读写
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            isAvailable = true;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(listStr.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //读取文件内容，并将String转成List<>
    public static List<HashMap<String, Object>> getJsonDataFromSDcard(Context c, String dirName, String fileName){
        //读取文件内容保存到resultStr
        String resultStr = null;
        File dir = new File(c.getExternalCacheDir(), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName + ".json");
        if (!file.exists()) {
            return null;
        }
        Log.d("getJsonDataFromSDcard", file.getAbsolutePath());

        //File file = new File(Environment.getExternalStorageDirectory(),fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] b = new byte[fileInputStream.available()];
            fileInputStream.read(b);
            resultStr = new String(b);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("读文件出错");
        }
        //将读取的String结果转化成List<>
        List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
        try {
            JSONArray jsonArray = new JSONArray(resultStr);
            if (jsonArray.length()>0) {
                for (int i=0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    HashMap<String, Object> map = new HashMap<String, Object>();

                    /*for(int j = 0; j < 5; j++){
                        String key = "item" + j;
                        Object item = jsonObject.optString(key);
                        map.put(key, item);
                    }*/
                    Iterator<String> it = jsonObject.keys();
                    while(it.hasNext()){
                        // 获得key
                        String key = it.next();
                        String value = jsonObject.optString(key);
                        map.put(key, value);
                    }

                    tempList.add(map);
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
            System.out.println("转化list出错");
        }
        return tempList;
    }
}
