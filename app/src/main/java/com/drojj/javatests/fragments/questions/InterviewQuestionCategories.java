package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.QuestionCategoriesAdapter;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionCategories extends Fragment {

    private TestDatabase mDatabase;

    @BindView(R.id.list_of_items)
    ListView listView;

    private Unbinder unbinder;

    private ArrayList<Category> mList;

    private QuestionCategoriesAdapter mAdapter;

    public static InterviewQuestionCategories newInstance() {
        return new InterviewQuestionCategories();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = TestDatabase.getInstance(getActivity());
        mList = mDatabase.getQuestionCategories();
        mAdapter = new QuestionCategoriesAdapter(getActivity(), mList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        unbinder = ButterKnife.bind(this, view);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showQuestions(view, i);
            }
        });

        return view;
    }

    //TODO:fragment transition
    private void showQuestions(View view, int i) {
        ImageView img = (ImageView) view.findViewById(R.id.category_image);

        InterviewQuestionList endFragment = InterviewQuestionList.newInstance(mList.get(i));

        FragmentManager fragmentManager = getFragmentManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans));
            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

            endFragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans));
            endFragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
            endFragment.setAllowEnterTransitionOverlap(false);
            endFragment.setAllowReturnTransitionOverlap(false);

            fragmentManager.beginTransaction().replace(R.id.fragment_container_main, endFragment)
                    .addToBackStack("question_list")
                    .addSharedElement(img, "transition_image")
                    .commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.fragment_container_main, endFragment)
                    .addToBackStack("question_list")
                    .commit();
        }
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
