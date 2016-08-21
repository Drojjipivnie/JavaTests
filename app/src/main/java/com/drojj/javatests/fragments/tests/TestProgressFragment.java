package com.drojj.javatests.fragments.tests;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.drojj.javatests.R;
import com.drojj.javatests.fragments.BaseFragment;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.TestEntryModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestProgressFragment extends BaseFragment {

    private Test mTest;

    private ArrayList<TestEntryModel> mEntrys;

    private DatabaseReference mReference;

    private ValueEventListener mListener;

    @BindView(R.id.chart)
    LineChart mChart;

    @BindView(R.id.progress_average_score)
    TextView mAverageScore;

    public static TestProgressFragment newInstance(Test test) {
        TestProgressFragment dialog = new TestProgressFragment();

        Bundle args = new Bundle();
        args.putParcelable("test", test);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTest = getArguments().getParcelable("test");
        mEntrys = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("tests_entrys").child("test" + String.valueOf(mTest.id));

        initListener();

        mToolbarTitle = mTest.name;
    }

    private void initListener() {
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEntrys.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestEntryModel entry = snapshot.getValue(TestEntryModel.class);
                    mEntrys.add(entry);
                }

                if (mEntrys.size() < 2) {
                    Toast.makeText(getActivity(), "Пройдите тест минимум 2 раза", Toast.LENGTH_SHORT).show();
                } else {
                    setData();
                    mAverageScore.setText(String.valueOf(getAverageScore()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private float getAverageScore() {

        int score = 0;

        for(TestEntryModel entry: mEntrys){
            score += entry.score;
        }

        return (float)score/mEntrys.size();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_progress, container, false);

        mUnbinder = ButterKnife.bind(this, v);

        initChart();
        return v;
    }

    private void initChart() {
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDescriptionColor(Color.BLACK);

        mChart.setDescription(mTest.name);


        //mChart.setTouchEnabled(true);

        //mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis x = mChart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setTextColor(Color.BLACK);

        x.setDrawAxisLine(true);
        x.setDrawGridLines(false);

        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(10, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setAxisLineColor(Color.BLACK);
    }

    private String formatData(long time) {
        Date date = new Date(time);

        Locale locale = new Locale("ru", "RU");
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);

        return df.format(date);
    }

    private void setData() {

        ArrayList<String> xVals = new ArrayList<>();
        for (TestEntryModel entry : mEntrys) {
            xVals.add(formatData(entry.time));
        }

        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < mEntrys.size(); i++) {

            yVals.add(new Entry(mEntrys.get(i).score, i));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals);
            mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "Кол-во правильных ответов");
            set1.setDrawCubic(true);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setFillColor(R.color.colorPrimaryDark);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.0f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.BLACK);
            set1.setColor(Color.BLACK);
            set1.setFillAlpha(50);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setDrawVerticalHighlightIndicator(false);
            set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(xVals, set1);
            data.setValueTextSize(9f);
            if (mEntrys.size() < 20) {
                data.setDrawValues(true);
            } else {
                data.setDrawValues(false);
            }
            // set data
            mChart.setData(data);
        }
        mChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mReference.addValueEventListener(mListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mReference.removeEventListener(mListener);
    }
}
