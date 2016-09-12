package com.drojj.javatests;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.base.MainApplication;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.fragments.questions.InterviewQuestionCategories;
import com.drojj.javatests.auth.LoginActivity;
import com.drojj.javatests.fragments.tests.TestListFragment;
import com.drojj.javatests.info.AboutProgram;
import com.drojj.javatests.info.FeedBack;
import com.drojj.javatests.utils.ClearingManager;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.metrica.YandexMetrica;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainWindow extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @BindView(R.id.navigation_view_main)
    NavigationView mNavigation;

    @BindView(R.id.toolbar_progress_bar)
    ProgressBar mProgressBar;

    private boolean mDoubleBackToExitPressedOnce = false;

    private final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    private final YandexAnalyticsLogger mLogger = YandexAnalyticsLogger.getInstance();

    private int currentNavigationItem = R.id.navigation_tests_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView view = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        view.loadAd(adRequest);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        initToolbar();
        initDrawerLayout();
        initNavigationView();

        if (savedInstanceState == null) {
            TestListFragment fragment = new TestListFragment();
            replaceFragment("test_list", fragment);
        } else {
            currentNavigationItem = savedInstanceState.getInt("currentTab");
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    private void initDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView() {
        mNavigation.setNavigationItemSelectedListener(this);
        mNavigation.setCheckedItem(currentNavigationItem);

        View v = mNavigation.getHeaderView(0);
        TextView name = ButterKnife.findById(v, R.id.navigation_header_name);
        TextView email = ButterKnife.findById(v, R.id.navigation_header_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        //Todo: Need to realize normal back method
        Log.d("Log", String.valueOf(getFragmentManager().getBackStackEntryCount()));
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (getFragmentManager().getBackStackEntryCount() == 2 && currentNavigationItem == R.id.navigation_questions_item) {
            getFragmentManager().popBackStack();
            currentNavigationItem = R.id.navigation_tests_item;
            mNavigation.setCheckedItem(currentNavigationItem);
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
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
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawers();
        if (currentNavigationItem != item.getItemId()) {

            switch (item.getItemId()) {
                case R.id.navigation_tests_item:
                    clearFragmentsBackStack();
                    currentNavigationItem = item.getItemId();
                    return true;
                case R.id.navigation_questions_item:
                    clearFragmentsBackStack();
                    currentNavigationItem = item.getItemId();
                    replaceFragment("question_list", InterviewQuestionCategories.newInstance());
                    return true;
                case R.id.navigation_about_item:
                    Intent intent = new Intent(this, AboutProgram.class);
                    startActivity(intent);
                    return false;
                case R.id.navigation_like_item:
                    Intent inten = new Intent(this, FeedBack.class);
                    startActivity(inten);
                    return false;
                case R.id.navigation_log_out:
                    mLogger.clickLogOut(mUser.getUid());
                    startLoginActivity();
                    return false;
                default:
                    Toast.makeText(MainWindow.this, "default", Toast.LENGTH_SHORT).show();
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentTab", currentNavigationItem);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void clearFragmentsBackStack() {
        getFragmentManager().popBackStackImmediate("test_list", 0);
    }

    private void replaceFragment(String name, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_right_to_left, R.animator.slide_out_from_right_to_left, R.animator.slide_in_from_left_to_right, R.animator.slide_out_from_left_to_right)
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack(name)
                .commit();

    }

    private void startLoginActivity() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение")
                .setMessage("Все сохраненные данные будут удалены. Вы уверены?")
                .setCancelable(true)
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClearingManager manager = new ClearingManager(MainWindow.this);
                        manager.clear();

                        Intent intent = new Intent(MainWindow.this, LoginActivity.class);
                        startActivity(intent);
                        FirebaseAuth.getInstance().signOut();
                        mLogger.logOut(mUser.getUid());
                        currentNavigationItem = R.id.navigation_tests_item;
                        MainWindow.this.finish();
                    }
                });
        builder.show();
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

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }
}
