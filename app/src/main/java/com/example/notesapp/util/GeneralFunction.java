package com.example.notesapp.util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GeneralFunction {
    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return sdf.format(date);
    }
    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
}
