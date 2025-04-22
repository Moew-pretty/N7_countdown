package com.example.n7_countdown.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.n7_countdown.dto.EventDTO;
import com.example.n7_countdown.models.ReminderTimes;
import com.example.n7_countdown.models.TimeEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TimeEventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 8;
    public static final String TABLE_NAME = "time_events";

    public TimeEventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userEmail TEXT," +
                "name TEXT," +
                "timestampMillis INTEGER," +
                "location TEXT," +
                "note TEXT," +
                "isReminder INTEGER," +
                "eventType TEXT," +
                "color INTEGER," +
                "createdAt INTEGER," +
                "imageUri TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_REMINDER_TABLE = "CREATE TABLE reminder_times (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "eventId INTEGER," +
                "timeMillis INTEGER," +
                "label TEXT," +
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

    public TimeEvent insertEvent(EventDTO event, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userEmail", userEmail);
        values.put("name", event.getName());
        values.put("timestampMillis", event.getTimestampMillis());
        values.put("location", event.getLocation());
        values.put("note", event.getNote());
        values.put("isReminder", event.isReminder() ? 1 : 0);
        values.put("eventType", event.getEventType());
        values.put("color", event.getColor());
        values.put("createdAt", System.currentTimeMillis());
        values.put("imageUri", event.getImageUri());

        long eventId = db.insert(TABLE_NAME, null, values);
        insertReminderTimes((int) eventId, event.getReminderTimes());

        return getEventById((int) eventId);
    }

    public List<TimeEvent> getAllEvents(String userEmail) {
        List<TimeEvent> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM time_events WHERE userEmail = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { userEmail });

        if (cursor.moveToFirst()) {
            do {
                TimeEvent event = new TimeEvent();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                event.setTimestampMillis(cursor.getLong(cursor.getColumnIndexOrThrow("timestampMillis")));
                event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow("isReminder")) == 1);
                event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")));
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
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
            event.setEventType(cursor.getString(cursor.getColumnIndexOrThrow("eventType")));
            event.setColor(cursor.getInt(cursor.getColumnIndexOrThrow("color")));
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
        deleteReminderTimesByEventId(id);
        db.close();
    }

    public TimeEvent updateEvent(TimeEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (event.getUserEmail() != null) {
            values.put("userEmail", event.getUserEmail());
        }
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
        if (event.getImageUri() != null) {
            values.put("imageUri", event.getImageUri());
        }

        if (event.getEventType() != null) {
            values.put("eventType", event.getEventType());
        }

        if (event.getColor() != -1) {
            values.put("color", event.getColor());
        }

        if (event.isReminder()) {
            values.put("isReminder", 1);
        }

        if (values.size() > 0) {
            db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(event.getId())});
        }

        db.close();
        return getEventById(event.getId());
    }

    public void updateAllNoneUserEvent(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userEmail", email);

        db.update(TABLE_NAME, values, "userEmail = ?", new String[] { "none" });
    }



    // Event Remind Times CRUD
    public void insertReminderTimes(int eventId, Set<ReminderTimes> reminders) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (ReminderTimes reminder : reminders) {
            ContentValues values = new ContentValues();
            values.put("eventId", eventId);
            values.put("timeMillis", reminder.getTimeMillis());
            values.put("label", reminder.getLabel());
            db.insert("reminder_times", null, values);
        }
    }

    public Set<ReminderTimes> getReminderTimesByEventId(int eventId) {
        Set<ReminderTimes> times = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reminder_times WHERE eventId = ?", new String[]{String.valueOf(eventId)});
        while (cursor.moveToNext()) {
            ReminderTimes time = new ReminderTimes();
            time.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            time.setEventId(cursor.getInt(cursor.getColumnIndexOrThrow("eventId")));
            time.setTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow("timeMillis")));
            time.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));

            times.add(time);
        }
        cursor.close();
        return times;
    }

    public void deleteReminderTimesByEventId(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminder_times", "eventId = ?", new String[]{String.valueOf(eventId)});
    }

    public void updateReminderTimes(int eventId, Set<ReminderTimes> newTimes) {
        Set<ReminderTimes> existingTimes = getReminderTimesByEventId(eventId);
        SQLiteDatabase db = this.getWritableDatabase();

        // Thêm mới nếu chưa có
        for (ReminderTimes newTime : newTimes) {
            if (!existingTimes.contains(newTime)) {
                ContentValues values = new ContentValues();
                values.put("eventId", eventId);
                values.put("timeMillis", newTime.getTimeMillis());
                values.put("label", newTime.getLabel()); // nếu có dùng label
                db.insert("reminder_times", null, values);
            }
        }

        // Xoá những cái không còn tồn tại
        for (ReminderTimes existing : existingTimes) {
            if (!newTimes.contains(existing)) {
                db.delete("reminder_times", "eventId = ? AND timeMillis = ?", new String[]{
                        String.valueOf(eventId),
                        String.valueOf(existing.getTimeMillis())
                });
            }
        }
    }



}

