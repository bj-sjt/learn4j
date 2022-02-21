package com.itao.hutool;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class DateTest {

    public static void main(String[] args) {
        //当前时间
        Date date = DateUtil.date();
        //当前时间
        Date date2 = DateUtil.date(Calendar.getInstance());
        //当前时间
        Date date3 = DateUtil.date(System.currentTimeMillis());
        //当前时间字符串，格式：yyyy-MM-dd HH:mm:ss
        String now = DateUtil.now();
        //当前日期字符串，格式：yyyy-MM-dd
        String today = DateUtil.today();

//        System.out.println(date);
//        System.out.println(date2);
//        System.out.println(date3);
//        System.out.println(now);
//        System.out.println(today);

        String dateStr = "2017-03-01";
        Date parse = DateUtil.parse(dateStr);
        Date parse1 = DateUtil.parse(dateStr, "yyyy-MM-dd");
//        System.out.println(parse);
//        System.out.println(parse1);
        //结果 2017/03/01
        String format = DateUtil.format(date, "yyyy/MM/dd");
        //常用格式的格式化，结果：2017-03-01
        String formatDate = DateUtil.formatDate(date);
        //结果：2017-03-01 00:00:00
        String formatDateTime = DateUtil.formatDateTime(date);
        //结果：00:00:00
        String formatTime = DateUtil.formatTime(date);

//        System.out.println(format);
//        System.out.println(formatDate);
//        System.out.println(formatDateTime);
//        System.out.println(formatTime);

        String dateStr1 = "2017-03-01 22:33:23";
        Date begin = DateUtil.parse(dateStr1);

        String dateStr2 = "2017-04-01 23:34:23";
        Date end = DateUtil.parse(dateStr2);

        //System.out.println(DateUtil.offset(begin, DateField.HOUR, 24));
        //相差一个月，31天
        long betweenDay = DateUtil.between(begin, end, DateUnit.MS);
        //System.out.println(betweenDay);

        //Level.MINUTE表示精确到分
        String formatBetween = DateUtil.formatBetween(betweenDay, BetweenFormatter.Level.MINUTE);
        //输出：31天1小时
        //System.out.println(formatBetween);
        int age = DateUtil.ageOfNow("1990-01-16");
        System.out.println(age);
    }
}
