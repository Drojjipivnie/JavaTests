package com.drojj.javatests.testeditor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.drojj.javatests.R;
import com.drojj.javatests.fragments.creator.QuestionCreator;
import com.drojj.javatests.fragments.creator.TestCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestCreatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final static String CREATE_TEST = "Создать тест";
    private final static String CREATE_QUESTION_IN_TEST = "Добавить вопрос";
    private final static String CHANGE_QUESTION_IN_TEST = "Изменить вопрос";

    private final static String[] arr = {CREATE_QUESTION_IN_TEST, CREATE_TEST};

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.choose_spinner)
    Spinner mSpinner;
    @BindView(R.id.fragment_container_creator)
    FrameLayout mFragmentContainer;
    @BindView(R.id.textView_link)
    TextView mLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_creator);
        ButterKnife.bind(this);

        initViews();
        if (savedInstanceState == null) {
            replaceFragment(new QuestionCreator(), "q_creator");
        }
    }

    private void initViews() {
        mLink.setText(Html.fromHtml(getString(R.string.link)));
        mLink.setMovementMethod(LinkMovementMethod.getInstance());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle(getString(R.string.navigation_menu_help_me));

        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, arr);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setSelection(0, false);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        while (supportFragmentManager.getBackStackEntryCount() > 0) {
            supportFragmentManager.popBackStackImmediate();
        }
        switch (position) {
            case 0:
                replaceFragment(new QuestionCreator(), "q_creator");
                break;
            case 1:
                replaceFragment(new TestCreator(), "t_creqator");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_creator, fragment)
                .addToBackStack(tag)
                .commit();
    }
}
