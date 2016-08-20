package com.drojj.javatests.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.fireweb.FireUser;
import com.drojj.javatests.model.fireweb.FireUserHelper;
import com.drojj.javatests.utils.AuthDataValidator;
import com.drojj.javatests.utils.analytics.FirebaseAnalyticsLogger;
import com.drojj.javatests.utils.FirebaseErrorHandler;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AuthBaseActivity implements View.OnClickListener {

    private static final String TAG = "SignUp";

    @BindView(R.id.input_name_wrapper)
    TextInputLayout mNameWrapper;

    @BindView(R.id.input_email_wrapper)
    TextInputLayout mEmailWrapper;

    @BindView(R.id.input_password_wrapper)
    TextInputLayout mPasswordWrapper;

    @BindView(R.id.input_name)
    EditText mInputName;

    @BindView(R.id.input_email)
    EditText mInputEmail;

    @BindView(R.id.input_password)
    EditText mInputPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        mInputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEND) {
                    signUp();
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
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    mLogger.logSignUp(user.getUid());

                    final String name = mInputName.getText().toString();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hideProgressDialog();
                            FireUserHelper helper = new FireUserHelper(user.getUid());

                            FireUser.Builder builder = FireUser.newBuilder();
                            builder.setName(name).setUid(user.getUid());

                            helper.createFireUser(builder.build());
                            startApp();
                            finish();
                        }
                    });
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @OnClick(R.id.btn_signup)
    public void onClick(View v) {
        signUp();
    }

    private void signUp() {
        hideErrors();

        final String email = mInputEmail.getText().toString().trim();
        final String password = mInputPassword.getText().toString().trim();
        String name = mInputName.getText().toString().trim();

        boolean isOk = true;

        if (!AuthDataValidator.validatePassword(password)) {
            isOk = false;
            if (password.isEmpty()) {
                showError(mPasswordWrapper,  getString(R.string.password_not_entered));
            } else {
                showError(mPasswordWrapper, getString(R.string.password_not_validated));
            }
        }

        if (!AuthDataValidator.validateEmail(email)) {
            showError(mEmailWrapper, getString(R.string.email_not_validated));
            isOk = false;
        }

        if (!AuthDataValidator.validateName(name)) {
            showError(mNameWrapper, getString(R.string.name_not_validated));
            isOk = false;
        }

        if (isOk) {
            showProgressDialog(getString(R.string.loading));
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        FirebaseErrorHandler handler = new FirebaseErrorHandler(SignupActivity.this, task.getException());
                        handler.showErrorToast();
                        mLogger.logFailSignUp(handler.toString());
                        hideProgressDialog();
                    }
                }
            });
        }
    }

    private void hideErrors() {
        hideError(mNameWrapper);
        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);
    }
}
