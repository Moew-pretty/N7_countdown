package com.example.n7_countdown.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;
import com.example.n7_countdown.models.User;
import com.example.n7_countdown.storage.UserDatabaseHelper;

public class SettingsActivity extends BaseActivity {
    private LinearLayout userInfoLayout;
    private TextView tvGroupName, tvEmail, tvError, tvRename, tvBackup, tvLogout;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userInfoLayout = findViewById(R.id.userInfoLayout);
        tvGroupName = findViewById(R.id.tvGroupName);
        tvEmail = findViewById(R.id.tvEmail);
        tvError = findViewById(R.id.tvError);
        tvRename = findViewById(R.id.tvRename);
        tvBackup = findViewById(R.id.tvBackup);
        tvLogout = findViewById(R.id.tvLogout);
        dbHelper = new UserDatabaseHelper(this);

        setupBottomNavigation(R.id.nav_settings);
        updateUI();
    }

    private void updateUI() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        String email = prefs.getString("email", null);

        if (isLoggedIn && email != null) {
            // Người dùng đã đăng nhập
            User user = dbHelper.getUser(email);
            if (user != null) {
                tvGroupName.setText(user.getGroupName());
                tvEmail.setText(user.getEmail());
                tvError.setVisibility(View.GONE);
            } else {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Không tìm thấy thông tin người dùng");
            }
            userInfoLayout.setVisibility(View.VISIBLE);
            tvRename.setVisibility(View.VISIBLE);
            tvBackup.setVisibility(View.VISIBLE);
            tvLogout.setVisibility(View.VISIBLE);
            tvLogout.setOnClickListener(v -> {
                // Đăng xuất
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            });
        } else {
            // Người dùng chưa đăng nhập
            userInfoLayout.setVisibility(View.GONE);
            tvRename.setVisibility(View.GONE);
            tvBackup.setVisibility(View.GONE);
            tvLogout.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Vui lòng đăng nhập để sử dụng đầy đủ tính năng");
        }
    }
}