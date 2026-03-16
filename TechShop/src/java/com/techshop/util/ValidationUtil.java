package com.techshop.util;

import java.util.regex.Pattern;

/**
 * Validation Utility for Data Validation
 * Includes IMEI, email, phone, and general input validation
 * 
 * @author TuanVH - Iteration 2
 */
public class ValidationUtil {
    
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(0|\\+84)[0-9]{9}$"  // Vietnamese phone: 0912345678 or +84912345678
    );
    
    private static final Pattern IMEI_PATTERN = Pattern.compile(
        "^[0-9]{15}$"  // IMEI is exactly 15 digits
    );
    
    // =====================================================
    // IMEI VALIDATION
    // =====================================================    
    /**
     * Validate IMEI format (15 digits)
     * @param imei IMEI string to validate
     * @return true if format is valid
     */
    public static boolean isIMEIFormatValid(String imei) {
        if (imei == null || imei.trim().isEmpty()) {
            return false;
        }
        
        return IMEI_PATTERN.matcher(imei.trim()).matches();
    }
    
    /**
     * Validate IMEI using Luhn algorithm (checksum)
     * This is the official IMEI validation algorithm
     * 
     * @param imei IMEI string to validate
     * @return true if IMEI passes Luhn check
     */
    public static boolean isIMEIChecksumValid(String imei) {
        if (!isIMEIFormatValid(imei)) {
            return false;
        }
        
        // Luhn algorithm for IMEI validation
        int sum = 0;
        boolean alternate = false;
        
        // Process from right to left
        for (int i = imei.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(imei.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10) == 0;
    }
    
    /**
     * Complete IMEI validation (format + checksum)
     * @param imei IMEI to validate
     * @return true if IMEI is valid
     */
    public static boolean isIMEIValid(String imei) {
        return isIMEIFormatValid(imei) && isIMEIChecksumValid(imei);
    }
    
    /**
     * Get IMEI validation error message
     * @param imei IMEI to validate
     * @return Error message or null if valid
     */
    public static String getIMEIValidationError(String imei) {
        if (imei == null || imei.trim().isEmpty()) {
            return "IMEI cannot be empty";
        }
        
        if (!isIMEIFormatValid(imei)) {
            return "IMEI must be exactly 15 digits";
        }
        
        if (!isIMEIChecksumValid(imei)) {
            return "Invalid IMEI checksum";
        }
        
        return null; // Valid
    }
    
    // =====================================================
    // EMAIL VALIDATION
    // =====================================================    
    /**
     * Validate email format
     * @param email Email address to validate
     * @return true if format is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Get email validation error message
     * @param email Email to validate
     * @return Error message or null if valid
     */
    public static String getEmailValidationError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        
        return null; // Valid
    }
    
    // =====================================================
    // PHONE VALIDATION
    // =====================================================    
    /**
     * Validate Vietnamese phone number format
     * Accepts: 0912345678 or +84912345678
     * 
     * @param phone Phone number to validate
     * @return true if format is valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Normalize phone number to standard format (0xxxxxxxxx)
     * Converts +84 to 0
     * 
     * @param phone Phone number to normalize
     * @return Normalized phone number or original if invalid
     */
    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        
        String trimmed = phone.trim();
        if (trimmed.startsWith("+84")) {
            return "0" + trimmed.substring(3);
        }
        
        return trimmed;
    }
    
    /**
     * Get phone validation error message
     * @param phone Phone to validate
     * @return Error message or null if valid
     */
    public static String getPhoneValidationError(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number cannot be empty";
        }
        
        if (!isValidPhone(phone)) {
            return "Invalid phone number format (use 0xxxxxxxxx or +84xxxxxxxxx)";
        }
        
        return null; // Valid
    }
    
    // =====================================================
    // GENERAL INPUT VALIDATION
    // =====================================================    
    /**
     * Check if string is null or empty (after trim)
     * @param str String to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate string length
     * @param str String to validate
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return true if length is valid
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate numeric string
     * @param str String to validate
     * @return true if string contains only digits
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        return str.trim().matches("^[0-9]+$");
    }
    
    /**
     * Validate alphanumeric string
     * @param str String to validate
     * @return true if string contains only letters and digits
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        return str.trim().matches("^[A-Za-z0-9]+$");
    }
    
    /**
     * Sanitize string for SQL (remove potential SQL injection)
     * Note: Always use PreparedStatement, this is just additional safety
     * 
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove common SQL injection patterns
        return input.replaceAll("[';\"--]", "");
    }
    
    /**
     * Validate date format (yyyy-MM-dd)
     * @param dateStr Date string to validate
     * @return true if format is valid
     */
    public static boolean isDateFormatValid(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        
        return dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
    
    /**
     * Validate positive integer
     * @param value Value to validate
     * @return true if value is positive integer
     */
    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }
    
    /**
     * Validate non-negative integer
     * @param value Value to validate
     * @return true if value is non-negative integer (>= 0)
     */
    public static boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }
    
    /**
     * Validate price (positive decimal with max 2 decimal places)
     * @param price Price to validate
     * @return true if price is valid
     */
    public static boolean isPriceValid(Double price) {
        if (price == null || price < 0) {
            return false;
        }
        
        // Check max 2 decimal places
        String priceStr = String.valueOf(price);
        int decimalPos = priceStr.indexOf('.');
        if (decimalPos != -1) {
            int decimalPlaces = priceStr.length() - decimalPos - 1;
            return decimalPlaces <= 2;
        }
        
        return true;
    }
    
    // =====================================================
    // UNIT TEST
    // =====================================================
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║         VALIDATION UTIL - UNIT TEST                ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Test 1: IMEI Validation
        System.out.println("TEST 1: IMEI Validation");
        System.out.println("─────────────────────────────────────────────────────");
        String[] testIMEIs = {
            "352812345678901",  // Valid IMEI (passes Luhn)
            "123456789012345",  // Invalid checksum
            "12345678901234",   // Too short
            "1234567890123456", // Too long
            "35281234567890A",  // Contains letter
            "352812345678902"   // Invalid checksum
        };
        
        for (String imei : testIMEIs) {
            boolean formatValid = isIMEIFormatValid(imei);
            boolean checksumValid = isIMEIChecksumValid(imei);
            boolean fullValid = isIMEIValid(imei);
            String error = getIMEIValidationError(imei);
            
            System.out.printf("IMEI: %-17s | Format: %s | Checksum: %s | Valid: %s | Error: %s\n",
                imei,
                formatValid ? "✅" : "❌",
                checksumValid ? "✅" : "❌",
                fullValid ? "✅" : "❌",
                error != null ? error : "None");
        }
        System.out.println();
        
        // Test 2: Email Validation
        System.out.println("TEST 2: Email Validation");
        System.out.println("─────────────────────────────────────────────────────");
        String[] testEmails = {
            "user@example.com",       // Valid
            "test.user@domain.co.uk", // Valid
            "invalid.email",          // Invalid - no @
            "@nodomain.com",          // Invalid - no local part
            "user@",                  // Invalid - no domain
            "user@domain"             // Invalid - no TLD
        };
        
        for (String email : testEmails) {
            boolean valid = isValidEmail(email);
            String error = getEmailValidationError(email);
            System.out.printf("%-30s: %s %s\n", email, valid ? "✅" : "❌", error != null ? "(" + error + ")" : "");
        }
        System.out.println();
        
        // Test 3: Phone Validation
        System.out.println("TEST 3: Phone Validation (Vietnamese)");
        System.out.println("─────────────────────────────────────────────────────");
        String[] testPhones = {
            "0912345678",      // Valid
            "+84912345678",    // Valid
            "912345678",       // Invalid - no prefix
            "091234567",       // Invalid - too short
            "09123456789",     // Invalid - too long
            "0a12345678"       // Invalid - contains letter
        };
        
        for (String phone : testPhones) {
            boolean valid = isValidPhone(phone);
            String normalized = normalizePhone(phone);
            String error = getPhoneValidationError(phone);
            System.out.printf("%-15s: %s | Normalized: %-15s | %s\n", 
                phone, valid ? "✅" : "❌", normalized, error != null ? error : "Valid");
        }
        System.out.println();
        
        // Test 4: General Validation
        System.out.println("TEST 4: General Validation");
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("isNullOrEmpty(null): " + (isNullOrEmpty(null) ? "✅ true" : "❌ false"));
        System.out.println("isNullOrEmpty(''): " + (isNullOrEmpty("") ? "✅ true" : "❌ false"));
        System.out.println("isNullOrEmpty('  '): " + (isNullOrEmpty("  ") ? "✅ true" : "❌ false"));
        System.out.println("isNullOrEmpty('text'): " + (isNullOrEmpty("text") ? "❌ true" : "✅ false"));
        System.out.println();
        
        System.out.println("isLengthValid('test', 1, 10): " + (isLengthValid("test", 1, 10) ? "✅ true" : "❌ false"));
        System.out.println("isLengthValid('test', 5, 10): " + (isLengthValid("test", 5, 10) ? "❌ true" : "✅ false"));
        System.out.println();
        
        System.out.println("isNumeric('12345'): " + (isNumeric("12345") ? "✅ true" : "❌ false"));
        System.out.println("isNumeric('123a5'): " + (isNumeric("123a5") ? "❌ true" : "✅ false"));
        System.out.println();
        
        System.out.println("isAlphanumeric('abc123'): " + (isAlphanumeric("abc123") ? "✅ true" : "❌ false"));
        System.out.println("isAlphanumeric('abc-123'): " + (isAlphanumeric("abc-123") ? "❌ true" : "✅ false"));
        System.out.println();
        
        // Test 5: Price Validation
        System.out.println("TEST 5: Price Validation");
        System.out.println("─────────────────────────────────────────────────────");
        Double[] testPrices = {100.0, 99.99, 100.999, -50.0, 0.0};
        for (Double price : testPrices) {
            boolean valid = isPriceValid(price);
            System.out.printf("Price: %8.3f -> %s\n", price, valid ? "✅ Valid" : "❌ Invalid");
        }
        System.out.println();
        
        // Test 6: Date Format Validation
        System.out.println("TEST 6: Date Format Validation");
        System.out.println("─────────────────────────────────────────────────────");
        String[] testDates = {"2024-12-31", "2024-1-1", "31-12-2024", "2024/12/31"};
        for (String date : testDates) {
            boolean valid = isDateFormatValid(date);
            System.out.printf("%-15s: %s\n", date, valid ? "✅ Valid (yyyy-MM-dd)" : "❌ Invalid");
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              ALL TESTS COMPLETED!                  ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}