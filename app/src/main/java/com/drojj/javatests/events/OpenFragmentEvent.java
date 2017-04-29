package com.drojj.javatests.events;

import android.app.Fragment;

import com.drojj.javatests.fragments.questions.InterviewQuestionCategories;
import com.drojj.javatests.fragments.questions.InterviewQuestionList;
import com.drojj.javatests.fragments.tests.TestListFragment;
import com.drojj.javatests.fragments.tests.TestProgressFragment;
import com.drojj.javatests.fragments.tests.TestQuizFragment;
import com.drojj.javatests.fragments.tests.TestResultsFragment;
import com.drojj.javatests.model.Category;
import com.drojj.javatests.model.Test;
import com.drojj.javatests.model.question.Question;

import java.util.ArrayList;

public class OpenFragmentEvent<T> {

    private T mData;

    private FragmentType mType;

    public enum FragmentType {
        TEST_LIST("test_list"),
        TEST_QUIZ("test_quiz"),
        TEST_RESULTS("test_results"),
        TEST_PROGRESS("test_progress"),
        QUESTION_CATEGORIES("question_categories"),
        QUESTION_LIST("question_list");

        private String mTag;

        FragmentType(String tag) {
            mTag = tag;
        }

        public String getTag() {
            return mTag;
        }
    }

    public OpenFragmentEvent(FragmentType type, T data) {
        mType = type;
        mData = data;
    }

    public T getData() {
        return mData;
    }

    public FragmentType getType() {
        return mType;
    }

    public Fragment getFragment() {
        switch (mType) {
            case TEST_LIST:
                return new TestListFragment();
            case TEST_QUIZ:
                return TestQuizFragment.newInstance((Test) mData);
            case TEST_RESULTS:
                return TestResultsFragment.newInstance((ArrayList<Question>) mData);
            case TEST_PROGRESS:
                return TestProgressFragment.newInstance((Test) mData);
            case QUESTION_CATEGORIES:
                return InterviewQuestionCategories.newInstance();
            case QUESTION_LIST:
                return InterviewQuestionList.newInstance((Category) mData);
            default:
                return null;
        }
    }
}
