package com.example.n7_countdown.activities;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.TimeUtils;

public class CountdownActivity extends AppCompatActivity {
    private TextView eventName, eventLocation, eventNote;
    private TextView days, hours, minutes, seconds;
    private CountDownTimer timer;
    private TimeEventDatabaseHelper dbHelper;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        // Ánh xạ View
        eventName = findViewById(R.id.eventName);
        eventLocation = findViewById(R.id.eventLocation);
        eventNote = findViewById(R.id.eventNote);
        days = findViewById(R.id.days);
        hours = findViewById(R.id.hours);
        minutes = findViewById(R.id.minutes);
        seconds = findViewById(R.id.seconds);

        dbHelper = new TimeEventDatabaseHelper(this);

        int eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Không tìm thấy sự kiện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TimeEvent event = dbHelper.getEvent(eventId);

        // Hiển thị dữ liệu
        if(event.getName() != null) {
            eventName.setText("Sự kiện: " + event.getName());
        } else {
            eventName.setText("Sự kiện: ");
        }
        if(event.getLocation() != null) {
            eventLocation.setText("Địa điểm: " + event.getLocation());
        } else {
            eventLocation.setText("Địa điểm: không có");
        }
        if(event.getNote() != null) {
            eventNote.setText("Ghi chú: " + event.getNote());
        } else {
            eventNote.setText("Ghi chú: không có");
        }

        // Đếm ngược theo thời gian còn lại
        startCountdown(event.getTimestampMillis());

        LinearLayout backButton = findViewById(R.id.backButtonLayout);
        backButton.setOnClickListener(v -> {
            finish();
        });

    }

    private void startCountdown(long targetMillis) {
        long currentMillis = System.currentTimeMillis();
        long remaining = targetMillis - currentMillis;

        if (remaining <= 0) {
            days.setText("0");
            hours.setText("0");
            minutes.setText("0");
            seconds.setText("0");
            return;
        }

        timer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                long d = totalSeconds / (60 * 60 * 24);
                long h = (totalSeconds / (60 * 60)) % 24;
                long m = (totalSeconds / 60) % 60;
                long s = totalSeconds % 60;

                days.setText(d < 10 ? "0" + d : String.valueOf(d));
                hours.setText(h < 10 ? "0" + h : String.valueOf(h));
                minutes.setText(m < 10 ? "0" + m : String.valueOf(m));
                seconds.setText(s < 10 ? "0" + s : String.valueOf(s));
            }

            @Override
            public void onFinish() {
                days.setText("0");
                hours.setText("0");
                minutes.setText("0");
                seconds.setText("0");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

//    private void startCountdown(long targetMillis) {
//        countDownTimer = TimeUtils.startCountdownTimer(targetMillis, countdownText);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
//    }
}
