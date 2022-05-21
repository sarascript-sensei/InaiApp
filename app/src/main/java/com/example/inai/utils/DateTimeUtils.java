package com.example.inai.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static String getSuffix(int day) {
        if (day == 1) return "st";
        if (day == 2) return "nd";
        if (day == 3) return "rd";
        return "th";
    }

    public static String formatTime24H(LocalTime time) {
        DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatObj);
    }

    public static String formatTime12H(LocalTime time) {
        DateTimeFormatter timeFormatObj = DateTimeFormatter.ofPattern("hh.mma");
        return time.format(timeFormatObj);
    }

    public static String formatDate(LocalDate date) {
        DateTimeFormatter formatObjDate = DateTimeFormatter.ofPattern("MMM d");
        return date.format(formatObjDate) + getSuffix(date.getDayOfMonth());
    }

    public static String getDayOfWeek(LocalDate date) {
        DateTimeFormatter dateFormatObj = DateTimeFormatter.ofPattern("EEEE");
        return date.format(dateFormatObj);
    }

}