package com.example.n7_countdown.dto;

import java.util.HashSet;
import java.util.Set;

public class EventDTO {
    private int id;
    private int userId;
    private String name;
    private long timestampMillis;
    private String location;
    private String note;
    private boolean isReminder;
    private Set<Long> reminderTimeMillis = new HashSet<>();
    private String subject;
    private int color;
    private String imageUri;

    public EventDTO() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public void setTimestampMillis(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }

    public Set<Long> getReminderTimeMillis() {
        return reminderTimeMillis;
    }

    public void setReminderTimeMillis(Set<Long> reminderTimeMillis) {
        this.reminderTimeMillis = reminderTimeMillis;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


}
