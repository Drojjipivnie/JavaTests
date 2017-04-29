package com.drojj.javatests.fragments.tests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.customviews.MyChart;
import com.drojj.javatests.database.FirebaseDatabaseUtils;
import com.drojj.javatests.fragments.BaseFragment;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.TestEntryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestProgressFragment extends BaseFragment {

    private static final int MIN_ENTRIES_TO_SHOW_CHART = 3;

    private Test mTest;

    private DatabaseReference mReference;

    @BindView(R.id.progress_clear_button)
    Button mClearButton;

    @BindView(R.id.chart)
    MyChart mChart;

    @BindView(R.id.progress_average_score)
    TextView mAverageScore;

    private final ValueEventListener mListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            updateFragment(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static TestProgressFragment newInstance(Test test) {
        TestProgressFragment fragment = new TestProgressFragment();

        Bundle args = new Bundle();
        args.putParcelable("test", test);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTest = getArguments().getParcelable("test");

        mReference = FirebaseDatabaseUtils.getTestEntriesReference(mTest.id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_progress, container, false);

        mUnbinder = ButterKnife.bind(this, v);

        mChart.setDescription(mTest.name);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mReference.addValueEventListener(mListener);
    }

    @Override
    protected String setTitle() {
        return "Статистика: " + mTest.name;
    }

    @Override
    public void onStop() {
        super.onStop();
        mReference.removeEventListener(mListener);
    }

    @OnClick(R.id.progress_clear_button)
    public void onClick(View v) {
        clearUserTestHistory();
    }

    private void clearUserTestHistory() {

        //TODO:Tsk completion of clearing

        mLogger.clickClearStatistics(mTest.name, mTest.progress, mChart.getAverageScore("%.1f"));

        FirebaseDatabaseUtils.clearUserTestHistory(mTest.id, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Статистика не удалена. Что - то пошло не так.", Toast.LENGTH_SHORT).show();
            }
        }, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mChart.clear();
                Toast.makeText(getActivity(), "Статистика удалена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFragment(DataSnapshot dataSnapshot) {
        List<TestEntryModel> entries = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            TestEntryModel entry = snapshot.getValue(TestEntryModel.class);
            entries.add(entry);
        }

        if (entries.size() < MIN_ENTRIES_TO_SHOW_CHART) {
            mAverageScore.setText("Пройдите тест минимум 3 раза");
            mClearButton.setEnabled(false);
        } else {
            mChart.updateData(entries);
            mChart.invalidate();
            mAverageScore.setText(mChart.getAverageScore("%.1f"));
            mClearButton.setEnabled(true);
        }
    }
}
