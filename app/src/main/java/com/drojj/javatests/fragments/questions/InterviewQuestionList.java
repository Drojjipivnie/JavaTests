package com.drojj.javatests.fragments.questions;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.InterviewQuestionListAdapter;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.model.Category;
import com.drojj.javatests.model.InterviewQuestion;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterviewQuestionList extends Fragment {

    private TestDatabase mDatabase;

    @BindView(R.id.list_of_items)
    ListView listView;

    private Unbinder unbinder;

    private InterviewQuestionListAdapter mAdapter;

    private List<InterviewQuestion> mList;

    private Category mCategory;

    public static InterviewQuestionList newInstance(Category category) {
        InterviewQuestionList fragment = new InterviewQuestionList();
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategory = (Category) getArguments().getSerializable("category");

        mDatabase = TestDatabase.getInstance(getActivity());
        mList = mDatabase.getInterviewQuestions(mCategory.getId());
        mAdapter = new InterviewQuestionListAdapter(getActivity(), mList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);

        unbinder = ButterKnife.bind(this, view);

        listView.addHeaderView(createImageHeader(), null, false);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = InterviewQuestionAnswer.newInstance(mList.get(i - listView.getHeaderViewsCount()));
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_from_right_to_left, R.animator.slide_out_from_right_to_left, R.animator.slide_in_from_left_to_right, R.animator.slide_out_from_left_to_right)
                        .replace(R.id.fragment_container_main, fragment)
                        .addToBackStack("t")
                        .commit();
            }
        });

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

    //TODO:fragment transition
    private ImageView createImageHeader() {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageResource(mCategory.getImageResId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName("kittenImage");
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return imageView;
    }

}
