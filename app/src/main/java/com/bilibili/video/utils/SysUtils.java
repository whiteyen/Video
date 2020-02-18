package com.bilibili.video.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class SysUtils {
    // get light
    public static int getBrightness(Context context){
        return Settings.System.getInt(context.getContentResolver(),"screen_brightness",-1);
    }
    // set light
    public static void setBrightness(Context context,int param){
        Settings.System.putInt(context.getContentResolver(),"screen_brightness",param);
    }
    public static int getDefaultBrightness(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("shared_preferences_light",-1);
    }
}
