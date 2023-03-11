package com.example.demodatabase.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Learn {
    private List terms;
    private ArrayList<String> termForQuestion = new ArrayList<>();
    private ArrayList<String> definitionForQuestion = new ArrayList<>();
    private ArrayList<String> correctAnswers = new ArrayList<>();
    private String userEmail;
    private String studySetID;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStudySetID() {
        return studySetID;
    }

    public void setStudySetID(String studySetID) {
        this.studySetID = studySetID;
    }

    public Learn() {
    }

    @Exclude
    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    @Exclude

    public ArrayList<String> getTermForQuestion() {
        return termForQuestion;
    }

    public void setTermForQuestion(ArrayList<String> termForQuestion) {
        this.termForQuestion = termForQuestion;
    }

    @Exclude

    public ArrayList<String> getDefinitionForQuestion() {
        return definitionForQuestion;
    }

    public void setDefinitionForQuestion(ArrayList<String> definitionForQuestion) {
        this.definitionForQuestion = definitionForQuestion;
    }

    @Exclude
    public ArrayList<String> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(ArrayList<String> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Learn(ArrayList<Term> terms) {
        this.terms = terms;
        for (Term t : terms
        ) {
            termForQuestion.add(t.getTerm());
            definitionForQuestion.add(t.getDefinition());
        }
    }


}
