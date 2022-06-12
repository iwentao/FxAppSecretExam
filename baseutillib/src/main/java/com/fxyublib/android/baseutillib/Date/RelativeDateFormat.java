package com.fxyublib.android.baseutillib.Date;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeDateFormat {

    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        Date date = format.parse("2013-11-11 18:35:35");
        System.out.println(format(date));
    }

    private static int countString(String str, String s) {
        String str1 = str.replaceAll(s, "");
        int len1 = str.length(),len2 = str1.length(),len3 = s.length();
        return (len1 - len2) / len3;
    }

    public static String longToString(long secord) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new Date(secord * 1000);
        return sdf.format(date);
    }

    public static String longToString(long secord, String format) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf= new SimpleDateFormat(format);
        java.util.Date date = new Date(secord * 1000);
        return sdf.format(date);
    }

    public static String format(long secord) {
        java.util.Date date = new Date(secord * 1000);
        return format(date);
    }

    public static String format(String strDate) {
        int nSign = countString(strDate, ":");
        String strDateFormat = "";
        if(nSign == 0)
            strDateFormat = "yyyy-MM-dd";
        else if(nSign == 1)
            strDateFormat = "yyyy-MM-dd HH:m";
        else if(nSign == 2)
            strDateFormat = "yyyy-MM-dd HH:m:s";

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat(strDateFormat);
        Date date = null;
        try {
            date = format.parse(strDate);
            if(date != null) {
                strDate = RelativeDateFormat.format(date);
                return format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    public static long getDiffHour(Date date) {
        long delta = new Date().getTime() - date.getTime();
        return delta / ONE_HOUR;
    }

    public static long getDiffDay(Date date) {
        long delta = new Date().getTime() - date.getTime();
        return delta / (24L*ONE_HOUR);
    }

    public static String formatDay(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 24L * ONE_HOUR) {
            return "今天";
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }

}
