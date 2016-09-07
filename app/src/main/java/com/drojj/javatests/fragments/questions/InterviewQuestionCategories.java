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
import com.drojj.javatests.database.TestDatabase;
import com.drojj.javatests.fragments.BaseFragment;
import com.drojj.javatests.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InterviewQuestionCategories extends BaseFragment implements QuestionCategoriesAdapter.CategoryClickListener {

    @BindView(R.id.list_of_items)
    RecyclerView listView;

    private List<Category> mList;

    private QuestionCategoriesAdapter mAdapter;

    public static InterviewQuestionCategories newInstance() {
        return new InterviewQuestionCategories();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mList = TestDatabase.getInstance(getActivity()).getQuestionCategories();

        mAdapter = new QuestionCategoriesAdapter(mList, this);
    }

    @Override
    protected String setTitle() {
        return getActivity().getString(R.string.title_question_categories);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
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
        } else {
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_from_right_to_left, R.animator.slide_out_from_right_to_left, R.animator.slide_in_from_left_to_right, R.animator.slide_out_from_left_to_right)
                    .replace(R.id.fragment_container_main, fragment)
                    .addToBackStack(null)
                    .commit();
        }


    }
}
