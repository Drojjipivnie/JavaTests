package com.drojj.javatests.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import com.drojj.javatests.MainWindow;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.yandex.metrica.YandexMetrica;

public abstract class AuthBaseActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog mDialog;
    protected final YandexAnalyticsLogger mLogger = YandexAnalyticsLogger.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = setOnAuthStateListener();
    }

    protected abstract FirebaseAuth.AuthStateListener setOnAuthStateListener();

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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

    protected void showProgressDialog(String message){
        if(mDialog==null){
            mDialog = new ProgressDialog(this);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage(message);
        mDialog.show();
    }

    protected void hideProgressDialog(){
        if(mDialog!=null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    protected void startApp() {
        Intent intent = new Intent(AuthBaseActivity.this, MainWindow.class);
        startActivity(intent);
    }

    protected void showError(TextInputLayout layout, String error){
        if(layout!=null){
            layout.setErrorEnabled(true);
            layout.setError(error);
        }
    }

    protected void hideError(TextInputLayout layout) {
        if (layout != null) {
            layout.setError("");
            layout.setErrorEnabled(false);
        }
    }
}
