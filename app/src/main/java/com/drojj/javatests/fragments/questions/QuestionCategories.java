package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.QuestionCategoriesAdapter;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.events.OpenFragmentEvent;
import com.drojj.javatests.model.Category;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class QuestionCategories extends Fragment {

    private TestDatabase mDatabase;

    @BindView(R.id.list_of_categories)
    ListView listView;

    private Unbinder unbinder;

    private ArrayList<Category> mList;

    private QuestionCategoriesAdapter mAdapter;

    public static QuestionCategories newInstance() {
        return new QuestionCategories();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = TestDatabase.getInstance(getActivity());
        mList = mDatabase.getQuestionCategories();
        mAdapter = new QuestionCategoriesAdapter(getActivity(),mList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_questions, container, false);

        unbinder = ButterKnife.bind(this, view);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showQuestions(mList.get(i).getId());
            }
        });

        return view;
    }

    private void showQuestions(int categoryId){
        OpenFragmentEvent<Integer> event = new OpenFragmentEvent<>(OpenFragmentEvent.FragmentType.QUESTION_LIST,categoryId);
        EventBus.getDefault().post(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getActivity().getString(R.string.title_question_categories));
    }
}
