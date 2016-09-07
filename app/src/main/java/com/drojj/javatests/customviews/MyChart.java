package com.drojj.javatests.customviews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.drojj.javatests.R;
import com.drojj.javatests.model.TestEntryModel;
import com.drojj.javatests.utils.FormatUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MyChart extends LineChart {

    private final List<TestEntryModel> mEntries = new ArrayList<>();

    public MyChart(Context context) {
        super(context);
        initChart();
    }

    public MyChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChart();
    }

    public MyChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initChart();
    }

    @Override
    public void invalidate() {
        if (!mEntries.isEmpty()) {
            setData();
        }
        super.invalidate();
    }

    @Override
    public void clear() {
        clearData();
        super.clear();
    }

    public void updateData(List<TestEntryModel> entries) {
        clearData();
        mEntries.addAll(entries);
    }

    public String getAverageScore(String format) {
        int score = 0;

        for (TestEntryModel entry : mEntries) {
            score += entry.score;
        }

        return String.format(format, (float) score / mEntries.size());
    }

    private void initChart() {
        this.setBackgroundColor(Color.WHITE);
        this.setDescriptionColor(Color.BLACK);
        this.setScaleEnabled(false);
        this.setPinchZoom(false);
        this.setDrawGridBackground(false);

        XAxis x = this.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setTextColor(Color.BLACK);
        x.setDrawAxisLine(true);
        x.setDrawGridLines(false);

        YAxis y = this.getAxisLeft();
        y.setLabelCount(10, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setAxisLineColor(Color.BLACK);
    }

    private void clearData() {
        mEntries.clear();
    }

    private void setData() {
        List<String> xValues = getXValues();
        List<Entry> yValues = getYValues();

        LineDataSet set1;

        if (this.getData() != null && this.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) this.getData().getDataSetByIndex(0);
            set1.setYVals(yValues);
            this.getData().setXVals(xValues);
            this.getData().notifyDataChanged();
            this.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yValues, "Кол-во правильных ответов");
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

            LineData data = new LineData(xValues, set1);
            data.setValueTextSize(9f);
            if (mEntries.size() < 10) {
                data.setDrawValues(true);
            } else {
                data.setDrawValues(false);
            }
            this.setData(data);
        }
    }

    private List<String> getXValues() {
        List<String> xValues = new ArrayList<>();
        for (TestEntryModel entry : mEntries) {
            xValues.add(FormatUtils.formatData(entry.time));
        }
        return xValues;
    }

    private List<Entry> getYValues() {
        List<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < mEntries.size(); i++) {
            yValues.add(new Entry(mEntries.get(i).score, i));
        }
        return yValues;
    }

}
