package com.drojj.javatests.testeditor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.drojj.javatests.R;
import com.drojj.javatests.fragments.creator.QuestionCreator;
import com.drojj.javatests.fragments.creator.TestCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestCreatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String CREATE_TEST = "Создать тест";
    private final static String CREATE_QUESTION_IN_TEST = "Добавить вопрос";
    private final static String CHANGE_QUESTION_IN_TEST = "Изменить вопрос";

    private final static String[] arr = {CREATE_TEST, CREATE_QUESTION_IN_TEST};

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.choose_spinner)
    Spinner mSpinner;
    @BindView(R.id.fragment_container_creator)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_creator);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.navigation_menu_help_me));

        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(0);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_creator, new TestCreator())
                        .commit();
                break;
            case 1:
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_creator, new QuestionCreator())
                        .commit();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
