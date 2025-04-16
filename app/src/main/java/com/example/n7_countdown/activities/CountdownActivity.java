package com.example.n7_countdown.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;

public class CountdownActivity extends AppCompatActivity {
    private TextView eventName, eventLocation, eventNote;
    private TextView days, hours, minutes, seconds;
    private CountDownTimer timer;
    private TimeEventDatabaseHelper dbHelper;

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

        TimeEvent event = dbHelper.getEventById(eventId);

        // Hiển thị dữ liệu
        if(event.getName() != null) {
            eventName.setText(String.format("%s%s", getString(R.string.event_name_prefix), event.getName()));
        } else {
            eventName.setText(R.string.event_name_prefix);
        }
        if(event.getLocation() != null) {
            eventLocation.setText(String.format("%s%s", getString(R.string.event_location_prefix), event.getLocation()));
        } else {
            eventLocation.setText(getString(R.string.event_location_prefix) + getString(R.string.no_value));
        }
        if(event.getNote() != null) {
            eventNote.setText(String.format("%s%s", getString(R.string.event_note_prefix), event.getNote()));
        } else {
            eventNote.setText(getString(R.string.event_note_prefix) + getString(R.string.no_value));
        }

        // Đếm theo thời gian
        startCountdown(event.getTimestampMillis());

        LinearLayout backButton = findViewById(R.id.backButtonLayout);
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

    }

    private void startCountdown(long targetMillis) {
        long currentMillis = System.currentTimeMillis();
        long remaining = targetMillis - currentMillis;
        TextView title = findViewById(R.id.title);

        // Nếu sự kiện ở tương lai => đếm ngược bình thường
        if (remaining > 0) {
            title.setText(getString(R.string.countdown_title));
            timer = new CountDownTimer(remaining, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long totalSeconds = millisUntilFinished / 1000;
                    long d = totalSeconds / (60 * 60 * 24);
                    long h = (totalSeconds / (60 * 60)) % 24;
                    long m = (totalSeconds / 60) % 60;
                    long s = totalSeconds % 60;

                    days.setText(String.format("%02d", d));
                    hours.setText(String.format("%02d", h));
                    minutes.setText(String.format("%02d", m));
                    seconds.setText(String.format("%02d", s));
                }

                @Override
                public void onFinish() {
                    days.setText("00");
                    hours.setText("00");
                    minutes.setText("00");
                    seconds.setText("00");
                }
            }.start();
        } else {
            // Sự kiện đã xảy ra trong quá khứ => đếm xuôi (tăng thời gian đã trôi qua)
            title.setText(getString(R.string.countup_title));
            long passedMillis = -remaining;
            timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                long offset = passedMillis;

                @Override
                public void onTick(long millisUntilFinished) {
                    long totalSeconds = offset / 1000;
                    long d = totalSeconds / (60 * 60 * 24);
                    long h = (totalSeconds / (60 * 60)) % 24;
                    long m = (totalSeconds / 60) % 60;
                    long s = totalSeconds % 60;

                    days.setText(String.format("%02d", d));
                    hours.setText(String.format("%02d", h));
                    minutes.setText(String.format("%02d", m));
                    seconds.setText(String.format("%02d", s));

                    offset += 1000;
                }

                @Override
                public void onFinish() {
                    // không bao giờ gọi vì đếm mãi mãi
                }
            }.start();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

}
