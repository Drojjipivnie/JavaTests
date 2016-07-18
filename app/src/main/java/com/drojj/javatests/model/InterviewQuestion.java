package com.drojj.javatests.model;

public class InterviewQuestion {

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
