package com.example.n7_countdown.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n7_countdown.R;
import com.example.n7_countdown.models.ReminderTimes;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.ReminderManager;
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
    private final Map<CheckBox, Long> reminderOptionMap = new HashMap<>();
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

            for (ReminderTimes reminder : dbHelper.getReminderTimesByEventId(eventId)) {
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

            Set<ReminderTimes> selectedReminders = new HashSet<>();
            for (Map.Entry<CheckBox, Long> entry : reminderOptionMap.entrySet()) {
                CheckBox checkBox = entry.getKey();
                if (checkBox.isChecked()) {
                    String label = checkBox.getText().toString();
                    long millisBeforeEvent = entry.getValue();

                    selectedReminders.add(new ReminderTimes(millisBeforeEvent, label));
                }
            }

            dbHelper.updateReminderTimes(eventId, selectedReminders);
            TimeEvent updatedEvent = dbHelper.updateEvent(updated);

            ReminderManager.scheduleAllReminders(this, updatedEvent);

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

