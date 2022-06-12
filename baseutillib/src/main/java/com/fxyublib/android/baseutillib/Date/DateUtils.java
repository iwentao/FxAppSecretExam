package com.fxyublib.android.baseutillib.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(long date) {
        Date d = new Date(date);
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format2.format(d);
        return str;
    }
    public static String formatDate(long date, String formatstring) {
        Date d = new Date(date);
        DateFormat format2 = new SimpleDateFormat(formatstring);
        String str = format2.format(d);
        return str;
    }
    //
    public static String getDateNowString(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getDateNowString(String formatstring){
        SimpleDateFormat formatter = new SimpleDateFormat(formatstring);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    //
    public static String getDateNowString2(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sDateFormat.format(new java.util.Date());
    }
    //
    public static String getDateNowString3(){
        Calendar c = Calendar.getInstance();
        int timeYear = c.get(Calendar.YEAR);
        int timeMonth = c.get(Calendar.MONTH)+1;
        int timeDay = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int secord = c.get(Calendar.SECOND);
        return timeYear + "/" + timeMonth +"/" + timeDay + "/ "
                + hour + ":" + minute + ":" + secord;
    }

    public static String timestampToDateStr(Long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

}
