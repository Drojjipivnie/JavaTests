package com.drojj.javatests.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {

    public static int getDisplayWidth(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return displayMetrics.widthPixels;
    }

}
