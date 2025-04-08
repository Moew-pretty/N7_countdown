package com.example.n7_countdown.activities;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;
import com.example.n7_countdown.utils.TimeUtils;

public class CountdownActivity extends AppCompatActivity {
    private TextView eventNameText, countdownText;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_event_options);

        eventNameText = findViewById(R.id.eventNameText);
        countdownText = findViewById(R.id.countdownText);

        String eventName = getIntent().getStringExtra("eventName");
        if (eventName != null) {
            eventNameText.setText("Sự kiện: " + eventName);
        }

        long eventTime = getIntent().getLongExtra("eventTime", 0);
        if (eventTime > 0) {
            startCountdown(eventTime);
        } else {
            countdownText.setText("Lỗi: Không có thời gian hợp lệ.");
        }
    }

    private void startCountdown(long targetMillis) {
        countDownTimer = TimeUtils.startCountdownTimer(targetMillis, countdownText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
