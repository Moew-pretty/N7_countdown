package com.example.n7_countdown.models;

public class ReminderTimes {
    private int id;
    private int eventId;
    private long timeMillis;

    public ReminderTimes() {}

    public ReminderTimes(int eventId, long timeMillis) {
        this.eventId = eventId;
        this.timeMillis = timeMillis;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }
}
