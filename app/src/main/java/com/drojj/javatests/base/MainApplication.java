package com.drojj.javatests.base;

import android.app.Application;

import com.drojj.javatests.R;
import com.google.firebase.database.FirebaseDatabase;
import com.yandex.metrica.YandexMetrica;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        YandexMetrica.activate(getApplicationContext(), "949c67d2-e7fd-4b6e-9904-70c75a5bb76a");
        YandexMetrica.enableActivityAutoTracking(this);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
