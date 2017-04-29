package com.drojj.javatests.utils;

import android.content.Context;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseErrorHandler {

    public static final int ERROR_USER_DISABLED = 1;
    public static final int ERROR_USER_NOT_FOUND = 2;
    public static final int ERROR_WRONG_PASSWORD = 3;
    public static final int ERROR_EMAIL_ALREADY_IN_USE = 4;
    public static final int ERROR_TOO_MANY_REQUESTS = 5;
    public static final int ERROR_API_NOT_AVAILABLE = 6;
    public static final int ERROR_NETWORK_EXCEPTION = 7;
    public static final int DEFAULT = 0;

    private Context mCtx;
    private String mMessage;
    private int mErrorCode;

    public FirebaseErrorHandler(Context mCtx, Exception e) {
        this.mCtx = mCtx;

        if (e instanceof FirebaseAuthException) {
            handleAuthException((FirebaseAuthException) e);
        } else if (e instanceof FirebaseException) {
            handleOtherException((FirebaseException) e);
        }

    }

    public void showErrorToast() {
        Toast.makeText(mCtx, mMessage, Toast.LENGTH_LONG).show();
    }

    private void handleAuthException(FirebaseAuthException e) {
        switch (e.getErrorCode()) {
            case "ERROR_USER_DISABLED":
                mErrorCode = ERROR_USER_DISABLED;
                mMessage = mCtx.getString(R.string.error_user_disabled);
                break;
            case "ERROR_USER_NOT_FOUND":
                mErrorCode = ERROR_USER_NOT_FOUND;
                mMessage = mCtx.getString(R.string.error_user_not_found);
                break;
            case "ERROR_WRONG_PASSWORD":
                mErrorCode = ERROR_WRONG_PASSWORD;
                mMessage = mCtx.getString(R.string.error_wrong_password);
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                mErrorCode = ERROR_EMAIL_ALREADY_IN_USE;
                mMessage = mCtx.getString(R.string.error_email_already_in_use);
                break;
            default:
                mErrorCode = DEFAULT;
                mMessage = "Some other error: " + e.getErrorCode();
                break;
        }
    }

    private void handleOtherException(FirebaseException e) {
        if (e instanceof FirebaseTooManyRequestsException) {
            mMessage = mCtx.getString(R.string.error_too_many_requests);
            mErrorCode = ERROR_TOO_MANY_REQUESTS;
        } else if (e instanceof FirebaseNetworkException || e.getMessage().equals("An internal error has occured. [ <<Network Error>> ]")) {
            mMessage = mCtx.getString(R.string.error_network_exception);
            mErrorCode = ERROR_NETWORK_EXCEPTION;
        } else if (e instanceof FirebaseApiNotAvailableException) {
            mMessage = mCtx.getString(R.string.error_api_not_available);
            mErrorCode = ERROR_API_NOT_AVAILABLE;
        }
    }

    @Override
    public String toString() {
        return mMessage;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
