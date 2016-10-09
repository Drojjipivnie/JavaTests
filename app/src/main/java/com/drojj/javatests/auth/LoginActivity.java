package com.drojj.javatests.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.NonGoogleApiActivity;
import com.drojj.javatests.R;
import com.drojj.javatests.fragments.PasswordReminder;
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
                    mLogger.logIn();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startApp();
                    finish();
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isGoogleAPIAvailable) {
            showSnackBar();
        }
    }

    @OnClick({R.id.button_login, R.id.link_signup, R.id.link_forgot_password})
    public void onClick(View v) {
        if (isGoogleAPIAvailable) {
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
                default:
                    break;
            }
        } else {
            Toast.makeText(LoginActivity.this, "Невозможное дествие!", Toast.LENGTH_SHORT).show();
        }
    }

    private void logIn() {

        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);

        final String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        boolean isOk = true;

        if (!AuthDataValidator.validateEmail(email)) {
            showError(mEmailWrapper, getString(R.string.email_not_validated));
            isOk = false;
        }

        if (password.isEmpty()) {
            showError(mPasswordWrapper, getString(R.string.password_not_entered));
            isOk = false;
        }
        if (isOk) {
            showProgressDialog(getString(R.string.loading));
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
                                mLogger.failLogIn(task.getException(), "LogIn" + email);
                            } else {
                                String username = task.getResult().getUser().getDisplayName();
                                Toast.makeText(LoginActivity.this, getString(R.string.welcome_user) + username + "!", Toast.LENGTH_SHORT).show();
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

    private void clear() {
        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);

        mEmailField.setText("");
        mPasswordField.setText("");

        mView.requestFocus();
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "На вашем устройстве отсутствует Google Play Services!", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Только вопросы", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogger.nonGoogleApi();
                startActivity(new Intent(LoginActivity.this, NonGoogleApiActivity.class));
                LoginActivity.this.finish();
            }
        });
        snackbar.show();
    }
}
