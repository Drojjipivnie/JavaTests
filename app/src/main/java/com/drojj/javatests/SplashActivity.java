package com.drojj.javatests;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.drojj.javatests.auth.LoginActivity;
import com.drojj.javatests.database.TestDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1000;

    private static final String APP_PREFERENCES = "app_settings";
    private static final String APP_PREFERENCES_DB_SETTING = "database_version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkDataBase();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        startActivity(MainWindow.class);
                    } else {
                        startActivity(LoginActivity.class);
                    }
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void startActivity(Class<?> activity) {
        Intent intent = new Intent(SplashActivity.this, activity);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void checkDataBase() {
        final SharedPreferences preferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int databaseVersion = preferences.getInt(APP_PREFERENCES_DB_SETTING, 0);

        if (databaseVersion == 0 || databaseVersion != TestDatabase.VERSION) {
            TestDatabase.copyDataBase(this, new TestDatabase.DataBaseInitCallback() {
                @Override
                public void onSuccess() {
                    Log.d("SplashLog", "DataBase is installed");
                    preferences.edit().putInt(APP_PREFERENCES_DB_SETTING, TestDatabase.VERSION).apply();
                }

                @Override
                public void onError(IOException e) {
                    Log.d("SplashLog", "Database not installed");
                    Toast.makeText(SplashActivity.this, "Error: " + e.getMessage() + ".Database not installed.", Toast.LENGTH_SHORT).show();
                    SplashActivity.this.finish();
                }
            });
        }
    }
}
