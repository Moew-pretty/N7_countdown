package com.example.n7_countdown.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.n7_countdown.R;
import com.example.n7_countdown.activities.CountdownActivity;

public class EventNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "onReceive được gọi rồi nè");
        Toast.makeText(context, "Sự kiện đến rồi!", Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            createNotificationChannel(context);

            Intent activityIntent = new Intent(context, CountdownActivity.class);
            int eventId = intent.getIntExtra("eventId", -1);
            activityIntent.putExtra("eventId", eventId);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Nhắc sự kiện")
                    .setContentText(intent.getStringExtra("customMessage"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());

            Log.d("Notify", "Đã gọi notify");
        } else {
            Log.d("Notify", "Chưa có quyền POST_NOTIFICATIONS");
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default",
                    "Kênh chung",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Thông báo khi đến giờ sự kiện");
            channel.setSound(null, null);
            channel.enableVibration(false);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}

