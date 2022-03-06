package com.lyqiang.monitor.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author lyqiang
 */
public class DateUtils {

    private static final TimeZone TIMEZONE = TimeZone.getTimeZone("GMT+8");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    private DateUtils() {
    }

    /**
     * 当天时间0点
     */
    public static Date dateMinOfDay(Date date) {
        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 格式化Date日期为20160415112036或20160415112036007
     *
     * @param date           日期
     * @param hasMillisecond 是否包含毫秒
     * @return long
     */
    public static long format2Long(Date date, boolean hasMillisecond) {
        if (date == null) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance(TIMEZONE);
        calendar.setTime(date);

        String year = String.format("%04d", calendar.get(Calendar.YEAR));
        String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
        String second = String.format("%02d", calendar.get(Calendar.SECOND));

        String str = year + month + day + hour + minute + second;

        if (hasMillisecond) {
            String millisecond = String.format("%03d", calendar.get(Calendar.MILLISECOND));
            str = str.concat(millisecond);
        }

        return Long.parseLong(str);
    }

    public static String now() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
