package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.InterviewQuestionListAdapter;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.model.Category;
import com.drojj.javatests.model.InterviewQuestion;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionList extends Fragment {

    private TestDatabase mDatabase;

    @BindView(R.id.list_of_categories)
    ListView listView;

    private Unbinder unbinder;

    private InterviewQuestionListAdapter mAdapter;

    private ArrayList<InterviewQuestion> mList;

    private Category mCategory;

    public static InterviewQuestionList newInstance(Category category){
        InterviewQuestionList fragment = new InterviewQuestionList();
        Bundle bundle = new Bundle();
        bundle.putSerializable("category",category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategory = (Category)getArguments().getSerializable("category");

        mDatabase = TestDatabase.getInstance(getActivity());
        mList = mDatabase.getInterviewQuestions(mCategory.getId());
        mAdapter = new InterviewQuestionListAdapter(getActivity(),mList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories_questions, container, false);

        unbinder = ButterKnife.bind(this, view);

        listView.setAdapter(mAdapter);

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
        getActivity().setTitle(mCategory.getTitle());
    }

}
