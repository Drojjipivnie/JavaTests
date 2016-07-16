package com.drojj.javatests.utils;

import android.content.Context;

import java.io.File;

public class ClearingManager {

    private Context mCtx;

    public ClearingManager(Context context) {
        mCtx = context;
    }

    public void clear(){
        clearAppCache();
    }

    private void clearAppCache() {
        try {
            File dir = mCtx.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {}
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
