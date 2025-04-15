package com.example.n7_countdown.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n7_countdown.R;
import com.example.n7_countdown.models.ReminderTimes;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.TimeUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class EditEventActivity extends BaseActivity {

    private EditText editName, editNote, editLocation;
    private TextView txtSelectedDateTime;
    private Button btnPickDateTime, btnSave, btnCancel;
    private long selectedTimestampMillis = -1;
    private int eventId;
    private Map<CheckBox, Long> reminderOptionMap = new HashMap<>();
    private TimeEventDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        editName = findViewById(R.id.editEventName);
        editNote = findViewById(R.id.editEventNote);
        editLocation = findViewById(R.id.editEventLocation);
        txtSelectedDateTime = findViewById(R.id.txtSelectedDateTime);
        btnPickDateTime = findViewById(R.id.btnPickDateTime);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSaveEvent);

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

        dbHelper = new TimeEventDatabaseHelper(this);
        eventId = getIntent().getIntExtra("eventId", -1);
        setupReminderOptions();

        // Load event cũ
        TimeEvent event = dbHelper.getEventById(eventId);
        if (event != null) {
            editName.setText(event.getName());
            editNote.setText(event.getNote());
            editLocation.setText(event.getLocation());
            selectedTimestampMillis = event.getTimestampMillis();
            txtSelectedDateTime.setText(TimeUtils.formatDateTimeShort(
                    TimeUtils.millisToLocalDateTime(selectedTimestampMillis)));

            for (ReminderTimes reminder : event.getReminderTimes()) {
                for (Map.Entry<CheckBox, Long> entry : reminderOptionMap.entrySet()) {
                    if (entry.getValue().equals(reminder.getTimeMillis())) {
                        entry.getKey().setChecked(true);
                    }
                }
            }
        }

        btnPickDateTime.setOnClickListener(v -> showDateTimePicker());

        btnCancel.setOnClickListener(v -> {
            finish(); // quay lại activity trước đó
        });

        btnSave.setOnClickListener(v -> {
            TimeEvent updated = new TimeEvent();
            updated.setId(eventId);
            updated.setName(editName.getText().toString());
            updated.setNote(editNote.getText().toString());
            updated.setLocation(editLocation.getText().toString());
            updated.setTimestampMillis(selectedTimestampMillis);

            Set<Long> selectedReminderTimes = new HashSet<>();
            for (Map.Entry<CheckBox, Long> entry : reminderOptionMap.entrySet()) {
                if (entry.getKey().isChecked()) {
                    selectedReminderTimes.add(entry.getValue());
                }
            }

            dbHelper.insertReminderTimes(eventId, selectedReminderTimes);
            dbHelper.updateEvent(updated);
            Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            new TimePickerDialog(this, (view1, hour, minute) -> {
                calendar.set(year, month, dayOfMonth, hour, minute);
                selectedTimestampMillis = calendar.getTimeInMillis();
                txtSelectedDateTime.setText(TimeUtils.formatDateTimeShort(
                        TimeUtils.millisToLocalDateTime(selectedTimestampMillis)));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

