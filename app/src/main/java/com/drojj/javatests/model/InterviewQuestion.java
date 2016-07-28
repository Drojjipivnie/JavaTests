package com.drojj.javatests.model;

import java.io.Serializable;

public class InterviewQuestion implements Serializable {

    private String mQuestion;

    private String mAnswer;

    public InterviewQuestion(String mQuestion, String mAnswer) {
        this.mQuestion = mQuestion;
        this.mAnswer = mAnswer;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }
}
