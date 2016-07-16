package com.drojj.javatests.model.question;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements Parcelable {
    private String mQuestionText;
    private ArrayList<Answer> mAnswers;
    private String mCode;
    private String mExplanation;
    private int mChosenAnswer = -1;

    private boolean wasRightAnswered = true;

    public Question(String questionText, ArrayList<Answer> answers, String code, String explanation) {
        this.mQuestionText = questionText;
        this.mAnswers = answers;

        if (code != null) {
            this.mCode = code;
        }
        if (explanation != null) {
            this.mExplanation = explanation;
        }
    }


    protected Question(Parcel in) {
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

    public String getQuestionText() {
        return mQuestionText;
    }

    public ArrayList<Answer> getAnswers() {
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

    public boolean isRightAnswered(){
        return wasRightAnswered;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuestionText);
        dest.writeParcelableArray(mAnswers.toArray(new Answer[mAnswers.size()]),0);
        dest.writeString(mCode);
        dest.writeString(mExplanation);
        dest.writeInt(mChosenAnswer);
    }

    public void setChosenAnswer(int chosenAnswer) {
        this.mChosenAnswer = chosenAnswer;
    }

    public int getChosenAnswer(){
        return mChosenAnswer;
    }
}
