package com.example.n7_countdown.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.storage.UserDatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {
    private TimeEventDatabaseHelper timeDBHelper;
    private UserDatabaseHelper userDBHelper;
    public static SQLiteDatabase timeDebugDb;
    public static SQLiteDatabase userDebugDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tải ngôn ngữ đã lưu trước khi gọi super.onCreate
        loadLocale();
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("default", "Kênh chung", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Mở db liên tục để debug
        timeDBHelper = new TimeEventDatabaseHelper(this);
        timeDebugDb = timeDBHelper.getWritableDatabase();

        userDBHelper = new UserDatabaseHelper(this);
        userDebugDb = userDBHelper.getWritableDatabase();
    }

    protected void setupBottomNavigation(int selectedItemId) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(selectedItemId);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Class<?> currentClass = getClass();

            if (itemId == R.id.nav_home && !MainActivity.class.equals(currentClass)) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (itemId == R.id.nav_add_event && !AddEventActivity.class.equals(currentClass)) {
                startActivity(new Intent(this, AddEventActivity.class));
            } else if (itemId == R.id.nav_settings && !SettingsActivity.class.equals(currentClass)) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else {
                return false;
            }

            overridePendingTransition(0, 0);
            return true;
        });
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Mặc định là tiếng Việt
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}