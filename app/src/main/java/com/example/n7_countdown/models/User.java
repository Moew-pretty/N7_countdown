package com.example.n7_countdown.models;

public class User {
    private int id;
    private String email;
    private String groupName;

    public User(int id, String email, String groupName) {
        this.id = id;
        this.email = email;
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getGroupName() {
        return groupName;
    }
}
