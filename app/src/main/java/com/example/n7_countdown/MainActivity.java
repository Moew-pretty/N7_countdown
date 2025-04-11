package com.example.n7_countdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.n7_countdown.activities.AddEventActivity;
import com.example.n7_countdown.activities.BaseActivity;
import com.example.n7_countdown.activities.LoginActivity;
import com.example.n7_countdown.activities.SettingsActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        setupBottomNavigation(R.id.nav_home);
    }
}