package com.drojj.javatests.utils.analytics;


import android.os.Build;

import com.drojj.javatests.model.Test;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;

public class YandexAnalyticsLogger {

    private static YandexAnalyticsLogger mInstance;

    public static YandexAnalyticsLogger getInstance() {
        if (mInstance == null) {
            mInstance = new YandexAnalyticsLogger();
        }
        return mInstance;
    }

    private YandexAnalyticsLogger() {

    }

    public void signUp() {
        Map<String, Object> eventAttributes = getStandardMap();
        eventAttributes.put("device", Build.DEVICE);
        eventAttributes.put("build_id", Build.ID);
        eventAttributes.put("manufacturer", Build.MANUFACTURER);
        eventAttributes.put("model", Build.MODEL);
        eventAttributes.put("product", Build.PRODUCT);
        eventAttributes.put("time", Build.TIME);
        YandexMetrica.reportEvent("User_sign_up", eventAttributes);
    }

    public void logIn() {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent("User_log_in", map);
    }

    public void logOut(String userId) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent("user_log_out", map);
    }

    public void clickLogOut(String userId) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent("user_try_log_out", map);
    }

    public void failLogIn(String cause, String email) {
        Map<String, Object> map = getStandardMap();
        map.put("cause", cause);
        map.put("email", email);
        YandexMetrica.reportEvent("user_fail_log_in", map);
    }

    public void failSignUp(String cause, String email) {
        Map<String, Object> map = getStandardMap();
        map.put("cause", cause);
        map.put("email", email);
        YandexMetrica.reportEvent("user_fail_sign_up", map);
    }

    public void startTest(Test test) {
        Map<String, Object> map = getStandardMap();
        map.put("test", test.name);
        YandexMetrica.reportEvent("user_start_test", map);
    }

    public void quitTest(Test mTest, int mQuestionCounter) {
        Map<String, Object> map = getStandardMap();
        map.put("test", mTest.name);
        map.put("questionCount_when_user_quit", mQuestionCounter);
        YandexMetrica.reportEvent("user_quit_test", map);
    }

    public void showProgress(Test mTest) {
        Map<String, Object> map = getStandardMap();
        map.put("test", mTest.name);
        YandexMetrica.reportEvent("user_show_test_progress", map);
    }

    public void testEnded(Test mTest, int rightAnswers) {
        Map<String, Object> map = getStandardMap();
        map.put("test", mTest.name);
        map.put("right_answers", rightAnswers);
        YandexMetrica.reportEvent("user_ended_test", map);
    }

    public void clickTestResults(boolean isShow) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(isShow ? "user_show_test_results" : "user_do_not_show_test_results", map);
    }

    public void clickFeedBackButton(boolean isGood) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(isGood ? "user_clicked_good_feed" : "user_clicked_bad_feed", map);
    }

    private Map<String, Object> getStandardMap() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            Map<String, Object> eventAttributes = new HashMap<>();
            eventAttributes.put("user_id", mUser.getUid());
            eventAttributes.put("user_email", mUser.getEmail());
            return eventAttributes;
        } else {
            return new HashMap<>();

        }
    }


}
