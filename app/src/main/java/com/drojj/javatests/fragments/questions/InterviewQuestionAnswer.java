package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drojj.javatests.R;
import com.drojj.javatests.model.InterviewQuestion;
import com.drojj.javatests.utils.elementshelper.QuestionElement;
import com.drojj.javatests.utils.elementshelper.QuestionElements;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionAnswer extends Fragment {

    private InterviewQuestion mQuestion;

    @BindView(R.id.question_main_view)
    LinearLayout mainView;

    private Unbinder unbinder;

    private QuestionElements mHelper;


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

        mHelper = new QuestionElements(mQuestion.getAnswer().split("!split!"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interview_question, container, false);

        unbinder = ButterKnife.bind(this, view);

        for(QuestionElement element: mHelper.getElements()){
            View v = element.getView(getActivity());
            mainView.addView(v);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mQuestion.getQuestion());
        mainView.requestFocus();
    }
}
