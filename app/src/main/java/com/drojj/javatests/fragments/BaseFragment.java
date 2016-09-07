package com.drojj.javatests.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;

import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected final YandexAnalyticsLogger mLogger = YandexAnalyticsLogger.getInstance();

    protected Unbinder mUnbinder;

    private String mToolbarTitle;

    @Override
    public void onStart() {
        super.onStart();
        mToolbarTitle = setTitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mToolbarTitle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    protected abstract String setTitle();
}
