package com.drojj.javatests.fragments.questions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.drojj.javatests.MainWindow;
import com.drojj.javatests.R;
import com.drojj.javatests.fragments.BaseFragment;
import com.drojj.javatests.model.InterviewQuestion;
import com.drojj.javatests.utils.elementshelper.QuestionElementsAsyncHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InterviewQuestionAnswer extends BaseFragment {

    private InterviewQuestion mQuestion;

    @BindView(R.id.question_main_view)
    LinearLayout mainView;

    private ProgressBar mProgressBar;

    public static InterviewQuestionAnswer newInstance(InterviewQuestion question) {
        InterviewQuestionAnswer fragment = new InterviewQuestionAnswer();
        Bundle bundle = new Bundle();
        bundle.putSerializable("question", question);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mQuestion = (InterviewQuestion) getArguments().getSerializable("question");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interview_question, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProgressBar = ((MainWindow) getActivity()).getProgressBar();
    }

    @Override
    public void onStart() {
        super.onStart();
        new QuestionElementsAsyncHandler(mainView, mProgressBar).execute(mQuestion.getAnswer());
    }

    @Override
    public void onResume() {
        super.onResume();
        mainView.requestFocus();
    }

    @Override
    protected String setTitle() {
        return mQuestion.getQuestion();
    }
}
