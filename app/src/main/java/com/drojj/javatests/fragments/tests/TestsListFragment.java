package com.drojj.javatests.fragments.tests;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drojj.javatests.R;
import com.drojj.javatests.adapters.TestsAdapter;
import com.drojj.javatests.database.tests.TestDatabase;
import com.drojj.javatests.model.Test;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TestsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.test_swipe_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.tests_recyclerview)
    RecyclerView recyclerView;

    private TestsAdapter mAdapter;

    private Unbinder unbinder;

    private TestDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = TestDatabase.getInstance(getActivity());
        initAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRefreshLayout.setOnRefreshListener(this);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void initAdapter(){
        mAdapter = new TestsAdapter();
        mAdapter.setOnClick(new TestsAdapter.ItemClickListener() {
            @Override
            public void onClick(Test test) {
                showDialog(test);
            }
        });
        mAdapter.setOnFinishLoadListener(new TestsAdapter.OnStartEndLoad() {
            @Override
            public void onStart() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mRefreshLayout.setRefreshing(true);
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });

        mAdapter.addAll(mDatabase.getTests());
        mAdapter.updateTestInfo(true);
    }


    private void showDialog(Test test) {
        TestInfoDialog dialog = TestInfoDialog.newInstance(test);
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getActivity().getString(R.string.title_tests_list));
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        mAdapter.updateTestInfo(false);
    }
}
