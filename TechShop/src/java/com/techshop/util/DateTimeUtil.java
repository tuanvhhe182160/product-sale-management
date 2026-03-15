package com.techshop.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMATTER);
    }
    
    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_ONLY_FORMATTER);
    }
    
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}