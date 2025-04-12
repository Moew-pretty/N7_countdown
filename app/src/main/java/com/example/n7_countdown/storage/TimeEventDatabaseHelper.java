package com.example.n7_countdown.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.n7_countdown.models.TimeEvent;

import java.util.ArrayList;
import java.util.List;


public class TimeEventDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "time_events";

    public TimeEventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
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
                "imageUri TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertEvent(TimeEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", event.getName());
        values.put("timestampMillis", event.getTimestampMillis());
        values.put("location", event.getLocation());
        values.put("note", event.getNote());
        values.put("isReminder", event.isReminder() ? 1 : 0);
        values.put("reminderTimeMillis", event.getReminderTimeMillis());
        values.put("subject", event.getSubject());
        values.put("color", event.getColor());
        values.put("isCountUp", event.isCountUp() ? 1 : 0);
        values.put("createdAt", event.getCreatedAt());
        values.put("imageUri", event.getImageUri());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<TimeEvent> getAllEvents() {
        List<TimeEvent> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                TimeEvent event = new TimeEvent();
                event.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                event.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                event.setTimestampMillis(cursor.getLong(cursor.getColumnIndexOrThrow("timestampMillis")));
                event.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow("isReminder")) == 1);
                event.setReminderTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow("reminderTimeMillis")));
                event.setCountUp(cursor.getInt(cursor.getColumnIndexOrThrow("isCountUp")) == 1);
                event.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("createdAt")));
                eventList.add(event);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return eventList;
    }

    public TimeEvent getEvent(int id) {
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
            event.setReminderTimeMillis(cursor.getLong(cursor.getColumnIndexOrThrow("reminderTimeMillis")));
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

}

