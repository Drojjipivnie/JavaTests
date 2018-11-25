package com.drojj.javatests.fragments.creator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.CustomLinearLayoutManager;
import com.drojj.javatests.adapters.QuestionsRecyclerAdapter;
import com.drojj.javatests.database.FirebaseDatabaseUtils;
import com.drojj.javatests.model.question.Question;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QuestionPreview extends Fragment {

    private Question question;

    @BindView(R.id.question_answers)
    RecyclerView mRecyclerView;

    @BindView(R.id.question_text)
    TextView mQuestionTextView;

    @BindView(R.id.question_code)
    TextView mQuestionCodeView;

    @BindView(R.id.comment)
    EditText editText;

    private QuestionsRecyclerAdapter questionsRecyclerAdapter;


    public static QuestionPreview newInstance(Question question) {
        QuestionPreview fragment = new QuestionPreview();

        Bundle args = new Bundle();
        args.putParcelable("question", question);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            question = getArguments().getParcelable("question");
        } else {
            question = savedInstanceState.getParcelable("question");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_preview, container, false);
        ButterKnife.bind(this, v);
        mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(getActivity()));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQuestionTextView.setText(question.getQuestionText());

        if (question.getCode() != null && !question.getCode().isEmpty()) {
            mQuestionCodeView.setText(Html.fromHtml(question.getCode()));
            mQuestionCodeView.setVisibility(View.VISIBLE);
        } else {
            mQuestionCodeView.setVisibility(View.GONE);
        }

        questionsRecyclerAdapter = new QuestionsRecyclerAdapter(question.getAnswers(), false);
        mRecyclerView.setAdapter(questionsRecyclerAdapter);

        if (getView() != null) {
            getView().requestFocus();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("question", question);
    }

    @OnClick(R.id.button_send)
    public void sendQuestion() {

        if (questionsRecyclerAdapter.getChosenAnswer() == -1) {
            Toast.makeText(getActivity(), "Выберите правильный ответ!", Toast.LENGTH_SHORT).show();
            return;
        }

        question.getAnswers().get(questionsRecyclerAdapter.getChosenAnswer()).setThisAnswerRight();
        FirebaseDatabaseUtils.sendNewQuestion(question, editText.getText().toString());
        Toast.makeText(getActivity(), "Сообщение отправлено. Спасибо за помощь!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_back)
    public void goBack() {
        getActivity().onBackPressed();
    }
}
