package com.example.n7_countdown;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.activities.AddEventActivity;
import com.example.n7_countdown.activities.BaseActivity;
import com.example.n7_countdown.activities.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigation(R.id.nav_home);

    }

}
