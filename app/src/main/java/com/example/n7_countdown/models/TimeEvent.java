package com.example.n7_countdown.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TimeEvent {
    private int id;
    private int userId;
    private String name;
    private LocalDateTime timestamp;       // Nhập và hiển thị dễ (cá nhân thấy là vậy :)))
    private long timestampMillis;         // Dạng milliseconds
    private String location;
    private String note;
    private boolean isReminder;         // Có bật thông báo hay không
    private Set<ReminderTimes> reminderTimes = new HashSet<>();    // Thời điểm nhắc nhở (nếu có)
    private String subject;             // Môn học / học phần
    private int color;
    private boolean isCountUp;          // true = Đếm ngược, false = Đếm thời gian đã qua
    private long createdAt;
    private String imageUri;

    public TimeEvent() {

    }

    public TimeEvent(int id, int userId, String name, LocalDateTime timestamp, long timestampMillis, String location, String note, boolean isReminder, Set<ReminderTimes> reminderTimes, String subject, int color, boolean isCountUp, long createdAt, String imageUri) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.timestamp = timestamp;
        this.timestampMillis = timestampMillis;
        this.location = location;
        this.note = note;
        this.isReminder = isReminder;
        this.reminderTimes = reminderTimes;
        this.subject = subject;
        this.color = color;
        this.isCountUp = isCountUp;
        this.createdAt = createdAt;
        this.imageUri = imageUri;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isCountUp() {
        return isCountUp;
    }

    public void setCountUp(boolean countUp) {
        isCountUp = countUp;
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

