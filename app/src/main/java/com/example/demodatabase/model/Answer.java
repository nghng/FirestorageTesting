package com.example.demodatabase.model;

public class Answer {
    private boolean isRight;
    private String answer;

    public Answer(boolean isRight, String answer) {
        this.isRight = isRight;
        this.answer = answer;
    }

    public Answer() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }
}
