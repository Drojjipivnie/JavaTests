package com.drojj.javatests.utils.analytics;


import android.os.Build;

import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;

public class YandexAnalyticsLogger implements Logger {

    private static YandexAnalyticsLogger mInstance;

    public static YandexAnalyticsLogger getInstance() {
        if (mInstance == null) {
            mInstance = new YandexAnalyticsLogger();
        }
        return mInstance;
    }

    private YandexAnalyticsLogger() {

    }

    @Override
    public void logSignUp(String userId) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("user_id", userId);
        eventAttributes.put("device", Build.DEVICE);
        eventAttributes.put("id", Build.ID);
        eventAttributes.put("manufacturer", Build.MANUFACTURER);
        eventAttributes.put("model", Build.MODEL);
        eventAttributes.put("product", Build.PRODUCT);
        eventAttributes.put("time", Build.TIME);
        YandexMetrica.reportEvent("User_sign_up", eventAttributes);
    }

    @Override
    public void logLogIn(String userId) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("user_id", userId);
        YandexMetrica.reportEvent("User_log_in", eventAttributes);
    }

    @Override
    public void logClickLogOut(String userId) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("user_id", userId);
        YandexMetrica.reportEvent("user_try_log_out", eventAttributes);
    }

    @Override
    public void logLogOut(String userId) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("user_id", userId);
        YandexMetrica.reportEvent("user_log_out", eventAttributes);
    }

    @Override
    public void logFailLogIn(String cause) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("cause", cause);
        YandexMetrica.reportEvent("user_fail_log_in", eventAttributes);
    }

    @Override
    public void logFailSignUp(String cause) {
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("cause", cause);
        YandexMetrica.reportEvent("user_fail_sign_up", eventAttributes);
    }
}
