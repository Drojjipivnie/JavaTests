package com.drojj.javatests.fragments.tests;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.CustomLinearLayoutManager;
import com.drojj.javatests.adapters.QuestionsAnswersRecyclerAdapter;
import com.drojj.javatests.database.FirebaseDatabaseUtils;
import com.drojj.javatests.fragments.BaseFragment;
import com.drojj.javatests.model.question.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestResultsFragment extends BaseFragment {

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

    public static TestResultsFragment newInstance(ArrayList<Question> questions) {
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

    @Override
    protected String setTitle() {
        return getActivity().getString(R.string.title_test_results);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_answers, container, false);
        mUnbinder = ButterKnife.bind(this, view);

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
        if (code != null && !code.isEmpty()) {
            mQuestionCodeView.setText(Html.fromHtml(code));
            mQuestionCodeView.setVisibility(View.VISIBLE);
        } else {
            mQuestionCodeView.setVisibility(View.GONE);
        }

        final Question currQuestion = mQuestions.get(mQuestionCounter);
        final QuestionsAnswersRecyclerAdapter questionsAnswersRecyclerAdapter = new QuestionsAnswersRecyclerAdapter(currQuestion.getAnswers(), currQuestion.getChosenAnswer());

        final DatabaseReference questionStatisticsReference = FirebaseDatabaseUtils.getQuestionStatisticsReference(currQuestion.getTestId(), currQuestion.getId());
        questionStatisticsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    int[] stats = new int[currQuestion.getAnswers().size()];
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int answerOrder = Integer.parseInt(snapshot.getKey());
                        Long value = (Long) snapshot.getValue();
                        int answerCount = value.intValue();
                        stats[answerOrder - 1] = answerCount;
                    }
                    questionsAnswersRecyclerAdapter.showStatistics(stats);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView.setAdapter(questionsAnswersRecyclerAdapter);

        String questionExplanation = mQuestions.get(mQuestionCounter).getExplanation();
        if (questionExplanation != null && !questionExplanation.isEmpty()) {
            mQuestionExplanationView.setText(questionExplanation);
            mQuestionExplanationView.setVisibility(View.VISIBLE);
        } else {
            mQuestionExplanationView.setVisibility(View.GONE);
        }

        if (mQuestions.get(mQuestionCounter).isRightAnswered()) {
            mQuestionCard.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.test_result_right_answer));
        } else {
            mQuestionCard.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.test_result_wrong_answer));
        }
    }

    private void switchImageButtonVisibility() {
        if (mQuestionCounter == 0) {
            changeButtonVisibility(View.INVISIBLE, View.VISIBLE);
        } else if (mQuestionCounter > 0 && mQuestionCounter < mQuestions.size() - 1) {
            changeButtonVisibility(View.VISIBLE, View.VISIBLE);
        } else {
            changeButtonVisibility(View.VISIBLE, View.INVISIBLE);
        }
    }

    private void changeButtonVisibility(int leftButtonState, int rightButtonState) {
        mPrevAnswerButton.setVisibility(leftButtonState);
        mNextAnswerButton.setVisibility(rightButtonState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions", mQuestions);
        outState.putInt("currentQuestion", mQuestionCounter);
    }

    @OnClick(R.id.notice_error)
    public void onErrorFieldClick() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("plain/text");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"javatestsandquestions@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ошибка в вопросе [id:" + mQuestions.get(mQuestionCounter).getId() + "]");
        intent.putExtra(Intent.EXTRA_TEXT, "Расскажите, пожалуйста, о замеченной ошибке:\n");
        startActivity(intent);
    }
}
