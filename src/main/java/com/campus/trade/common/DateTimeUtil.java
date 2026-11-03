package com.campus.trade.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期格式化工具，供 JSP 通过 getXxxText() 调用，避免 JSTL fmt 对 LocalDateTime 的兼容问题。
 */
public final class DateTimeUtil {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeUtil() {
    }

    public static String format(LocalDateTime value) {
        return value == null ? "-" : DATE_TIME.format(value);
    }

    public static String formatDate(LocalDateTime value) {
        return value == null ? "-" : DATE.format(value);
    }

    public static String formatTime(LocalDateTime value) {
        return value == null ? "-" : TIME.format(value);
    }

    public static String nowText() {
        return DATE_TIME.format(LocalDateTime.now());
    }

    public static String todayText() {
        return DATE.format(LocalDateTime.now());
    }
}
