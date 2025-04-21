package com.example.n7_countdown.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.net.Uri;
import android.util.Log;

import com.example.n7_countdown.R;
import com.example.n7_countdown.models.ReminderTimes;
import com.example.n7_countdown.models.TimeEvent;
import com.example.n7_countdown.storage.TimeEventDatabaseHelper;

import java.util.Objects;
import java.util.Set;

public class ReminderManager {

    private static final String CHANNEL_ID = "event_channel_id";
    private static TimeEventDatabaseHelper dbHelper;

    public static void scheduleAllReminders(Context context, TimeEvent event) {
        if (!event.isReminder()) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // API 31+ cần kiểm tra quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }

        // Lịch chính
        scheduleOne(context, alarmManager, event.getId(), event.getId(), event.getName(), event.getTimestampMillis(), "");

        // Lịch nhắc nhở
        dbHelper = new TimeEventDatabaseHelper(context);
        Set<ReminderTimes> reminderTimes = dbHelper.getReminderTimesByEventId(event.getId());

        Log.d("remind times:" , reminderTimes.toString());

        int index = 1;
        for (ReminderTimes reminderTime : reminderTimes) {
            long time = event.getTimestampMillis() - reminderTime.getTimeMillis();
            Log.d("time: ", String.valueOf(time));
            if (time > System.currentTimeMillis()) {
                int requestCode = event.getId() * 1000 + index; // Mỗi reminder khác nhau
                scheduleOne(context, alarmManager, requestCode, event.getId(), event.getName(), time, reminderTime.getLabel());
                index++;
            }
        }
    }

    private static void scheduleOne(Context context, AlarmManager alarmManager, int requestCode, int eventId, String eventName, long timeMillis, String label) {
        Intent intent = new Intent(context, EventNotificationReceiver.class);
        intent.putExtra("eventId", eventId);
        intent.putExtra("eventName", eventName);

        if(Objects.equals(label, "")) {
            intent.putExtra("customMessage", context.getString(R.string.event_name_prefix) + " " + eventName + " " + context.getString(R.string.is_coming));
        } else {
            intent.putExtra("customMessage", context.getString(R.string.event_name_prefix) + " " + eventName + " " + context.getString(R.string.will_begin_in) + " " + label + "!");
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
    }

    // Dùng nếu bạn muốn hủy toàn bộ alarms theo event
    public static void cancelAllReminders(Context context, TimeEvent event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Hủy alarm chính
        cancelOne(context, alarmManager, event.getId());

        // Hủy reminders
        int index = 0;
        for (ReminderTimes reminder : event.getReminderTimes()) {
            int requestCode = event.getId() * 1000 + index;
            cancelOne(context, alarmManager, requestCode);
            index++;
        }
    }

    private static void cancelOne(Context context, AlarmManager alarmManager, int requestCode) {
        Intent intent = new Intent(context, EventNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
