package com.drojj.javatests.fragments;

import android.app.Fragment;

import com.drojj.javatests.utils.analytics.YandexAnalyticsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Unbinder;

public class BaseFragment extends Fragment {

    protected final YandexAnalyticsLogger mLogger = YandexAnalyticsLogger.getInstance();

    protected Unbinder mUnbinder;

    protected String mToolbarTitle;

    protected FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

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
}
