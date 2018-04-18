package edu.rsztandera.ubbgps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParseTools {
    private final static String dateStrPattern = "yy.MM.dd_HH:mm:ss";

    public static String getDateTimeStr(Date givenDate) {
        return new SimpleDateFormat(dateStrPattern, Locale.ENGLISH).format(givenDate);
    }

    public static String getCurrentDateTimeStr() {
        return new SimpleDateFormat(dateStrPattern, Locale.ENGLISH).format(new Date());
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
}
