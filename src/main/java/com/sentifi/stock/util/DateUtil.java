package com.sentifi.stock.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author khaled
 */
public class DateUtil {
    public static boolean isBefore(final Date d1, final Date d2) {
        return d1.before(d2) || d1.compareTo(d2) == 0;
    }

    public static boolean isAfter(final Date d1, final Date d2) {
        return d1.after(d2) || d1.compareTo(d2) == 0;
    }

    public static boolean isBetween(final Date d, final Date start, final Date end) {
        return isAfter(d, start) && isBefore(d, end);
    }

    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
