package com.drojj.javatests.info;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.drojj.javatests.R;
import com.drojj.javatests.base.MainApplication;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.yandex.metrica.YandexMetrica;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedBack extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @BindView(R.id.feedback_bad_btn)
    Button mBadVote;

    @BindView(R.id.feedback_good_btn)
    Button mGoodVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        ButterKnife.bind(this);

        initToolbar();

        mGoodVote.setOnClickListener(this);
        mBadVote.setOnClickListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.navigation_menu_like));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback_bad_btn:
                YandexAnalyticsLogger.getInstance().clickFeedBackButton(false);
                SendTextDialog dialog = new SendTextDialog();
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.feedback_good_btn:
                YandexAnalyticsLogger.getInstance().clickFeedBackButton(true);
                voteApp();
                FeedBack.this.finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.getReporter(this, MainApplication.YANDEX_API_KEY).onResumeSession();
    }

    @Override
    protected void onPause() {
        YandexMetrica.getReporter(this, MainApplication.YANDEX_API_KEY).onPauseSession();
        super.onPause();
    }

    private void voteApp() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        } else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }
}
