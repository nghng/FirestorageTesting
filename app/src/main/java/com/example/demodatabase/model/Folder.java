package com.example.demodatabase.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class Folder {
    private String folderID, folderName, folderDescription;
    private ArrayList<StudySet> studysets;
    private String user;
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Exclude
    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    @Exclude
    public ArrayList<StudySet> getStudysets() {
        return studysets;
    }

    public void setStudysets(ArrayList<StudySet> studysets) {
        this.studysets = studysets;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderDescription() {
        return folderDescription;
    }

    public void setFolderDescription(String folderDescription) {
        this.folderDescription = folderDescription;
    }

    public Folder(String folderName, String folderDescription, String user, String displayName) {
        this.folderName = folderName;
        this.folderDescription = folderDescription;
        this.user = user;
        this.displayName=displayName;
    }

    public Folder() {
    }
}
