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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.dto.EventDTO;
import com.example.n7_countdown.models.ReminderTimes;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.ReminderManager;

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
    private EditText etNotes, etEventName, etEventLocation;
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
//        spinnerReminder = findViewById(R.id.spinnerReminder);

        setupReminderOptions();

        etEventName = findViewById(R.id.eventName);
        etEventLocation = findViewById(R.id.eventLocation);
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        ivClose = findViewById(R.id.ivClose);

        // Bấm vào chọn ngày hoặc giờ → mở DateTime picker
        tvDate.setOnClickListener(v -> openDateTimePicker());
        tvTime.setOnClickListener(v -> openDateTimePicker());

        Button btnShowReminder = findViewById(R.id.btnShowReminderOptions);
        GridLayout reminderContainer = findViewById(R.id.reminderOptionsContainer);

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
        String eventName = etEventName.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String note = etNotes.getText().toString().trim();
        String eventType = spinnerEventType.getSelectedItem().toString();
        int color = Color.BLUE;
        String imageUri = "";

        Set<ReminderTimes> selectedReminders = new HashSet<>();
        for (Map.Entry<CheckBox, Long> entry : reminderOptionMap.entrySet()) {
            CheckBox checkBox = entry.getKey();
            if (checkBox.isChecked()) {
                String label = checkBox.getText().toString();
                long millisBeforeEvent = entry.getValue();

                selectedReminders.add(new ReminderTimes(millisBeforeEvent, label));
            }
        }

        boolean isReminder = !selectedReminders.isEmpty();

        if (selectedMillis == 0) {
            Toast.makeText(this, R.string.error_no_datetime, Toast.LENGTH_SHORT).show();
            return;
        }

        EventDTO event = new EventDTO();
        event.setTimestampMillis(selectedMillis);
        event.setName(eventName);
        event.setLocation(location);
        event.setNote(note);
        event.setReminderTimes(selectedReminders);
        event.setReminder(isReminder);
        event.setEventType(eventType);
        event.setColor(color);
        event.setImageUri(imageUri);

        // Lưu vào database
        TimeEvent savedEvent = dbHelper.insertEvent(event, 1); // Thay bằng user id thật
        ReminderManager.scheduleAllReminders(this, savedEvent);

        // Trở về trang chủ
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventTime", event.getTimestampMillis());
        startActivity(intent);
        finish();
    }

    private void setupReminderOptions() {
        GridLayout container = findViewById(R.id.reminderOptionsContainer);

        // Định nghĩa các lựa chọn nhắc nhở
        LinkedHashMap<String, Long> options = new LinkedHashMap<>();
        options.put("1 " + getString(R.string.minutes_unit_downcase), 60 * 1000L);
        options.put("5 " + getString(R.string.minutes_unit_downcase), 5 * 60 * 1000L);
        options.put("15 " + getString(R.string.minutes_unit_downcase), 15 * 60 * 1000L);
        options.put("30 " + getString(R.string.minutes_unit_downcase), 30 * 60 * 1000L);
        options.put("1 " + getString(R.string.hours_unit_downcase), 60 * 60 * 1000L);
        options.put("2 " + getString(R.string.hours_unit_downcase), 2 * 60 * 60 * 1000L);
        options.put("3 " + getString(R.string.hours_unit_downcase), 3 * 60 * 60 * 1000L);
        options.put("6 " + getString(R.string.hours_unit_downcase), 6 * 60 * 60 * 1000L);
        options.put("12 " + getString(R.string.hours_unit_downcase), 12 * 60 * 60 * 1000L);
        options.put("1 " + getString(R.string.days_unit_downcase), 24 * 60 * 60 * 1000L);
        options.put("2 " + getString(R.string.days_unit_downcase), 2 * 24 * 60 * 60 * 1000L);
        options.put("3 " + getString(R.string.days_unit_downcase), 3 * 24 * 60 * 60 * 1000L);
        options.put("1 " + getString(R.string.weeks_unit_downcase), 7 * 24 * 60 * 60 * 1000L);
        options.put("2 " + getString(R.string.weeks_unit_downcase), 14 * 24 * 60 * 60 * 1000L);
        options.put("1 " + getString(R.string.months_unit_downcase), 30L * 24 * 60 * 60 * 1000L);

        for (Map.Entry<String, Long> entry : options.entrySet()) {
            CheckBox checkBox = new CheckBox(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            checkBox.setLayoutParams(params);

            checkBox.setText(entry.getKey());
            container.addView(checkBox);
            reminderOptionMap.put(checkBox, entry.getValue());
        }
    }

}

