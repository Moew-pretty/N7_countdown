package com.example.n7_countdown.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.n7_countdown.dto.EventDTO;
import com.example.n7_countdown.models.TimeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TimeEventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "time_events";

    public TimeEventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER," +
                "name TEXT," +
                "timestampMillis INTEGER," +
                "location TEXT," +
                "note TEXT," +
                "isReminder INTEGER," +
                "reminderTimeMillis INTEGER," +
                "subject TEXT," +
                "color INTEGER," +
                "isCountUp INTEGER," +
                "createdAt INTEGER," +
                "imageUri TEXT," +
                "FOREIGN KEY(userId) REFERENCES User(id)" +
                ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_REMINDER_TABLE = "CREATE TABLE reminder_times (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "eventId INTEGER," +
                "timeMillis INTEGER," +
                "FOREIGN KEY(eventId) REFERENCES time_events(id) ON DELETE CASCADE" +
                ")";
        db.execSQL(CREATE_REMINDER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS reminder_times");
        db.execSQL("PRAGMA foreign_keys=ON");
        onCreate(db);
    }

    public void insertEvent(EventDTO event, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userId", userId);
        values.put("name", event.getName());
        values.put("timestampMillis", event.getTimestampMillis());
        values.put("location", event.getLocation());
        values.put("note", event.getNote());
        values.put("isReminder", event.isReminder() ? 1 : 0);
        values.put("subject", event.getSubject());
        values.put("color", event.getColor());
        values.put("createdAt", System.currentTimeMillis());
        values.put("imageUri", event.getImageUri());

        long eventId = db.insert(TABLE_NAME, null, values);
        insertReminderTimes((int) eventId, event.getReminderTimeMillis());

        db.close();
    }

    public List<TimeEvent> getAllEvents(int userId) {
        List<TimeEvent> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where userId=" + userId, null);

        if (cursor.moveToFirst()) {
            do {
                TimeEvent event = new TimeEvent();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                event.setTimestampMillis(cursor.getLong(cursor.getColumnIndexOrThrow("timestampMillis")));
                event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow("isReminder")) == 1);
                event.setCountUp(cursor.getInt(cursor.getColumnIndexOrThrow("isCountUp")) == 1);
                event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")));
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return eventList;
    }

    public TimeEvent getEventById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " where id=" + id, null);

        TimeEvent event = new TimeEvent();

        if (cursor.moveToFirst()) {
            event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            event.setTimestampMillis(cursor.getLong(cursor.getColumnIndexOrThrow("timestampMillis")));
            event.setLocation(cursor.getString(cursor.getColumnIndexOrThrow("location")));
            event.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
            event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow("isReminder")) == 1);
            event.setSubject(cursor.getString(cursor.getColumnIndexOrThrow("subject")));
            event.setColor(cursor.getInt(cursor.getColumnIndexOrThrow("color")));
            event.setCountUp(cursor.getInt(cursor.getColumnIndexOrThrow("isCountUp")) == 1);
            event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")));
            event.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow("imageUri")));
        } else {
            throw new IllegalStateException("No event found with id: " + id);
        }

        cursor.close();
        db.close();
        return event;
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE id = ?", new Object[]{id});
        db.close();
    }

    public void updateEvent(TimeEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (event.getName() != null) {
            values.put("name", event.getName());
        }
        if (event.getTimestampMillis() != 0) {
            values.put("timestampMillis", event.getTimestampMillis());
        }
        if (event.getLocation() != null) {
            values.put("location", event.getLocation());
        }
        if (event.getNote() != null) {
            values.put("note", event.getNote());
        }
        if (event.getSubject() != null) {
            values.put("subject", event.getSubject());
        }
        if (event.getImageUri() != null) {
            values.put("imageUri", event.getImageUri());
        }

        if (event.getColor() != -1) {
            values.put("color", event.getColor());
        }

        if (event.isReminder()) {
            values.put("isReminder", 1);
        }
        if (event.isCountUp()) {
            values.put("isCountUp", 1);
        }

        if (values.size() > 0) {
            db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(event.getId())});
        }

        db.close();
    }




    // Event Remind Times CRUD
    public void insertReminderTimes(int eventId, Set<Long> times) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Long time : times) {
            ContentValues values = new ContentValues();
            values.put("eventId", eventId);
            values.put("timeMillis", time);
            db.insert("reminder_times", null, values);
        }
    }

    public Set<Long> getReminderTimesByEventId(int eventId) {
        Set<Long> times = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT timeMillis FROM reminder_times WHERE eventId = ?", new String[]{String.valueOf(eventId)});
        while (cursor.moveToNext()) {
            times.add(cursor.getLong(cursor.getColumnIndexOrThrow("timeMillis")));
        }
        cursor.close();
        return times;
    }

    public void deleteReminderTimesByEventId(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminder_times", "eventId = ?", new String[]{String.valueOf(eventId)});
    }


}

