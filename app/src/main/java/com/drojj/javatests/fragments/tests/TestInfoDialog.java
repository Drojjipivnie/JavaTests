package com.drojj.javatests.fragments.tests;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.model.Test;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class TestInfoDialog extends DialogFragment implements View.OnClickListener {

    private Test mTest;

    @BindView(R.id.dialog_start_test_button)
    Button startTestButton;

    private Unbinder unbinder;

    public static TestInfoDialog newInstance(Test test) {
        TestInfoDialog dialog = new TestInfoDialog();

        Bundle args = new Bundle();
        args.putParcelable("test", test);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTest = getArguments().getParcelable("test");

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_test_open, container, false);

        unbinder = ButterKnife.bind(this, v);

        TextView totalQuestions = ButterKnife.findById(v, R.id.dialog_total_questions);
        TextView bestScore = ButterKnife.findById(v, R.id.dialog_best_score);
        TextView dialogTitle = ButterKnife.findById(v, R.id.dialog_title);

        totalQuestions.setText(String.valueOf(mTest.question_count));
        bestScore.setText(String.valueOf(mTest.progress));
        dialogTitle.setText(mTest.name);

        return v;
    }

    @OnClick({R.id.dialog_start_test_button, R.id.dialog_show_progress})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_start_test_button:
                getDialog().dismiss();
                OpenFragmentEvent<Test> event = new OpenFragmentEvent<>(OpenFragmentEvent.FragmentType.TEST_QUIZ, mTest);
                EventBus.getDefault().post(event);
                break;
            case R.id.dialog_show_progress:
                getDialog().dismiss();
                OpenFragmentEvent<Test> event2 = new OpenFragmentEvent<>(OpenFragmentEvent.FragmentType.TEST_PROGRESS, mTest);
                EventBus.getDefault().post(event2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
