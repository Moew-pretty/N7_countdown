package com.example.n7_countdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.n7_countdown.activities.BaseActivity;
import com.example.n7_countdown.activities.CountdownActivity;
import com.example.n7_countdown.activities.EditEventActivity;
import com.example.n7_countdown.activities.LoginActivity;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;
import com.example.n7_countdown.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    ViewGroup container;
    private TimeEventDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigation(R.id.nav_home);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            container = findViewById(R.id.cardGrid);
        } else {
            container = findViewById(R.id.cardContainer);
        }

        dbHelper = new TimeEventDatabaseHelper(this);
        loadEventCards();
    }

    private void loadEventCards() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            List<TimeEvent> events = dbHelper.getAllEvents(1);

            handler.post(() -> {
                container.removeAllViews();

                for (TimeEvent event : events) {
                    View cardView = LayoutInflater.from(this).inflate(R.layout.item_event_card, container, false);
                    if (container instanceof GridLayout) {
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = 0;
                        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        params.setMargins(16, 16, 16, 16);
                        cardView.setLayoutParams(params);
                    }

                    TextView eventTime = cardView.findViewById(R.id.eventTime);
                    EditText eventName = cardView.findViewById(R.id.eventName);
                    TextView countdownDays = cardView.findViewById(R.id.countdownText_general);
                    TextView countdownHours = cardView.findViewById(R.id.countdownText_hours);

                    LocalDateTime dateTime = TimeUtils.millisToLocalDateTime(event.getTimestampMillis());
                    eventTime.setText(TimeUtils.formatDateTimeShort(dateTime));
                    eventName.setText(event.getName());

                    TimeUtils.startCountUpOrDownTimer(this, event.getTimestampMillis(), countdownDays);
                    TimeUtils.startCountdownTimerHMSOnly(event.getTimestampMillis(), countdownHours);

                    cardView.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, CountdownActivity.class);
                        intent.putExtra("eventId", event.getId());
                        startActivity(intent);
                    });

                    ImageButton menuBtn = cardView.findViewById(R.id.eventMenuBtn);
                    menuBtn.setOnClickListener(v -> {
                        PopupMenu popupMenu = new PopupMenu(menuBtn.getContext(), menuBtn);

                        popupMenu.getMenuInflater().inflate(R.menu.menu_event_options, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == R.id.action_delete) {
                                dbHelper.deleteEvent(event.getId());
                                container.removeView(cardView);
                                Toast.makeText(MainActivity.this, "Đã xóa sự kiện", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (itemId == R.id.action_edit) {
                                Intent intent = new Intent(MainActivity.this, EditEventActivity.class);
                                intent.putExtra("eventId", event.getId());
                                startActivity(intent);
                                return false;
                            }
                            return true;
                        });

                        popupMenu.show();
                    });

                    container.addView(cardView);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventCards(); // Tải lại danh sách sự kiện khi quay lại
    }
}