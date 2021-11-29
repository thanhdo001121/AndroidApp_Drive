package com.example.firebase;

public class User {
    String id;
    String email;
    String pwd;

    public User(String id, String email, String pwd) {
        this.id = id;
        this.email = email;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }
}
