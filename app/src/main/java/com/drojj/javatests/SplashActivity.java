package com.drojj.javatests;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.drojj.javatests.auth.LoginActivity;
import com.drojj.javatests.database.tests.TestDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    public static final String APP_PREFERENCES = "app_settings";
    private static final String APP_PREFERENCES_DB_SETTING = "isDataBaseInstalled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        checkDataBase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        startApp();
                    } else {
                        startLogin();
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void startApp() {
        Intent intent = new Intent(SplashActivity.this, MainWindow.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void startLogin(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void checkDataBase() {
        final SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (!preferences.getBoolean(APP_PREFERENCES_DB_SETTING, false)) {
            TestDatabase.copyDataBase(this, new TestDatabase.DataBaseInitCallback() {
                @Override
                public void onSuccess() {
                    Log.d("SplashLog","DataBase is installed");
                    preferences.edit().putBoolean(APP_PREFERENCES_DB_SETTING, true).apply();
                }

                @Override
                public void onError(IOException e) {
                    Log.d("SplashLog","Database not installed");
                    e.printStackTrace();
                }
            });
        }
    }
}
