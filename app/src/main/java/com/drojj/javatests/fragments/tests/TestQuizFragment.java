package com.drojj.javatests.fragments.tests;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.fragments.EndResultsDialog;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.question.Question;
import com.drojj.javatests.R;
import com.drojj.javatests.adapters.CustomLinearLayoutManager;
import com.drojj.javatests.adapters.QuestionsRecyclerAdapter;
import com.drojj.javatests.animations.FlipAnimation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TestQuizFragment extends Fragment {

    private QuestionsRecyclerAdapter mQuestionsAdapter;

    private List<Question> mQuestionsList;

    private int mTestId, mQuestionCounter = 0, mWrongAnswersNum = 0;

    @BindView(R.id.question_answers)
    RecyclerView mRecyclerView;

    @BindView(R.id.question_counter)
    TextView mQuestionCounterView;

    @BindView(R.id.question_text)
    TextView mQuestionTextView;

    @BindView(R.id.question_code)
    TextView mQuestionCodeView;

    @BindView(R.id.question_card)
    CardView mQuestionCard;

    @BindView(R.id.question_scroll)
    NestedScrollView mScrollView;

    private TestDatabase mDatabase;

    private Unbinder unbinder;

    private Test mTest;

    public static TestQuizFragment newInstance(Test test) {
        TestQuizFragment fragment = new TestQuizFragment();

        Bundle args = new Bundle();
        args.putParcelable("test", test);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = TestDatabase.getInstance(getActivity());
        if (savedInstanceState == null) {
            mTest = getArguments().getParcelable("test");

            mTestId = mTest.id;
            mQuestionsList = mDatabase.getQuestions(mTestId, mTest.question_count);
        } else {
            mTest = savedInstanceState.getParcelable("test");

            mQuestionsList = savedInstanceState.getParcelableArrayList("questions");
            mTestId = savedInstanceState.getInt("testId");
            mQuestionCounter = savedInstanceState.getInt("currentQuestion");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_test, container, false);

        unbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity()));

        showNextQuestion(false);

        return view;
    }

    @OnClick(R.id.button)
    public void onClick() {
        if (mQuestionsAdapter.isAnyAnswerChosen()) {

            mQuestionsList.get(mQuestionCounter).setChosenAnswer(mQuestionsAdapter.getChosenAnswer());

            if (!mQuestionsAdapter.isAnswerRight()) {
                mQuestionsList.get(mQuestionCounter).setRightAnswered(false);
                mWrongAnswersNum++;
            }

            mQuestionCounter++;

            if (mQuestionsList.size() != mQuestionCounter) {
                showNextQuestion(true);
            } else {
                int rightAnswers = mQuestionsList.size() - mWrongAnswersNum;

                showFinalDialog();
                if (mTest.progress < rightAnswers) {
                    insertNewScore(rightAnswers);
                }
                updateLastTimeTestPassed(rightAnswers);
            }

        } else {
            Toast.makeText(getActivity(), R.string.choose_any_answer, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLastTimeTestPassed(int rightAnswers) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid()).child("tests_last_time").child("test" + String.valueOf(mTest.id)).setValue(System.currentTimeMillis());


        String key = reference.child("users").child(user.getUid()).child("tests_entrys").child("test" + String.valueOf(mTest.id)).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("users/" + user.getUid() + "/tests_entrys/test" + String.valueOf(mTest.id) + "/" + key + "/time", System.currentTimeMillis());
        childUpdates.put("users/" + user.getUid() + "/tests_entrys/test" + String.valueOf(mTest.id) + "/" + key + "/score", rightAnswers);
        reference.updateChildren(childUpdates);
    }

    private void insertNewScore(int score) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid()).child("test_progress").child("test" + String.valueOf(mTest.id)).setValue(score);
    }

    private void showFinalDialog() {
        int rightAnswers = mQuestionsList.size() - mWrongAnswersNum;
        EndResultsDialog dialog = EndResultsDialog.newInstance(rightAnswers, mQuestionsList.size(), getTag(), mQuestionsList);
        dialog.show(getFragmentManager(), "dialog_end_test");
    }

    private void showNextQuestion(boolean showAnimation) {
        mScrollView.fullScroll(View.FOCUS_UP);

        StringBuilder builder = new StringBuilder();
        builder.append(mQuestionCounter + 1).append("/").append(mQuestionsList.size());

        mQuestionTextView.setText(mQuestionsList.get(mQuestionCounter).getQuestionText());

        if (mQuestionsList.get(mQuestionCounter).getCode() != null) {
            mQuestionCodeView.setText(Html.fromHtml(mQuestionsList.get(mQuestionCounter).getCode()));
            mQuestionCodeView.setVisibility(View.VISIBLE);
        } else {
            mQuestionCodeView.setVisibility(View.GONE);
        }

        mQuestionsAdapter = new QuestionsRecyclerAdapter(mQuestionsList.get(mQuestionCounter).getAnswers());
        mRecyclerView.setAdapter(mQuestionsAdapter);

        if (showAnimation) {
            FlipAnimation.startCardAnimation(mQuestionCard);
            FlipAnimation.animateText(mQuestionCounterView, builder.toString());
        } else {
            mQuestionCounterView.setText(builder.toString());
        }

        if (getView() != null) {
            getView().requestFocus();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mTest.name);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    showDialog();
                    return true;
                }
                return false;
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning);
        builder.setMessage(R.string.test_not_completed);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().popBackStack();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions", (ArrayList<Question>)mQuestionsList);
        outState.putInt("testId", mTestId);
        outState.putInt("currentQuestion", mQuestionCounter);
        outState.putParcelable("test", mTest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
