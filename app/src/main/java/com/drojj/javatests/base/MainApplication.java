package com.drojj.javatests.base;

import android.app.Application;

import com.drojj.javatests.R;
import com.yandex.metrica.YandexMetrica;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainApplication extends Application {

    public static final String YANDEX_API_KEY = "bc3b1318-1421-4151-a0b4-1f03bf8a3660";

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
