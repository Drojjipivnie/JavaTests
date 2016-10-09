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
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.database.FirebaseDatabaseUtils;
import com.drojj.javatests.model.fireweb.FireUser;
import com.drojj.javatests.utils.AuthDataValidator;
import com.drojj.javatests.utils.FirebaseErrorHandler;
import com.drojj.javatests.utils.ServicesChecker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.yandex.metrica.YandexMetrica;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends AuthBaseActivity implements View.OnClickListener, OnSuccessListener<AuthResult>, OnFailureListener {

    private static final String TAG = "SignUp";

    private boolean isLogged = false;

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
                if(firebaseAuth.getCurrentUser()!=null){
                    Log.d("SignUp: ", firebaseAuth.getCurrentUser().getDisplayName() + " logged");
                }
                /*final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!isLogged) {

                        isLogged = true;

                        mLogger.signUp();

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
                                builder.setName(name).setUid(user.getUid()).setEmail(user.getEmail()).setSignUpTime(System.currentTimeMillis());

                                helper.createFireUser(builder.build());
                                startApp();
                                finish();
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }*/
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
        final String name = mInputName.getText().toString().trim();

        boolean isOk = true;

        if (!AuthDataValidator.validatePassword(password)) {
            isOk = false;
            if (password.isEmpty()) {
                showError(mPasswordWrapper, getString(R.string.password_not_entered));
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
            if (ServicesChecker.isInternetAvailable(this)) {
                showProgressDialog(getString(R.string.loading));
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(this, this)
                        .addOnFailureListener(this, this);
            } else {
                Toast.makeText(this, "Нет подключения к сети Интернет", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideErrors() {
        hideError(mNameWrapper);
        hideError(mEmailWrapper);
        hideError(mPasswordWrapper);
    }

    /*public void onCreationFinished() {
        hideProgressDialog();
        startApp();
        finish();
    }

    public void onCreationFailed(Exception e) {
        hideProgressDialog();
        FirebaseDatabaseUtils.deleteUser();
        FirebaseErrorHandler handler = new FirebaseErrorHandler(SignupActivity.this, e);
        showError(mEmailWrapper, handler.toString());
    }*/

    @Override
    public void onSuccess(AuthResult authResult) {
        FireUser user = FireUser.createUser(mInputName.getText().toString(),
                mInputEmail.getText().toString(),
                authResult.getUser().getUid());
        FirebaseDatabaseUtils.createFireUser(user);
        FirebaseDatabaseUtils.updateUserInfo(user);

        hideProgressDialog();
        startApp();
        finish();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        mLogger.failSignUp(e, "SignUp: " + mInputEmail.getText().toString());

        FirebaseErrorHandler handler = new FirebaseErrorHandler(SignupActivity.this, e);
        showError(mEmailWrapper, handler.toString());
    }

    /*@Override
    public void onFailure(@NonNull Exception e) {
        hideProgressDialog();
        mLogger.failSignUp(e, "SignUp: " + mInputEmail.getText().toString());

        FirebaseErrorHandler handler = new FirebaseErrorHandler(SignupActivity.this, e);
        showError(mEmailWrapper, handler.toString());
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        final FireUser user = FireUser.newBuilder()
                .setName(mInputName.getText().toString())
                .setEmail(mInputEmail.getText().toString())
                .setSignUpTime(System.currentTimeMillis())
                .setUid(authResult.getUser().getUid())
                .build();


        FirebaseDatabaseUtils.createFireUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabaseUtils.updateUserInfo(user);
                } else {
                    onCreationFailed(task.getException());
                }
            }
        })
    }*/
}
