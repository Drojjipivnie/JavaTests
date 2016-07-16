package com.drojj.javatests.utils;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsLogger {

    private static FirebaseAnalyticsLogger mInstance;

    private Context mCtx;

    private FirebaseAnalytics mAnalytics;

    public static FirebaseAnalyticsLogger getInstance(Context ctx){
        if(mInstance == null) {
            mInstance = new FirebaseAnalyticsLogger(ctx);
        }
        return mInstance;
    }

    private FirebaseAnalyticsLogger(Context ctx){
        mAnalytics = FirebaseAnalytics.getInstance(ctx);
        mCtx = ctx;
    }

    public void logSignUp(String userId){
        Bundle bundle = new Bundle();
        bundle.putString("user_id",userId);
        bundle.putString("device", Build.DEVICE);
        bundle.putString("id", Build.ID);
        bundle.putString("manufacturer", Build.MANUFACTURER);
        bundle.putString("model", Build.MODEL);
        bundle.putString("product",Build.PRODUCT);
        bundle.putLong("time", Build.TIME);
        mAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP,bundle);
    }

    public void logLogIn(String userId){
        Bundle bundle = new Bundle();
        bundle.putString("user_id",userId);
        mAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,bundle);
    }

    public void logClickLogOut(String userId){
        Bundle bundle = new Bundle();
        bundle.putString("user_id",userId);
        mAnalytics.logEvent("user_try_log_out",bundle);
    }

    public void logLogOut(String userId){
        Bundle bundle = new Bundle();
        bundle.putString("user_id",userId);
        mAnalytics.logEvent("user_log_out",bundle);
    }

    public void logFailLogIn(String cause){
        Bundle bundle = new Bundle();
        bundle.putString("cause",cause);
        mAnalytics.logEvent("user_fail_log_in",bundle);
    }

    public void logFailSignUp(String cause){
        Bundle bundle = new Bundle();
        bundle.putString("cause",cause);
        mAnalytics.logEvent("user_fail_sign_up",bundle);
    }

}
