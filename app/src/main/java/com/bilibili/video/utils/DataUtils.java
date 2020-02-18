package com.bilibili.video.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    public static String getCurrentTime(){
        SimpleDateFormat format
                = new SimpleDateFormat("HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = format.format(curDate);
        return str;
    }
}
