package com.example.myproject.utils;

import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static String getTimeAgo(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return "";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // Định dạng tùy chỉnh hỗ trợ nano giây linh hoạt (1-9 chữ số)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                        .withZone(ZoneId.of("UTC")); // Giả định múi giờ UTC nếu không có

                // Phân tích chuỗi thành LocalDateTime
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                } catch (DateTimeParseException e) {
                    // Thử định dạng với số chữ số nano giây khác
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS")
                            .withZone(ZoneId.of("UTC"));
                    try {
                        dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                    } catch (DateTimeParseException e2) {
                        // Thử định dạng với 3 chữ số nanoBLANKgiây
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                                .withZone(ZoneId.of("UTC"));
                        dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                    }
                }

                // Chuyển sang múi giờ hệ thống
                ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault());
                LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

                // Tính khoảng cách thời gian
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(localDateTime, now);

                long seconds = duration.getSeconds();
                if (seconds < 0) {
                    return "Mới đây";
                }

                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (seconds < 60) {
                    return "Vài giây trước";
                } else if (minutes < 60) {
                    return minutes + " phút trước";
                } else if (hours < 24) {
                    return hours + " giờ trước";
                } else if (days < 30) {
                    return days + " ngày trước";
                } else {
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    return localDateTime.format(outputFormatter);
                }
            } catch (Exception e) {
                Log.e("TimeUtils", "Lỗi phân tích thời gian: " + dateTimeStr, e);
                return "Định dạng thời gian không hợp lệ";
            }
        } else {
            // Fallback cho Android cũ
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());
                Date date = sdf.parse(dateTimeStr);
                if (date == null) {
                    // Thử định dạng khác
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS", Locale.getDefault());
                    date = sdf.parse(dateTimeStr);
                }
                if (date == null) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    date = sdf.parse(dateTimeStr);
                }
                if (date == null) {
                    return "Định dạng thời gian không hợp lệ";
                }

                long millis = System.currentTimeMillis() - date.getTime();
                long seconds = millis / 1000;
                if (seconds < 0) {
                    return "Mới đây";
                }

                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (seconds < 60) {
                    return "Vài giây trước";
                } else if (minutes < 60) {
                    return minutes + " phút trước";
                } else if (hours < 24) {
                    return hours + " giờ trước";
                } else if (days < 30) {
                    return days + " ngày trước";
                } else {
                    SimpleDateFormat outputSdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return outputSdf.format(date);
                }
            } catch (Exception e) {
                Log.e("TimeUtils", "Lỗi phân tích thời gian (API cũ): " + dateTimeStr, e);
                return "Định dạng thời gian không hợp lệ";
            }
        }
    }
}