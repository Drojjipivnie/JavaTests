package com.drojj.javatests.utils.analytics;

import android.os.Bundle;

import com.drojj.javatests.base.MainApplication;

public class FirebaseAnalyticsLogger {

    public static void startTest(String userId, int testId) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", userId);
        bundle.putInt("test_id", testId);
        MainApplication.getAnalytics().logEvent("start_test", bundle);
    }

    public static void showProgress(String uid, int id) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id",uid);
        bundle.putInt("test_id", id);
        MainApplication.getAnalytics().logEvent("show_progress", bundle);
    }

    public static void showResults(String uid, int testId) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id",uid);
        bundle.putInt("test_id", testId);
        MainApplication.getAnalytics().logEvent("show_results", bundle);
    }

    public static void chooseCategory(String uid, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id",uid);
        bundle.putString("category_title", title);
        MainApplication.getAnalytics().logEvent("choose_category", bundle);
    }

    public static void chooseQuestionFromCategory(String uid, String categoryTitle, String questionTitle) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id",uid);
        bundle.putString("category_title", categoryTitle);
        bundle.putString("question_title", questionTitle);
        MainApplication.getAnalytics().logEvent("choose_question", bundle);
    }
}
