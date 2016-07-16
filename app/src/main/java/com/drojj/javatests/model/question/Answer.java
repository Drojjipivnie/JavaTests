package com.drojj.javatests.model.question;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer implements Parcelable{

    private String mAnswerText;
    private boolean mIsAnswerRight = false;

    public Answer(String text) {
        this.mAnswerText = text;
    }

    protected Answer(Parcel in) {
        mAnswerText = in.readString();
        mIsAnswerRight = in.readByte() != 0;
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    public String getAnswerText() {
        return mAnswerText;
    }

    public void setThisAnswerRight() {
        mIsAnswerRight = true;
    }

    public boolean isAnswerRight() {
        return mIsAnswerRight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAnswerText);
        dest.writeByte((byte) (mIsAnswerRight ? 1 : 0));
    }
}
