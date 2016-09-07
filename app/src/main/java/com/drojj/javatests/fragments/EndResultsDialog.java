package com.drojj.javatests.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.drojj.javatests.R;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.model.question.Question;
import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class EndResultsDialog extends DialogFragment {

    private static final String RIGHT_ANSWERS = "RIGHT_ANSWERS";

    private static final String ANSWERS_COUNT = "ANSWERS_COUNT";

    private static final String TAG = "TAG";

    private String mText;

    public static EndResultsDialog newInstance(int rightAnswers, int answersCount, String tag, List<Question> list) {
        EndResultsDialog dialog = new EndResultsDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(RIGHT_ANSWERS, rightAnswers);
        bundle.putInt(ANSWERS_COUNT, answersCount);
        bundle.putString(TAG, tag);
        bundle.putParcelableArrayList("list", (ArrayList<Question>) list);

        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int rightCount = getArguments().getInt(RIGHT_ANSWERS);
        int answersCount = getArguments().getInt(ANSWERS_COUNT);

        StringBuilder builder = new StringBuilder();
        builder.append(getActivity().getString(R.string.test_final_score))
                .append(rightCount)
                .append(getActivity().getString(R.string.right_answers_from))
                .append(answersCount)
                .append(".")
                .append(getActivity().getString(R.string.want_to_see_answers));

        mText = builder.toString();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.test_complited)
                .setCancelable(false)
                .setMessage(mText)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        YandexAnalyticsLogger.getInstance().clickTestResults(false);
                        dialog.dismiss();
                        getFragmentManager().popBackStack();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        YandexAnalyticsLogger.getInstance().clickTestResults(true);
                        ArrayList<Question> list = getArguments().getParcelableArrayList("list");
                        dialog.dismiss();
                        getFragmentManager().popBackStackImmediate("test_list", 0);
                        OpenFragmentEvent<ArrayList<Question>> event = new OpenFragmentEvent<>(OpenFragmentEvent.FragmentType.TEST_RESULTS, list);
                        EventBus.getDefault().post(event);
                    }
                });


        return builder.create();
    }
}
