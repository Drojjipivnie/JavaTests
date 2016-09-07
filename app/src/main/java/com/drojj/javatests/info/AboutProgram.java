package com.drojj.javatests.info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.drojj.javatests.BuildConfig;
import com.drojj.javatests.R;
import com.yandex.metrica.YandexMetrica;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutProgram extends AppCompatActivity {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @BindView(R.id.about_window_text)
    TextView mAboutText;

    @BindView(R.id.about_window_version)
    TextView mVersion;

    @BindView(R.id.about_window_reffers)
    TextView mReffers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        initToolbar();
        initVersion();
        initAboutText();
        initReffers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.getReporter(this, "949c67d2-e7fd-4b6e-9904-70c75a5bb76a").onResumeSession();
    }

    @Override
    protected void onPause() {
        YandexMetrica.getReporter(this, "949c67d2-e7fd-4b6e-9904-70c75a5bb76a").onPauseSession();
        super.onPause();
    }

    private void initReffers() {
        String reffers = getString(R.string.reffers);
        mReffers.setText(Html.fromHtml(reffers));
        mReffers.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initVersion() {
        mVersion.setText("Версия " + BuildConfig.VERSION_NAME);
    }

    private void initAboutText() {
        String about = getString(R.string.about);
        mAboutText.setText(about);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.navigation_menu_about));
    }
}
