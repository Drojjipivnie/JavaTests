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

    private static final String USER_REGISTERED = "Пользователь зарегестрировался";
    private static final String USER_LOGED_IN = "Пользователь вошел в профиль";
    private static final String USER_LOGED_OUT = "Пользователь вышел из профиля";
    private static final String USER_TRY_LOG_OUT = "Пользователь нажал кнопку выйти";
    private static final String USER_FAIL_LOG_IN = "Пользователь не смог войти в профиль";
    private static final String USER_FAIL_SIGN_UP = "Пользователь не смог зарегестрироваться";
    private static final String USER_START_TEST = "Пользователь начал тест";
    private static final String USER_QUIT_TEST = "Пользователь вышел из теста";
    private static final String USER_SHOW_TEST_STATISTICS = "Показать статистику теста";
    private static final String USER_TEST_ENDED = "Тест закончен";
    private static final String USER_SHOW_TEST_RESULTS = "Показать результаты теста";
    private static final String USER_NO_SHOW_TEST_RESULTS = "Не показывать результаты теста";
    private static final String USER_GOOD_FEEDBACK = "Пользователь решил оставить хороший отзыв";
    private static final String USER_BAD_FEEDBACK = "Пользователь решил оставить плохой отзыв";
    private static final String USER_CLEAR_STATISTICS = "Очистить статистику теста";

    private static final String USER_EMAIL = "Email пользователя";
    private static final String TIME = "Время";
    private static final String CAUSE = "Причина";
    private static final String TEST_TITLE = "Название теста";
    private static final String QUESTION_COUNT_WHEN_USER_LEFT = "Индекс вопроса, на котором пользователь вышел";
    private static final String RIGHT_ANSWERS = "Кол-во правильных ответов";
    private static final String AVERAGE_SCORE = "Средний балл";
    private static final String MAX_SCORE = "Максимальный балл";


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
        eventAttributes.put(TIME, Build.TIME);
        YandexMetrica.reportEvent(USER_REGISTERED, eventAttributes);
    }

    public void logIn() {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(USER_LOGED_IN, map);
    }

    public void logOut(String userId) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(USER_LOGED_OUT, map);
    }

    public void clickLogOut(String userId) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(USER_TRY_LOG_OUT, map);
    }

    public void failLogIn(String cause, String email) {
        Map<String, Object> map = getStandardMap();
        map.put(CAUSE, cause);
        map.put(USER_EMAIL, email);
        YandexMetrica.reportEvent(USER_FAIL_LOG_IN, map);
    }

    public void failSignUp(String cause, String email) {
        Map<String, Object> map = getStandardMap();
        map.put(CAUSE, cause);
        map.put(USER_EMAIL, email);
        YandexMetrica.reportEvent(USER_FAIL_SIGN_UP, map);
    }

    public void startTest(Test test) {
        Map<String, Object> map = getStandardMap();
        map.put(TEST_TITLE, test.name);
        YandexMetrica.reportEvent(USER_START_TEST, map);
    }

    public void quitTest(Test mTest, int mQuestionCounter) {
        Map<String, Object> map = getStandardMap();
        map.put(TEST_TITLE, mTest.name);
        map.put(QUESTION_COUNT_WHEN_USER_LEFT, mQuestionCounter);
        YandexMetrica.reportEvent(USER_QUIT_TEST, map);
    }

    public void showProgress(Test mTest) {
        Map<String, Object> map = getStandardMap();
        map.put(TEST_TITLE, mTest.name);
        YandexMetrica.reportEvent(USER_SHOW_TEST_STATISTICS, map);
    }

    public void testEnded(Test mTest, int rightAnswers) {
        Map<String, Object> map = getStandardMap();
        map.put(TEST_TITLE, mTest.name);
        map.put(RIGHT_ANSWERS, rightAnswers);
        YandexMetrica.reportEvent(USER_TEST_ENDED, map);
    }

    public void clickTestResults(boolean isShow) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(isShow ? USER_SHOW_TEST_RESULTS : USER_NO_SHOW_TEST_RESULTS, map);
    }

    public void clickFeedBackButton(boolean isGood) {
        Map<String, Object> map = getStandardMap();
        YandexMetrica.reportEvent(isGood ? USER_GOOD_FEEDBACK : USER_BAD_FEEDBACK, map);
    }

    public void clickClearStatistics(String testName, int maxScore, String averageInt) {
        Map<String, Object> map = getStandardMap();
        map.put(MAX_SCORE, maxScore);
        map.put(AVERAGE_SCORE, averageInt);
        map.put(TEST_TITLE, testName);
        YandexMetrica.reportEvent(USER_CLEAR_STATISTICS, map);
    }

    public void nonGoogleApi() {
        YandexMetrica.reportEvent("Запуск без Google API");
    }


    private Map<String, Object> getStandardMap() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            Map<String, Object> eventAttributes = new HashMap<>();
            eventAttributes.put(USER_EMAIL, mUser.getEmail());
            return eventAttributes;
        } else {
            return new HashMap<>();

        }
    }
}
