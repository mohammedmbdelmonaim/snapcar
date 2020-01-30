package com.intcore.snapcar.core.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class TimeUtil {

    private static final String HOUR_FORMAT_24 = "H:mm";
    private static final String HOUR_FORMAT_12 = "h:mm a";

    private final DateFormat timeFormatter;

    @Inject
    public TimeUtil(Context context) {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
        String format = is24HourFormat ? HOUR_FORMAT_24 : HOUR_FORMAT_12;
        timeFormatter = new SimpleDateFormat(format, Locale.getDefault());
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static long getCurrentUnixTime() {
        return System.currentTimeMillis() / 1000L;
    }

    public String formatTime(long timeStamp) {
        return timeFormatter.format(new Date(timeStamp * 1000L));
    }

    public String formatMinutes(long millis) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        int minutes = (int) (totalSeconds / 60);
        return String.format(Locale.getDefault(), "%02d", minutes);
    }

    public String formatSeconds(long millis) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        NumberFormat formatter = new DecimalFormat("00.00");
        String formattedTime = formatter.format((double) totalSeconds / 60);
        String[] splitValues = formattedTime.split("\\.");
        String secondsString = "0." + splitValues[1];
        float seconds = Float.valueOf(secondsString) * 60;
        return String.format(Locale.getDefault(), "%02d", Math.round(seconds));
    }
}
