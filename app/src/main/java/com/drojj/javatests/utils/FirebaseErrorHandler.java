package com.drojj.javatests.utils;

import android.content.Context;
import android.widget.Toast;

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
                mMessage = "Действие учетной записи приостановлено.";
                break;
            case "ERROR_USER_NOT_FOUND":
                mErrorCode = ERROR_USER_NOT_FOUND;
                mMessage = "Нет пользователя с таким email.";
                break;
            case "ERROR_WRONG_PASSWORD":
                mErrorCode = ERROR_WRONG_PASSWORD;
                mMessage = "Неверный пароль.";
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                mErrorCode = ERROR_EMAIL_ALREADY_IN_USE;
                mMessage = "Данный email уже используется.";
                break;
            default:
                mErrorCode = DEFAULT;
                mMessage = "Some other error: " + e.getErrorCode();
                break;
        }
    }

    private void handleOtherException(FirebaseException e) {
        if (e instanceof FirebaseTooManyRequestsException) {
            mMessage = "Слишком много запросов. Попробуйте чуть позже.";
            mErrorCode = ERROR_TOO_MANY_REQUESTS;
        } else if (e instanceof FirebaseNetworkException || e.getMessage().equals("An internal error has occured. [ <<Network Error>> ]")) {
            mMessage = "Проблемы со связью.";
            mErrorCode = ERROR_NETWORK_EXCEPTION;
        } else if (e instanceof FirebaseApiNotAvailableException) {
            mMessage = "Сейчас сервис не доступен.";
            mErrorCode = ERROR_API_NOT_AVAILABLE;
        }
    }

    @Override
    public String toString() {
        return mMessage;
    }

    public int getErrorCode(){
        return mErrorCode;
    }
}
