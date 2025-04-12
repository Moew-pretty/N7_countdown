package com.example.n7_countdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.n7_countdown.activities.AddEventActivity;
import com.example.n7_countdown.activities.BaseActivity;
import com.example.n7_countdown.activities.CountdownActivity;
import com.example.n7_countdown.activities.LoginActivity;
import com.example.n7_countdown.activities.SettingsActivity;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;

public class MainActivity extends BaseActivity {
    private LinearLayout cardContainer;
    private TimeEventDatabaseHelper dbHelper;

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

        cardContainer = findViewById(R.id.cardContainer);
        dbHelper = new TimeEventDatabaseHelper(this);

        loadEventCards();

    }

    private void loadEventCards() {
        List<TimeEvent> events = dbHelper.getAllEvents(1); // Thay bằng user id thật
        cardContainer.removeAllViews(); // clear old views

        for (TimeEvent event : events) {
            View cardView = LayoutInflater.from(this).inflate(R.layout.item_event_card, cardContainer, false);

            TextView eventTime = cardView.findViewById(R.id.eventTime);
            EditText eventName = cardView.findViewById(R.id.eventName);
            TextView countdownDays = cardView.findViewById(R.id.countdownText_general);
            TextView countdownHours = cardView.findViewById(R.id.countdownText_hours);

            // Gán dữ liệu vào các view
            LocalDateTime dateTime = TimeUtils.millisToLocalDateTime(event.getTimestampMillis());
            eventTime.setText(TimeUtils.formatDateTimeShort(dateTime));
            eventName.setText(event.getName());

            // Bắt đầu đếm ngược
            TimeUtils.startCountUpOrDownTimer(event.getTimestampMillis(), countdownDays);
            TimeUtils.startCountdownTimerHMSOnly(event.getTimestampMillis(), countdownHours);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CountdownActivity.class);
                intent.putExtra("eventId", event.getId());
                startActivity(intent);
            });


            cardContainer.addView(cardView);
        }
    }
}