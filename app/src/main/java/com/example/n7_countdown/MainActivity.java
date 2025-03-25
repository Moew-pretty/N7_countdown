package com.example.n7_countdown;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.n7_countdown.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private Button btnPickDate;
    private TextView txtSelectedDate, txtCountdown;
    private CountDownTimer countdownTimer;
    private long selectedTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPickDate = findViewById(R.id.btnPickDate);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtCountdown = findViewById(R.id.txtCountdown);

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Chuyển đổi thành LocalDateTime
                    LocalDateTime selectedDate = LocalDateTime.of(selectedYear, selectedMonth + 1, selectedDay, 0, 0);
                    selectedTimeMillis = TimeUtils.localDateTimeToMillis(selectedDate);

                    // Hiển thị ngày đã chọn
                    txtSelectedDate.setText("Ngày đã chọn: " + TimeUtils.formatDateTimeLong(selectedDate));

                    // Bắt đầu đếm ngược
                    startCountdown();
                }, year, month, day);

        datePickerDialog.show();
    }

    private void startCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        countdownTimer = TimeUtils.startCountdownTimer(selectedTimeMillis, txtCountdown);
    }
}