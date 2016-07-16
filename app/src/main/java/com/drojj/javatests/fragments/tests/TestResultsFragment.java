package com.drojj.javatests.fragments.tests;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drojj.javatests.model.question.Question;
import com.drojj.javatests.R;
import com.drojj.javatests.adapters.CustomLinearLayoutManager;
import com.drojj.javatests.adapters.QuestionsAnswersRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TestResultsFragment extends Fragment {

    private ArrayList<Question> mQuestions;
    private int mQuestionCounter = 0;

    @BindView(R.id.result_question_answers)
    RecyclerView mRecyclerView;

    @BindView(R.id.result_question_counter)
    TextView mQuestionCounterView;

    @BindView(R.id.result_question_text)
    TextView mQuestionTextView;

    @BindView(R.id.result_question_code)
    TextView mQuestionCodeView;

    @BindView(R.id.result_question_explanation)
    TextView mQuestionExplanationView;

    @BindView(R.id.result_question_card)
    CardView mQuestionCard;

    @BindView(R.id.result_prev_question)
    ImageButton mPrevAnswerButton;

    @BindView(R.id.result_next_question)
    ImageButton mNextAnswerButton;

    private Unbinder unbinder;

    public static TestResultsFragment getInstance(ArrayList<Question> questions) {
        TestResultsFragment fragment = new TestResultsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("questions", questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mQuestions = getArguments().getParcelableArrayList("questions");
        } else {
            mQuestions = savedInstanceState.getParcelableArrayList("questions");
            mQuestionCounter = savedInstanceState.getInt("currentQuestion");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_answers, container, false);
        unbinder = ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity()));

        showQuestion();
        return view;
    }

    @OnClick({R.id.result_prev_question, R.id.result_next_question})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.result_prev_question:
                mQuestionCounter--;
                showQuestion();
                break;
            case R.id.result_next_question:
                mQuestionCounter++;
                showQuestion();
                break;
        }
    }

    private void showQuestion() {
        switchImageButtonVisibility();

        StringBuilder builder = new StringBuilder();
        builder.append(mQuestionCounter + 1).append("/").append(mQuestions.size());

        mQuestionCounterView.setText(builder.toString());
        mQuestionTextView.setText(mQuestions.get(mQuestionCounter).getQuestionText());

        String code = mQuestions.get(mQuestionCounter).getCode();
        if (code != null) {
            mQuestionCodeView.setText(Html.fromHtml(code));
            mQuestionCodeView.setVisibility(View.VISIBLE);
        } else {
            mQuestionCodeView.setVisibility(View.GONE);
        }

        mRecyclerView.setAdapter(new QuestionsAnswersRecyclerAdapter(mQuestions.get(mQuestionCounter).getAnswers(), mQuestions.get(mQuestionCounter).getChosenAnswer()));

        String exp = mQuestions.get(mQuestionCounter).getExplanation();
        if (exp != null) {
            mQuestionExplanationView.setText(exp);
            mQuestionExplanationView.setVisibility(View.VISIBLE);
        } else {
            mQuestionExplanationView.setVisibility(View.GONE);
        }

        if (mQuestions.get(mQuestionCounter).isRightAnswered()) {
            mQuestionCard.setCardBackgroundColor(Color.parseColor("#1100FF00"));
        } else {
            mQuestionCard.setCardBackgroundColor(Color.parseColor("#11FF0000"));
        }
    }

    private void switchImageButtonVisibility() {
        if (mQuestionCounter == 0) {
            mPrevAnswerButton.setEnabled(false);
            mNextAnswerButton.setEnabled(true);
        } else if (mQuestionCounter > 0 && mQuestionCounter < mQuestions.size() - 1) {
            mPrevAnswerButton.setEnabled(true);
            mNextAnswerButton.setEnabled(true);
        } else {
            mPrevAnswerButton.setEnabled(true);
            mNextAnswerButton.setEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions", mQuestions);
        outState.putInt("currentQuestion", mQuestionCounter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Результаты");
    }
}
