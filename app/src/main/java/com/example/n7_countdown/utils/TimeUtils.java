package com.example.n7_countdown.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    // Đếm ngược theo thời gian thực
    public static CountDownTimer startCountdownTimer(long targetMillis, TextView textView) {
        long currentMillis = System.currentTimeMillis();
        long timeRemaining = targetMillis - currentMillis;

        if (timeRemaining <= 0) {
            textView.setText("Sự kiện đã diễn ra.");
            return null;
        }

        CountDownTimer timer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(formatDuration(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                textView.setText("Đã đến thời điểm!");
            }
        };

        timer.start();
        return timer;
    }

    // Hiển thị kiểu "20/04/2025 09:00 sáng"
    public static String formatDateTimeShort(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.getDefault());
        return ldt.format(formatter);
    }

    // Hiển thị kiểu "Thứ Hai, 20 tháng 4, 2025"
    public static String formatDateTimeLong(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'tháng' M, yyyy", new Locale("vi", "VN"));
        return ldt.format(formatter);
    }

    // Chuyển LocalDateTime → millis
    public static long localDateTimeToMillis(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    // Chuyển millis → LocalDateTime (ngược lại)
    public static LocalDateTime millisToLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(millis),
                ZoneId.systemDefault()
        );
    }

    // Chuyển từ milliseconds -> ngày/giờ/phút/giây
    public static String formatDuration(long millis) {
        boolean isFuture = millis >= 0;
        millis = Math.abs(millis);

        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60)) % 24;
        long days = millis / (1000 * 60 * 60 * 24);

        String result = days + " ngày, " + hours + " giờ, " + minutes + " phút, " + seconds + " giây";
        return isFuture ? "Còn lại: " + result : "Đã qua: " + result;
    }

    // Chuyển String -> millis (từ ngày giờ do người dùng nhập)
    // Ví dụ: long time = parseDateTimeToMillis("01/01/2025 00:00", "dd/MM/yyyy HH:mm");
    public static long parseDateTimeToMillis(String dateTime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            Date date = sdf.parse(dateTime);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Chuyển millis -> String (định dạng ngày giờ)
    // Ví dụ: String date = formatMillisToDate(1735689600000L, "dd/MM/yyyy HH:mm");
    public static String formatMillisToDate(long millis, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    // Tính khoảng cách thời gian 2 thời điểm
    public static long getTimeDifference(long fromMillis, long toMillis) {
        return toMillis - fromMillis;
    }

    // Check thời điểm là quá khứ hay tương lai
    public static boolean isFutureTime(long millis) {
        return millis > System.currentTimeMillis();
    }

}
