package com.itao.date;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTest {
    public static void main(String[] args) {
        /*LocalDate date = LocalDate.now();
        System.out.println(date.toString());
        System.out.println(date);*/
        /*DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        zonedDateTime = zonedDateTime.minusDays(10);
        zonedDateTime= zonedDateTime.plusDays(5);
        System.out.println(zonedDateTime.format(dateTimeFormatter));*/
        Clock clock = Clock.systemUTC();
        System.out.println(clock);
    }
}
