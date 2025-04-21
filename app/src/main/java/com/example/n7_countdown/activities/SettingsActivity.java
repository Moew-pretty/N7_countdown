package com.example.n7_countdown.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.Uri;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.models.User;
import com.example.n7_countdown.storage.UserDatabaseHelper;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    private LinearLayout userInfoLayout;
    private TextView tvGroupName, tvEmail, tvError, tvRename, tvBackup, tvLogout, tvGuide, tvLanguage, tvInviteFriends, tvTheme, tvFeedback, tvReportBug, tvNewFeatures;
    private ImageView ivAvatar, ivClose;
    private Button btnLoginRegister;
    private UserDatabaseHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //giaodien
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isNightMode = prefs.getBoolean("night_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isNightMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        tvTheme = findViewById(R.id.tvTheme);
        tvTheme.setText(
                isNightMode
                        ? getString(R.string.light_mode_option)
                        : getString(R.string.night_mode_option)
        );

        tvTheme.setOnClickListener(v -> {
            boolean newMode = !prefs.getBoolean("night_mode", false);
            prefs.edit().putBoolean("night_mode", newMode).apply();
            AppCompatDelegate.setDefaultNightMode(
                    newMode
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });

        userInfoLayout = findViewById(R.id.userInfoLayout);
        tvGroupName = findViewById(R.id.tvGroupName);
        tvEmail = findViewById(R.id.tvEmail);
        tvError = findViewById(R.id.tvError);
        tvRename = findViewById(R.id.tvRename);
        tvBackup = findViewById(R.id.tvBackup);
        tvLogout = findViewById(R.id.tvLogout);
        tvGuide = findViewById(R.id.tvGuide);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvInviteFriends = findViewById(R.id.tvInviteFriends);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvReportBug = findViewById(R.id.tvReportBug);
        tvNewFeatures = findViewById(R.id.tvNewFeatures);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivClose = findViewById(R.id.ivClose);
        btnLoginRegister = findViewById(R.id.btnLoginRegister);
        dbHelper = new UserDatabaseHelper(this);
        this.prefs = prefs;

        setupBottomNavigation(R.id.nav_settings);
        updateUI();

        // Xử lý sự kiện nhấn nút Đăng nhập / Đăng ký
        btnLoginRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nhấn nút Đóng
        ivClose.setOnClickListener(v -> finish());

        // Xử lý sự kiện cho các mục cài đặt
        tvRename.setOnClickListener(v -> {
            if (isLoggedIn()) {
                showRenameDialog();
            } else {
                Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            }
        });

        tvBackup.setOnClickListener(v -> {
            if (isLoggedIn()) {
                performBackup();
            } else {
                Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            }
        });

        tvLogout.setOnClickListener(v -> {
            if (isLoggedIn()) {
                logout();
            } else {
                Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            }
        });

        View.OnClickListener emailListener = v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:n7app@gmail.com"));
            String subject = (v.getId() == R.id.tvFeedback)
                    ? "Góp ý N7 Countdown"
                    : "Báo lỗi N7 Countdown";
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            startActivity(Intent.createChooser(emailIntent, "Gửi email qua"));
        };

        tvFeedback.setOnClickListener(emailListener);
        tvReportBug.setOnClickListener(emailListener);

        tvInviteFriends.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareSubject = "N7 Countdown";
            String shareBody = "Mình đang dùng N7 Countdown để đếm ngày quan trọng. Cũng hay lắm đấy. Bạn thử tải nhé:\n" +
                    "Đếm ngày vui lắm hihi.com :>";

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });

        tvGuide.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, GuideActivity.class));
        });

        tvLanguage.setOnClickListener(v -> showLanguageDialog());

        tvNewFeatures.setOnClickListener(v -> {
            Toast.makeText(this, R.string.new_features_not_implemented, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isLoggedIn() {
        return prefs.getBoolean("isLoggedIn", false);
    }

    private void updateUI() {
        boolean isLoggedIn = isLoggedIn();
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
                tvError.setText(R.string.error_user_not_found);
            }
            userInfoLayout.setVisibility(View.VISIBLE);
            btnLoginRegister.setVisibility(View.GONE);
            tvRename.setVisibility(View.VISIBLE);
            tvBackup.setVisibility(View.VISIBLE);
            tvLogout.setVisibility(View.VISIBLE);
        } else {
            // Người dùng chưa đăng nhập
            userInfoLayout.setVisibility(View.GONE);
            btnLoginRegister.setVisibility(View.VISIBLE);
            tvRename.setVisibility(View.GONE);
            tvBackup.setVisibility(View.GONE);
            tvLogout.setVisibility(View.GONE);
            tvError.setVisibility(View.GONE);
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

    private void showRenameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rename_option);

        final EditText input = new EditText(this);
        input.setText(prefs.getString("groupName", ""));
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newGroupName = input.getText().toString().trim();
            if (!newGroupName.isEmpty()) {
                String email = prefs.getString("email", null);
                if (email != null) {
                    dbHelper.updateGroupName(email, newGroupName);
                    prefs.edit().putString("groupName", newGroupName).apply();
                    updateUI();
                    Toast.makeText(this, R.string.rename_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.register_empty_error, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void performBackup() {
        Toast.makeText(this, R.string.backup_not_implemented, Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        updateUI();
        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI(); // Cập nhật giao diện khi quay lại từ LoginActivity
    }
}