package com.example.n7_countdown.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.n7_countdown.models.TimeEvent;

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
}

