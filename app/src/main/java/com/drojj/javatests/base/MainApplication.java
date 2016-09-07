package com.drojj.javatests.base;

import android.app.Application;

import com.drojj.javatests.R;
import com.google.firebase.database.FirebaseDatabase;
import com.yandex.metrica.YandexMetrica;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    public static final String YANDEX_API_KEY = "dc89ca5b-4d02-4ce5-80c4-e0df1e9286e3";

    @Override
    public void onCreate() {
        super.onCreate();

        YandexMetrica.activate(getApplicationContext(), YANDEX_API_KEY);
        YandexMetrica.enableActivityAutoTracking(this);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
