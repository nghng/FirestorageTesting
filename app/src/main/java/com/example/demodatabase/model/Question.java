package com.example.demodatabase.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionID;
    private Term term;
    private String termID;
    private Boolean isAnswerRight;
    private ArrayList<Answer> answers;


    public static   ArrayList<Question> createQuestions(ArrayList<Term> terms) {
        ArrayList<Question> questions = new ArrayList<>();
        for (Term t : terms
        ) {
            Question q = new Question(t);
            questions.add(q);
        }
        return questions;
    }
    @Exclude
    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    @Exclude
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
    public Question(Term term) {
        termID = term.getTermID();
        this.isAnswerRight = false;
    }

    public Question() {
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public Boolean getAnswerRight() {
        return isAnswerRight;
    }

    public void setAnswerRight(Boolean answerRight) {
        isAnswerRight = answerRight;
    }

    @Exclude
    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
