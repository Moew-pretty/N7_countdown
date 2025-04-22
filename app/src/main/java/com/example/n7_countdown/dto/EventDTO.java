package com.example.n7_countdown.dto;

import com.example.n7_countdown.models.ReminderTimes;

import java.util.HashSet;
import java.util.Set;

public class EventDTO {
    private int id;
    private String userEmail;
    private String name;
    private long timestampMillis;
    private String location;
    private String note;
    private boolean isReminder;
    private Set<ReminderTimes> reminderTimes = new HashSet<>();
    private String eventType;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public Set<ReminderTimes> getReminderTimes() {
        return reminderTimes;
    }

    public void setReminderTimes(Set<ReminderTimes> reminderTimes) {
        this.reminderTimes = reminderTimes;
    }

}
