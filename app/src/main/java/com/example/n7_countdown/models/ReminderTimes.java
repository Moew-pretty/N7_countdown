package com.example.n7_countdown.models;

import java.util.Objects;

public class ReminderTimes {
    private int id;
    private int eventId;
    private long timeMillis;
    private String label;

    public ReminderTimes() {}

    public ReminderTimes(int eventId, long timeMillis, String label) {
        this.eventId = eventId;
        this.timeMillis = timeMillis;
        this.label = label;
    }

    public ReminderTimes(long timeMillis, String label) {
        this.timeMillis = timeMillis;
        this.label = label;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ReminderTimes{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", timeMillis=" + timeMillis +
                ", label=" + label +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReminderTimes that = (ReminderTimes) o;
        return timeMillis == that.timeMillis && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeMillis, label);
    }

}
