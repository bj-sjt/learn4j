package com.itao.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class DateUtil {

    /**
     * 获取年份
     */
    public static int getYear(LocalDate localDate) {
        return localDate.get(ChronoField.YEAR);
    }

    /**
     * 获取月份（1 - 12）
     */
    public static int getMonth(LocalDate localDate) {
        return localDate.get(ChronoField.MONTH_OF_YEAR);
    }

    /**
     * 获取一周的第几天（1 - 7）
     */
    public static int getDayOfWeek(LocalDate localDate) {
        return localDate.get(ChronoField.DAY_OF_WEEK);
    }

    /**
     * 获取当月的第几天
     */
    public static int getDayOfMonth(LocalDate localDate) {
        return localDate.get(ChronoField.DAY_OF_MONTH);
    }

    /**
     * 获取当年的第几天
     */
    public static int getDayOfYear(LocalDate localDate) {
        return localDate.get(ChronoField.DAY_OF_YEAR);
    }

    /**
     * 一天的开始
     */
    public static LocalDateTime startOfDay(LocalDate localDate){
        return localDate.atStartOfDay();
    }

}
