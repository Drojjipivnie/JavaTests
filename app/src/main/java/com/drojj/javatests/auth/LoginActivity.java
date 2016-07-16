package com.drojj.javatests.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.fragments.tests.PasswordReminder;
import com.drojj.javatests.utils.FirebaseErrorHandler;
import com.drojj.javatests.utils.AuthDataValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AuthBaseActivity implements View.OnClickListener {

    private static final String TAG = "LOGIN";

    @BindView(R.id.login_password_wrapper)
    TextInputLayout mPasswordWrapper;

    @BindView(R.id.login_email_wrapper)
    TextInputLayout mEmailWrapper;

    @BindView(R.id.edittext_login)
    EditText mEmailField;

    @BindView(R.id.edittext_password)
    EditText mPasswordField;

    @BindView(R.id.login_view)
    View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        //TODO:Transitions between Activities

        mEmailField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordField.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });

        mPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {
                    logIn();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected FirebaseAuth.AuthStateListener setOnAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startApp();
                    finish();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @OnClick({R.id.button_login, R.id.link_signup, R.id.link_forgot_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                logIn();
                break;
            case R.id.link_signup:
                startSignUpActivity();
                break;
            case R.id.link_forgot_password:
                showReminderWindow();
                break;
        }
    }

    private void logIn() {

        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);

        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        boolean isOk = true;

        if (!AuthDataValidator.validateEmail(email)) {
            showError(mEmailWrapper, "Email введен неверно.");
            isOk = false;
        }

        if (password.isEmpty()) {
            showError(mPasswordWrapper, "Введите пароль");
            isOk = false;
        }
        if (isOk) {
            showProgressDialog("Загрузка...");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                FirebaseErrorHandler handler = new FirebaseErrorHandler(LoginActivity.this, task.getException());
                                if (handler.getErrorCode() == FirebaseErrorHandler.ERROR_WRONG_PASSWORD) {
                                    showError(mPasswordWrapper, handler.toString());
                                } else {
                                    showError(mEmailWrapper, handler.toString());
                                }
                            } else {
                                String username = task.getResult().getUser().getDisplayName();
                                Toast.makeText(LoginActivity.this, "Добро пожаловать " + username + "!", Toast.LENGTH_SHORT).show();
                            }
                            hideProgressDialog();
                        }
                    });
        }
    }

    private void startSignUpActivity() {
        clear();
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void showReminderWindow() {
        DialogFragment fragment = PasswordReminder.newInstance();
        fragment.show(this.getSupportFragmentManager(), null);
    }

    private void clear(){
        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);

        mEmailField.setText("");
        mPasswordField.setText("");

        mView.requestFocus();
    }
}
