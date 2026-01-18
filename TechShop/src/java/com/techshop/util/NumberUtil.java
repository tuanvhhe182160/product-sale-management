package com.techshop.util;

import java.text.DecimalFormat;
import java.math.BigDecimal;

public class NumberUtil {
    
    private static final DecimalFormat CURRENCY_FORMAT = 
        new DecimalFormat("#,###");
    
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return CURRENCY_FORMAT.format(amount) + "đ";
    }
    
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount) + "đ";
    }
    
    public static int parseIntOrDefault(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static double parseDoubleOrDefault(String str, double defaultValue) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}