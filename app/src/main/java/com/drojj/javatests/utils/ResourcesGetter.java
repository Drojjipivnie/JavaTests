package com.drojj.javatests.utils;

import android.content.Context;

public class ResourcesGetter {
    public static int getDrawableId(Context ctx,String picName) {
        return ctx.getResources().getIdentifier(picName, "drawable", ctx.getApplicationContext().getPackageName());
    }
}
