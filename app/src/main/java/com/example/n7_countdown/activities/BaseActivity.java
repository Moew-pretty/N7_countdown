package com.example.n7_countdown.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

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
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
                if (isLoggedIn) {
                    startActivity(new Intent(this, SettingsActivity.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            } else {
                return false;
            }

            overridePendingTransition(0, 0);
            return true;
        });
    }
}