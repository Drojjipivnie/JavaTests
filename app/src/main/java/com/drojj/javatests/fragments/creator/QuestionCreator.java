package com.drojj.javatests.fragments.creator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.database.TestDatabase;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.question.Answer;
import com.drojj.javatests.model.question.Question;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionCreator extends Fragment implements View.OnClickListener {

    @BindView(R.id.spinner_choose_test)
    Spinner spinner;

    @BindView(R.id.edit_text_question_input)
    EditText questionInput;

    @BindView(R.id.switch_add_code)
    Switch switchAddCode;

    @BindView(R.id.edit_text_code_input)
    EditText codeInput;

    @BindView(R.id.button_add_answer)
    Button addAnswer;

    @BindView(R.id.button_remove_answer)
    Button removeAnswer;

    @BindView(R.id.button_next_step)
    Button nextStep;

    @BindView(R.id.answer3_text_input)
    View answer3InputLayout;

    @BindView(R.id.answer4_text_input)
    View answer4InputLayout;

    @BindView(R.id.answer1)
    EditText answer1_et;

    @BindView(R.id.answer2)
    EditText answer2_et;

    @BindView(R.id.answer3)
    EditText answer3_et;

    @BindView(R.id.answer4)
    EditText answer4_et;

    EditText[] editTexts;

    private List<Test> tests;

    private char answersAdded = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tests = TestDatabase.getInstance(getActivity()).getTests();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_question, container, false);
        ButterKnife.bind(this, v);
        editTexts = new EditText[]{answer1_et, answer2_et, answer3_et, answer4_et};
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        if (savedInstanceState != null) {
            answersAdded = savedInstanceState.getChar("answersAdded");
            switch (answersAdded) {
                case 3:
                    answer3InputLayout.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    answer3InputLayout.setVisibility(View.VISIBLE);
                    answer4InputLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putChar("answersAdded", answersAdded);
    }

    private void initViews() {
        initSpinner();
        initButtons();
        switchAddCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    codeInput.setVisibility(View.VISIBLE);
                } else {
                    codeInput.setVisibility(View.GONE);
                    codeInput.setText("");
                }
            }
        });
    }

    private void initButtons() {
        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (answersAdded) {
                    case 2:
                        answer3InputLayout.setVisibility(View.VISIBLE);
                        answersAdded++;
                        break;
                    case 3:
                        answer4InputLayout.setVisibility(View.VISIBLE);
                        answersAdded++;
                        break;
                    case 4:
                        Toast.makeText(getActivity(), "Максимальное число ответов = 4", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        removeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (answersAdded) {
                    case 2:
                        Toast.makeText(getActivity(), "Минимальное число ответов = 2", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        answer3InputLayout.setVisibility(View.GONE);
                        answer3_et.setText("");
                        answersAdded--;
                        break;
                    case 4:
                        answer4InputLayout.setVisibility(View.GONE);
                        answer4_et.setText("");
                        answersAdded--;
                        break;
                }
            }
        });

        nextStep.setOnClickListener(this);
    }

    private void initSpinner() {
        String[] arr = new String[tests.size()];
        for (int i = 0; i < tests.size(); i++) {
            arr[i] = tests.get(i).name;
        }
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);
    }

    @Override
    public void onClick(View view) {
        String questionText = questionInput.getText().toString().trim();
        if (questionText.isEmpty()) {
            Toast.makeText(getActivity(), "Введите текст вопроса", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answer1_et.getText().toString().trim().isEmpty() || answer2_et.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Введите текст двух первых ответов", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < answersAdded; i++) {
            answers.add(new Answer(i, editTexts[i].getText().toString()));
        }

        Question question = new Question(spinner.getSelectedItemPosition() + 1, questionInput.getText().toString(), answers, codeInput.getText().toString(), "", 0);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_creator, QuestionPreview.newInstance(question))
                .addToBackStack("preview")
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (answersAdded) {
            case 3:
                answer3InputLayout.setVisibility(View.VISIBLE);
                break;
            case 4:
                answer3InputLayout.setVisibility(View.VISIBLE);
                answer4InputLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
