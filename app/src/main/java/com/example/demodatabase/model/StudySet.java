package com.example.demodatabase.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StudySet {
    private Date date;
    private String user;
    private String description;
    private String studySetName;
    private String displayName;
    private String imageUri;
    private ArrayList<Term> terms;

    @Exclude
    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<Term> terms) {
        this.terms = terms;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public StudySet(Date date, String user, String description, String studySetName, String displayName) {
        this.date = date;
        this.user = user;
        this.description = description;
        this.studySetName = studySetName;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public StudySet() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStudySetName() {
        return studySetName;
    }

    public void setStudySetName(String studySetName) {
        this.studySetName = studySetName;
    }
}
