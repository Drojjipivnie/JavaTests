package com.drojj.javatests.model.question;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Question implements Parcelable {

    private final int mId;
    private final int testId;
    private final String mQuestionText;
    private final List<Answer> mAnswers;
    private String mCode = "";
    private String mExplanation = "";
    private int mChosenAnswer = -1;
    private int mChosenRealIndex = -1;

    private boolean wasRightAnswered = true;

    public Question(int id, String questionText, List<Answer> answers, String code, String explanation, int testId) {
        this.mQuestionText = questionText;
        this.mId = id;
        this.mAnswers = answers;

        if (code != null) {
            this.mCode = code;
        }
        if (explanation != null) {
            this.mExplanation = explanation;
        }
        this.testId = testId;
    }


    protected Question(Parcel in) {
        mId = in.readInt();
        testId = in.readInt();
        mQuestionText = in.readString();
        mAnswers = in.createTypedArrayList(Answer.CREATOR);
        mCode = in.readString();
        mExplanation = in.readString();
        mChosenAnswer = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public List<Answer> getAnswers() {
        return mAnswers;
    }

    public String getExplanation() {
        return mExplanation;
    }

    public String getCode() {
        return mCode;
    }

    public void setRightAnswered(boolean flag) {
        wasRightAnswered = flag;
    }

    public boolean isRightAnswered() {
        return wasRightAnswered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(testId);
        dest.writeString(mQuestionText);
        dest.writeParcelableArray(mAnswers.toArray(new Answer[mAnswers.size()]), 0);
        dest.writeString(mCode);
        dest.writeString(mExplanation);
        dest.writeInt(mChosenAnswer);
    }

    public void setChosenAnswer(int chosenAnswer) {
        this.mChosenAnswer = chosenAnswer;
        this.mChosenRealIndex = mAnswers.get(chosenAnswer).getOrderCount();
    }

    public int getChosenAnswer() {
        return mChosenAnswer;
    }

    public int getChosenRealIndex() {
        return mChosenRealIndex;
    }

    public int getTestId() {
        return testId;
    }
}
