package com.example.n7_countdown.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.n7_countdown.R;
import com.example.n7_countdown.utils.TimeUtils;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventNameInput;
    private TextView selectedDateTime;
    private long selectedMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameInput = findViewById(R.id.eventNameInput);
        selectedDateTime = findViewById(R.id.selectedDateTime);
        Button pickDateTimeButton = findViewById(R.id.pickDateTimeButton);
        Button saveButton = findViewById(R.id.saveButton);

        pickDateTimeButton.setOnClickListener(v -> openDateTimePicker());
        saveButton.setOnClickListener(v -> saveEvent());
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
                selectedDateTime.setText(TimeUtils.formatMillisToDate(selectedMillis, "dd/MM/yyyy HH:mm"));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveEvent() {
        String eventName = eventNameInput.getText().toString().trim();
        if (eventName.isEmpty() || selectedMillis == 0) {
            selectedDateTime.setText("Vui lòng chọn ngày giờ!");
            return;
        }

        Intent intent = new Intent(this, CountdownActivity.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventTime", selectedMillis);
        startActivity(intent);
    }
}

