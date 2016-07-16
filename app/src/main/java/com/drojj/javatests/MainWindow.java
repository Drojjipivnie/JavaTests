package com.drojj.javatests;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.events.OpenTestEvent;
import com.drojj.javatests.events.OpenTestPRogressEvent;
import com.drojj.javatests.events.OpenTestResultsEvent;
import com.drojj.javatests.fragments.tests.TestProgressFragment;
import com.drojj.javatests.model.question.Question;
import com.drojj.javatests.auth.LoginActivity;
import com.drojj.javatests.fragments.tests.QuestionsFragment;
import com.drojj.javatests.fragments.tests.TestResultsFragment;
import com.drojj.javatests.fragments.tests.TestsListFragment;
import com.drojj.javatests.utils.ClearingManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainWindow extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar_main) Toolbar mToolbar;

    private boolean mDoubleBackToExitPressedOnce = false;

    private static int currentNavigationItem = R.id.navigation_tests_item;

    //private Snackbar mSnackbarConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        //mSnackbarConnection = Snackbar.make(findViewById(android.R.id.content),"Устанавливается соединение...",Snackbar.LENGTH_INDEFINITE);

        initToolbar();
        initDrawerLayout();
        initNavigationView();

        if (savedInstanceState == null) {
            TestsListFragment fragment = new TestsListFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container_main, fragment).addToBackStack("TestsList").commit();
        }
    }

    /*private void initConnectionListener() {

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        ValueEventListener listenerConnection = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected && NetworkHelper.isNetworkAvailable(MainWindow.this)){
                    if(mSnackbarConnection.isShown()){
                        mSnackbarConnection.dismiss();
                    }
                }else{
                    if(!mSnackbarConnection.isShown()){
                        mSnackbarConnection.show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        connectedRef.addValueEventListener(listenerConnection);
    }*/

    private void initToolbar() {
        setSupportActionBar(mToolbar);
    }

    private void initDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(currentNavigationItem);

        View v = navigationView.getHeaderView(0);
        TextView name = ButterKnife.findById(v,R.id.navigation_header_name);
        TextView email = ButterKnife.findById(v,R.id.navigation_header_email);

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
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else if (getFragmentManager().getBackStackEntryCount() == 1) {
            if (mDoubleBackToExitPressedOnce) {
                goHome();
                return;
            }

            mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();

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
                    replaceFragment("TestsList", new TestsListFragment());
                    mToolbar.setTitle("Тесты");
                    return true;

                case R.id.navigation_questions_item:
                    clearFragmentsBackStack();
                    currentNavigationItem = item.getItemId();
                    replaceFragment("Questions", new Fragment());
                    mToolbar.setTitle("Вопросы");
                    return true;

                case R.id.navigation_about_item:
                    Toast.makeText(MainWindow.this, "О программе", Toast.LENGTH_SHORT).show();
                    return false;

                case R.id.navigation_like_item:
                    Toast.makeText(MainWindow.this, "Оценить", Toast.LENGTH_SHORT).show();
                    return false;
                case R.id.navigation_log_out:
                    FirebaseAuth.getInstance().signOut();
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

    private void clearFragmentsBackStack() {
        FragmentManager fm = getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void replaceFragment(String name, Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, fragment)
                //.setCustomAnimations()
                .addToBackStack(name)
                .commit();

    }

    private void startLoginActivity(){
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
                        MainWindow.this.finish();
                    }
                });
        builder.show();
    }

    private void goHome(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initConnectionListener();
    }

    @Subscribe
    public void onOpenTestEvent(OpenTestEvent event){
        Fragment fragment = QuestionsFragment.getInstance(event.test);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_out_right, R.animator.slide_in_right)
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack("Test_questions")
                .commit();
    }

    @Subscribe
    public void onOpenTestResultsEvent(OpenTestResultsEvent event){
        getFragmentManager().popBackStack();
        ArrayList<Question> questions = event.questions;
        Fragment fragment = TestResultsFragment.getInstance(questions);
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_out_right, R.animator.slide_in_right)
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack("Test_results")
                .commit();
    }

    @Subscribe
    public void onOpenTestPRogressEvent(OpenTestPRogressEvent event){
        Fragment fragment = TestProgressFragment.newInstance(event.test);
        getFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left, R.animator.slide_out_right, R.animator.slide_in_right)
                .replace(R.id.fragment_container_main, fragment)
                .addToBackStack("Test_progress")
                .commit();
    }
}
