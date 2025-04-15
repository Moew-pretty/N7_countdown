package com.example.n7_countdown.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.dto.EventDTO;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AddEventActivity extends BaseActivity {
    private TextView tvDate, tvTime, tvCountdownValue;
    private Spinner spinnerRepeat, spinnerEventType, spinnerReminder;

    private Map<CheckBox, Long> reminderOptionMap = new HashMap<>();
    private EditText etNotes;
    private Button btnSave;
    private ImageView ivClose;
    private TimeEventDatabaseHelper dbHelper;
    private long selectedMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        setupBottomNavigation(R.id.nav_add_event);

        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvCountdownValue = findViewById(R.id.tvCountdownValue);
        spinnerRepeat = findViewById(R.id.spinnerRepeat);
        spinnerEventType = findViewById(R.id.spinnerEventType);
        spinnerReminder = findViewById(R.id.spinnerReminder);
        setupReminderOptions();
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        ivClose = findViewById(R.id.ivClose);

        // Bấm vào chọn ngày hoặc giờ → mở DateTime picker
        tvDate.setOnClickListener(v -> openDateTimePicker());
        tvTime.setOnClickListener(v -> openDateTimePicker());

        Button btnShowReminder = findViewById(R.id.btnShowReminderOptions);
        LinearLayout reminderContainer = findViewById(R.id.reminderOptionsContainer);

        btnShowReminder.setOnClickListener(v -> {
            if (reminderContainer.getVisibility() == View.GONE) {
                reminderContainer.setVisibility(View.VISIBLE);
                btnShowReminder.setText(R.string.remindBtn_show);
            } else {
                reminderContainer.setVisibility(View.GONE);
                btnShowReminder.setText(R.string.remindBtn_hide);
            }
        });

        ivClose.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveEvent());

        dbHelper = new TimeEventDatabaseHelper(this);

    }

    private void openDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // Chọn ngày
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Chọn giờ
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                selectedMillis = calendar.getTimeInMillis();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveEvent() {
        String note = etNotes.getText().toString().trim();
        String eventType = spinnerEventType.getSelectedItem().toString();
        String reminderStr = spinnerReminder.getSelectedItem().toString();
        boolean isReminder = !reminderStr.equals("No");
        String subject = eventType;
        int color = Color.BLUE;
        String imageUri = "";

        Set<Long> selectedReminderTimes = new HashSet<>();
        for (Map.Entry<CheckBox, Long> entry : reminderOptionMap.entrySet()) {
            if (entry.getKey().isChecked()) {
                selectedReminderTimes.add(entry.getValue());
            }
        }

        if (selectedMillis == 0) {
            Toast.makeText(this, R.string.error_no_datetime, Toast.LENGTH_SHORT).show();
            return;
        }

        EventDTO event = new EventDTO();
        event.setTimestampMillis(selectedMillis);
        event.setName(subject);
        event.setNote(note);
        event.setReminderTimeMillis(selectedReminderTimes);
        event.setReminder(isReminder);
        event.setColor(color);
        event.setImageUri(imageUri);

        // Lưu vào database
        dbHelper.insertEvent(event, 1); // Thay bằng user id thật

        // Trở về trang chủ
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventTime", event.getTimestampMillis());
        startActivity(intent);
        finish();
    }

    private void setupReminderOptions() {
        LinearLayout container = findViewById(R.id.reminderOptionsContainer);

        // Định nghĩa các lựa chọn nhắc nhở
        LinkedHashMap<String, Long> options = new LinkedHashMap<>();
        options.put("1 phút", 60 * 1000L);
        options.put("5 phút", 5 * 60 * 1000L);
        options.put("15 phút", 15 * 60 * 1000L);
        options.put("30 phút", 30 * 60 * 1000L);
        options.put("1 tiếng", 60 * 60 * 1000L);
        options.put("2 tiếng", 2 * 60 * 60 * 1000L);
        options.put("3 tiếng", 3 * 60 * 60 * 1000L);
        options.put("6 tiếng", 6 * 60 * 60 * 1000L);
        options.put("12 tiếng", 12 * 60 * 60 * 1000L);
        options.put("1 ngày", 24 * 60 * 60 * 1000L);
        options.put("2 ngày", 2 * 24 * 60 * 60 * 1000L);
        options.put("3 ngày", 3 * 24 * 60 * 60 * 1000L);
        options.put("1 tuần", 7 * 24 * 60 * 60 * 1000L);
        options.put("2 tuần", 14 * 24 * 60 * 60 * 1000L);
        options.put("1 tháng", 30L * 24 * 60 * 60 * 1000L);

        for (Map.Entry<String, Long> entry : options.entrySet()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(entry.getKey());
            container.addView(checkBox);
            reminderOptionMap.put(checkBox, entry.getValue());
        }
    }

}

