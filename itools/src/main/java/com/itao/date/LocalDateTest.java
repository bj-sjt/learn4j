package com.itao.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;


public class LocalDateTest {

    public static void main(String[] args) {
        Period period = Period.between(LocalDate.now(), LocalDate.of(2024, Month.of(3), 10));
        System.out.println(period);
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.until(LocalDate.of(2024, Month.of(3), 10), ChronoUnit.MONTHS));
        System.out.println(period.toTotalMonths());
    }
}
