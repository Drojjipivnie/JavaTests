package com.drojj.javatests.info;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.utils.analytics.Logger;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class SendTextDialog extends DialogFragment {

    private EditText mTheme;

    private EditText mText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_send_feedback, null, false);

        mTheme = ButterKnife.findById(v, R.id.feedback_theme);
        mText = ButterKnife.findById(v, R.id.feedback_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.feedback_leave)
                .setView(v)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendFeedback();
                    }
                });
            }
        });

        mTheme.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    mText.requestFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });

        mText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    sendFeedback();
                    return true;
                } else {
                    return false;
                }
            }
        });

        ButterKnife.bind(this, alertDialog);

        return alertDialog;
    }

    private void sendFeedback() {

        if (!checkInput()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Map<String, Object> event = new HashMap<>();
            event.put("userId", user.getUid());
            event.put("userEmail", user.getEmail());
            event.put("theme", mTheme.getText().toString());
            event.put("text", mText.getText().toString());
            YandexMetrica.reportEvent("bad_feedback", event);
            this.dismiss();
            getActivity().finish();
            Toast.makeText(getActivity(), "Спасибо за отзыв!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInput() {
        return mTheme.getText().toString().isEmpty() && mText.getText().toString().isEmpty();
    }
}
