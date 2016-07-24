package com.campustribune.event.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by aditi on 16/07/16.
 */
public class Utility {

    public Bitmap scaleImage(int width, int height, String imagePath){

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int imgWidth = bmOptions.outWidth;
        int imgHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(imgWidth/width, imgHeight/height);

        bmOptions.inJustDecodeBounds=false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath, bmOptions);
    }

    public static String getFormattedDate(String start, String end){
        StringBuilder st = new StringBuilder();
        DateTime dtStart = new DateTime(DateTimeZone.UTC);
        dtStart = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(start);

        int mon = dtStart.getMonthOfYear();
        String month = getMonth(mon);
        int day = dtStart.getDayOfMonth();
        st.append(month+" "+day);

        DateTime dtEnd = new DateTime(DateTimeZone.UTC);
        dtEnd = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(end);

        if(!(dtEnd.getMonthOfYear()==mon && dtEnd.getDayOfMonth()==day)){
            mon = dtEnd.getMonthOfYear();
            month = getMonth(mon);
            day = dtEnd.getDayOfMonth();
            st.append("-"+month+" "+day);
        }
        return st.toString();
    }

    public static String getMonth(int mon){
        String month = new String();
        switch (mon){
            case 1:
                month = "JAN";
                break;
            case 2:
                month = "FEB";
                break;
            case 3:
                month = "MAR";
                break;
            case 4:
                month = "APR";
                break;
            case 5:
                month = "MAY";
                break;
            case 6:
                month = "JUN";
                break;
            case 7:
                month = "JUL";
                break;
            case 8:
                month = "AUG";
                break;
            case 9:
                month = "SEP";
                break;
            case 10:
                month = "OCT";
                break;
            case 11:
                month = "NOV";
                break;
            case 12:
                month = "DEC";
                break;
        }
        return month;
    }

    public static String getFormattedTime(String start, String end){
        DateTime dt = new DateTime(DateTimeZone.UTC);
        dt = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(start);

        StringBuilder st = new StringBuilder();
        int hours = dt.getHourOfDay();
        int minutes = dt.getMinuteOfHour();
        if(hours<10 && minutes<10)
            st.append("0"+hours+":0"+minutes);
        else if(hours<10)
            st.append("0"+hours+":"+minutes);
        else if(minutes<10)
            st.append(hours+":0"+minutes);
        else
            st.append(hours+":"+minutes);
        st.append(" - ");

        dt = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(end);
        hours = dt.getHourOfDay();
        minutes = dt.getMinuteOfHour();
        if(hours<10 && minutes<10)
            st.append("0"+hours+":0"+minutes);
        else if(hours<10)
            st.append("0"+hours+":"+minutes);
        else if(minutes<10)
            st.append(hours+":0"+minutes);
        else
            st.append(hours+":"+minutes);

        return st.toString();
    }

    public static long getTimeInMiliSeconds(String date){
        DateTime dt = new DateTime(DateTimeZone.UTC);
        dt = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC).parseDateTime(date);
        return dt.getMillis();
    }
}
