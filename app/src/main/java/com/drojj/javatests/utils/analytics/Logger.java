package com.drojj.javatests.utils.analytics;

public interface Logger {

    void logSignUp(String userId);

    void logLogIn(String userId);

    void logClickLogOut(String userId);

    void logLogOut(String userId);

    void logFailLogIn(String cause);

    void logFailSignUp(String cause);

}
