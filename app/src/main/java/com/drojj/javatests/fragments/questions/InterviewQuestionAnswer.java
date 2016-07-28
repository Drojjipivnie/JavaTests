package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.model.InterviewQuestion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionAnswer extends Fragment {

    private InterviewQuestion mQuestion;

    @BindView(R.id.question_text)
    TextView qtext;

    private Unbinder unbinder;


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

        unbinder = ButterKnife.bind(this, view);

        String text = mQuestion.getAnswer();

        qtext.setText(Html.fromHtml(text));

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
    }
}
