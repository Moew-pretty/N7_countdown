package com.example.n7_countdown.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TimeEvent {
    private int id;
    private String userEmail;
    private String name;
    private LocalDateTime timestamp;       // Nhập và hiển thị dễ (cá nhân thấy là vậy :)))
    private long timestampMillis;         // Dạng milliseconds
    private String location;
    private String note;
    private boolean isReminder;         // Có bật thông báo hay không
    private Set<ReminderTimes> reminderTimes = new HashSet<>();    // Thời điểm nhắc nhở (nếu có)
    private String eventType;
    private int color;
    private long createdAt;
    private String imageUri;

    public TimeEvent() {

    }

    public TimeEvent(int id, String name, LocalDateTime timestamp, long timestampMillis, String location, String note, boolean isReminder, Set<ReminderTimes> reminderTimes, int color, long createdAt, String imageUri) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.timestampMillis = timestampMillis;
        this.location = location;
        this.note = note;
        this.isReminder = isReminder;
        this.reminderTimes = reminderTimes;
        this.color = color;
        this.createdAt = createdAt;
        this.imageUri = imageUri;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampMillis() {
        return timestampMillis;
    }

    public void setTimestampMillis(long timestampMillis) {
        this.timestampMillis = timestampMillis;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<ReminderTimes> getReminderTimes() {
        return reminderTimes;
    }

    public void setReminderTimes(Set<ReminderTimes> reminderTimes) {
        this.reminderTimes = reminderTimes;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}

