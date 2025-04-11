package com.example.n7_countdown.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.models.User;
import com.example.n7_countdown.storage.UserDatabaseHelper;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    private LinearLayout userInfoLayout;
    private TextView tvGroupName, tvEmail, tvError, tvRename, tvBackup, tvLogout, tvGuide, tvLanguage;
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
        tvGuide = findViewById(R.id.tvGuide);
        tvLanguage = findViewById(R.id.tvLanguage);
        dbHelper = new UserDatabaseHelper(this);

        setupBottomNavigation(R.id.nav_settings);
        updateUI();

        tvGuide.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, GuideActivity.class));
        });

        tvLanguage.setOnClickListener(v -> showLanguageDialog());
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

    private void showLanguageDialog() {
        String[] languages = {
                getString(R.string.language_vietnamese),
                getString(R.string.language_english),
                getString(R.string.language_chinese)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_language);
        builder.setItems(languages, (dialog, which) -> {
            String languageCode;
            switch (which) {
                case 0: // Tiếng Việt
                    languageCode = "vi";
                    break;
                case 1: // Tiếng Anh
                    languageCode = "en";
                    break;
                case 2: // Tiếng Trung
                    languageCode = "zh";
                    break;
                default:
                    languageCode = "vi";
            }
            setLocale(languageCode);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void setLocale(String languageCode) {
        // Lưu ngôn ngữ vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", languageCode);
        editor.apply();

        // Cập nhật ngôn ngữ
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Tải lại toàn bộ ứng dụng để áp dụng ngôn ngữ
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}