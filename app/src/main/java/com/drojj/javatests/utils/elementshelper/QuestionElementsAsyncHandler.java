package com.drojj.javatests.utils.elementshelper;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.List;

public class QuestionElementsAsyncHandler extends AsyncTask<String, QuestionElement, List<QuestionElement>> {

    private final LinearLayout mView;

    private final ProgressBar mProgressBar;

    private final Context mCtx;

    public QuestionElementsAsyncHandler(LinearLayout mView, ProgressBar mProgressBar) {
        this.mView = mView;
        this.mProgressBar = mProgressBar;
        mCtx = mView.getContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(QuestionElement... values) {
        super.onProgressUpdate(values);
        View v = values[0].getView(mView.getContext());
        mView.addView(v);
        mView.invalidate();
    }

    @Override
    protected List<QuestionElement> doInBackground(String... strings) {
        QuestionElements helper = new QuestionElements(mCtx, strings[0].split("!split!"));
        for (QuestionElement element : helper.getElements()) {
            publishProgress(element);
        }
        return helper.getElements();
    }

    @Override
    protected void onPostExecute(List<QuestionElement> questionElements) {
        super.onPostExecute(questionElements);
        mProgressBar.setVisibility(View.GONE);
    }
}
