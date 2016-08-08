package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.QuestionCategoriesAdapter;
import com.drojj.javatests.animations.DetailsTransition;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionCategories extends Fragment implements QuestionCategoriesAdapter.CategoryClickListener {

    private TestDatabase mDatabase;

    @BindView(R.id.list_of_items)
    RecyclerView listView;

    private Unbinder unbinder;

    private ArrayList<Category> mList;

    public static InterviewQuestionCategories newInstance() {
        return new InterviewQuestionCategories();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = TestDatabase.getInstance(getActivity());
        mList = mDatabase.getQuestionCategories();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        unbinder = ButterKnife.bind(this, view);

        listView.setAdapter(new QuestionCategoriesAdapter(mList,this));
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        getActivity().setTitle(getActivity().getString(R.string.title_question_categories));
    }

    @Override
    public void onCategoryClicked(QuestionCategoriesAdapter.CategoryViewHolder holder, int position) {

        Fragment fragment = InterviewQuestionList.newInstance(mList.get(position));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(new DetailsTransition());
            fragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            fragment.setSharedElementReturnTransition(new DetailsTransition());

            getActivity().getFragmentManager()
                    .beginTransaction()
                    .addSharedElement(holder.image, "kittenImage")
                    .replace(R.id.fragment_container_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }


    }
}
