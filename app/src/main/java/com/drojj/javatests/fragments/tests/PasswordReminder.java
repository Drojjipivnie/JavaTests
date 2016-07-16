package com.drojj.javatests.fragments.tests;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.utils.AuthDataValidator;
import com.drojj.javatests.utils.FirebaseErrorHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import butterknife.ButterKnife;

public class PasswordReminder extends android.support.v4.app.DialogFragment {

    public static PasswordReminder newInstance(){
        return  new PasswordReminder();
    }

    private TextInputLayout mEmailTextWrapper;
    private ProgressDialog mProgressLogin;
    private View mRootView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_reminder_password, null, false);


        mEmailTextWrapper = ButterKnife.findById(v, R.id.email_reg_wrapper);
        mRootView = ButterKnife.findById(v, R.id.root_view_dialog);
        mRootView.requestFocus();

        mProgressLogin = new ProgressDialog(getActivity());
        mProgressLogin.setTitle("Загрузка");
        mProgressLogin.setMessage("Подождите пожалуйста...");
        mProgressLogin.setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Вспомнить пароль")
                .setView(v)
                .setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Отмена", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendEmail(alertDialog);
                    }
                });
            }
        });

        if (mEmailTextWrapper.getEditText() != null) {
            mEmailTextWrapper.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        sendEmail(alertDialog);
                        handled = true;
                    }
                    return handled;
                }
            });
        }

        mEmailTextWrapper.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideError(mEmailTextWrapper);
                }
            }
        });

        ButterKnife.bind(this, alertDialog);

        return alertDialog;
    }

    private void sendEmail(final AlertDialog alertDialog) {

        String email = mEmailTextWrapper.getEditText().getText().toString().trim();

        if(email.isEmpty() || !AuthDataValidator.validateEmail(email)){
            if (mRootView != null)
                mRootView.requestFocus();
            showError(mEmailTextWrapper,"Email введен неверно.");
            return;
        }

        mProgressLogin.show();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()||!task.isComplete()){

                    if (mRootView != null)
                        mRootView.requestFocus();
                    FirebaseErrorHandler handler = new FirebaseErrorHandler(getActivity(),task.getException());
                    showError(mEmailTextWrapper,handler.toString());
                }else{
                    Toast.makeText(getActivity(),"Успешно! Мы отправили вам сообщение.",Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
                mProgressLogin.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (mEmailTextWrapper != null && mEmailTextWrapper.getEditText() != null) {
            mEmailTextWrapper.getEditText().setOnFocusChangeListener(null);
            mEmailTextWrapper.getEditText().setOnEditorActionListener(null);
        }
        super.onDestroyView();
    }

    private void hideError(TextInputLayout textInputLayout) {
        if (textInputLayout != null) {
            textInputLayout.setError("");
            textInputLayout.setErrorEnabled(false);
        }
    }

    private void showError(TextInputLayout textInputLayout, String errorText) {
        if (textInputLayout != null) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(errorText);
        }
    }
}
