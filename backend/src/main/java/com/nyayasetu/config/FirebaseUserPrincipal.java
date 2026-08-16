package com.nyayasetu.config;

import com.google.firebase.auth.FirebaseToken;

public class FirebaseUserPrincipal {
    
    private final String uid;
    private final String email;
    private final FirebaseToken token;

    public FirebaseUserPrincipal(String uid, String email, FirebaseToken token) {
        this.uid = uid;
        this.email = email;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public FirebaseToken getToken() {
        return token;
    }
}
