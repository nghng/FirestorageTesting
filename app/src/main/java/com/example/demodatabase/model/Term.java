package com.example.demodatabase.model;

import android.net.Uri;

import com.google.firebase.firestore.Exclude;

public class Term {
    private String termID;
    private String term;
    private String definition;
    private Uri image;

    @Exclude
    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public Term(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    public Term() {
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Exclude
    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Term{" +
                "term='" + term + '\'' +
                ", definition='" + definition + '\'' +
                ", image=" + image +
                '}';
    }
}
