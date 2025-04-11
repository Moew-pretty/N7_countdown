package com.example.n7_countdown.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.MainActivity;
import com.example.n7_countdown.R;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.TimeUtils;

import java.util.Calendar;

public class AddEventActivity extends BaseActivity {
    private TextView tvDate, tvTime, tvCountdownValue;
    private Spinner spinnerRepeat, spinnerEventType, spinnerReminder;
    private EditText etNotes;
    private Button btnSave;
    private ImageView ivClose;
    private TimeEventDatabaseHelper dbHelper;
    public static SQLiteDatabase debugDb;
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
        etNotes = findViewById(R.id.etNotes);
        btnSave = findViewById(R.id.btnSave);
        ivClose = findViewById(R.id.ivClose);

        // Bấm vào chọn ngày hoặc giờ → mở DateTime picker
        tvDate.setOnClickListener(v -> openDateTimePicker());
        tvTime.setOnClickListener(v -> openDateTimePicker());

        ivClose.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveEvent());

        dbHelper = new TimeEventDatabaseHelper(this);
        debugDb = dbHelper.getWritableDatabase();

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
        long reminderTimeMillis = selectedMillis - 15 * 60 * 1000; // Ví dụ: nhắc 15 phút trước
        String subject = eventType;
        boolean isCountUp = false;
        long createdAt = System.currentTimeMillis();
        int color = Color.BLUE; // hoặc random màu
        String imageUri = "";

        if (selectedMillis == 0) {
            Toast.makeText(this, R.string.error_no_datetime, Toast.LENGTH_SHORT).show();
            return;
        }

        TimeEvent event = new TimeEvent();
        event.setTimestampMillis(selectedMillis);
        event.setTimestamp(TimeUtils.millisToLocalDateTime(selectedMillis));
        event.setName(subject);
        event.setNote(note);
        event.setReminderTimeMillis(reminderTimeMillis);
        event.setReminder(isReminder);
        event.setSubject(subject);
        event.setCreatedAt(createdAt);
        event.setCountUp(isCountUp);
        event.setColor(color);
        event.setImageUri(imageUri);

        // Lưu vào database
        dbHelper.insertEvent(event);

        // Trở về trang chủ
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventTime", event.getTimestampMillis());
        startActivity(intent);
        finish();
    }

}

