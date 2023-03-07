package com.example.demodatabase.model;

public class User {
    private String displayName;
    private String password;
    private String email;
    private boolean isGoogleAccount;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGoogleAccount() {
        return isGoogleAccount;
    }

    public void setGoogleAccount(boolean googleAccount) {
        isGoogleAccount = googleAccount;
    }
}
