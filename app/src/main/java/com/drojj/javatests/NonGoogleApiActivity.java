package com.drojj.javatests;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.drojj.javatests.base.MainApplication;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.fragments.questions.InterviewQuestionCategories;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yandex.metrica.YandexMetrica;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NonGoogleApiActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_progress_bar)
    ProgressBar mProgressBar;

    private boolean mDoubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_google_api);

        ButterKnife.bind(this);

        AdView view = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        view.loadAd(adRequest);

        EventBus.getDefault().register(this);

        initToolbar();

        if (savedInstanceState == null) {
            replaceFragment("question_list", InterviewQuestionCategories.newInstance());
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("Log", String.valueOf(getFragmentManager().getBackStackEntryCount()));
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStackImmediate();
        } else if (getFragmentManager().getBackStackEntryCount() == 1) {
            if (mDoubleBackToExitPressedOnce) {
                goHome();
                return;
            }

            mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.double_back_to_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDoubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            goHome();
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

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    private void replaceFragment(String name, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_right_to_left, R.animator.slide_out_from_right_to_left, R.animator.slide_in_from_left_to_right, R.animator.slide_out_from_left_to_right)
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack(name)
                .commit();
    }

    private void goHome() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe
    public void onFragmentOpenListener(OpenFragmentEvent event) {
        replaceFragment(event.getType().getTag(), event.getFragment());
    }
}
