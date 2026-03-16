package com.techshop.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Authentication Utility for Password Management
 * Uses BCrypt for secure password hashing
 * 
 * @author TuanVH - Iteration 2
 */
public class AuthenticationUtil {
    
    // BCrypt work factor (cost): 10 is recommended for good security/performance balance
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Hash a plain text password using BCrypt
     * @param plainPassword The plain text password to hash
     * @return BCrypt hashed password (60 characters)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Generate salt and hash password
        String salt = BCrypt.gensalt(BCRYPT_ROUNDS);
        return BCrypt.hashpw(plainPassword, salt);
    }
    
    /**
     * Verify if a plain text password matches a hashed password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The BCrypt hashed password from database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            System.err.println("Invalid BCrypt hash: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate password strength
     * Requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter  
     * - At least one digit
     * - At least one special character
     * 
     * @param password Password to validate
     * @return true if password meets requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null) {
            return false;
        }
        
        // Minimum length check
        if (password.length() < 8) {
            return false;
        }
        
        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // Check for special character
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get password strength feedback for user
     * @param password Password to evaluate
     * @return Descriptive feedback message
     */
    public static String getPasswordStrengthFeedback(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter (A-Z)";
        }
        
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter (a-z)";
        }
        
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit (0-9)";
        }
        
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character (!@#$%^&*...)";
        }
        
        return "Password is strong";
    }
    
    /**
     * Generate a secure random token for password reset
     * @return Random 32-character hex string
     */
    public static String generateResetToken() {
        java.security.SecureRandom random = new java.security.SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        
        // Convert to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
    
    /**
     * Calculate token expiry time (1 hour from now)
     * @return Timestamp for 1 hour in the future
     */
    public static java.time.LocalDateTime getTokenExpiryTime() {
        return java.time.LocalDateTime.now().plusHours(1);
    }
    
    /**
     * Check if a reset token has expired
     * @param expiryTime The expiry timestamp from database
     * @return true if token has expired
     */
    public static boolean isTokenExpired(java.time.LocalDateTime expiryTime) {
        if (expiryTime == null) {
            return true;
        }
        return java.time.LocalDateTime.now().isAfter(expiryTime);
    }
    
    // =====================================================
    // UNIT TEST
    // =====================================================
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║       AUTHENTICATION UTIL - UNIT TEST              ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Test 1: Password Hashing
        System.out.println("TEST 1: Password Hashing");
        System.out.println("─────────────────────────────────────────────────────");
        String plainPassword = "SecurePass123!";
        String hashedPassword = hashPassword(plainPassword);
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Hashed password: " + hashedPassword);
        System.out.println("Hash length: " + hashedPassword.length() + " characters");
        System.out.println("✅ Password hashed successfully\n");
        
        // Test 2: Password Verification
        System.out.println("TEST 2: Password Verification");
        System.out.println("─────────────────────────────────────────────────────");
        boolean correctMatch = verifyPassword(plainPassword, hashedPassword);
        boolean wrongMatch = verifyPassword("WrongPassword", hashedPassword);
        System.out.println("Correct password match: " + (correctMatch ? "✅ YES" : "❌ NO"));
        System.out.println("Wrong password match: " + (wrongMatch ? "❌ YES (ERROR!)" : "✅ NO (correct)"));
        System.out.println();
        
        // Test 3: Password Strength Validation
        System.out.println("TEST 3: Password Strength Validation");
        System.out.println("─────────────────────────────────────────────────────");
        String[] testPasswords = {
            "short",                    // Too short
            "NoDigitsOrSpecial",        // No digit or special char
            "nouppercas3!",             // No uppercase
            "NOLOWERCASE123!",          // No lowercase
            "NoSpecialChar123",         // No special char
            "ValidPass123!"             // Valid
        };
        
        for (String pwd : testPasswords) {
            boolean isStrong = isPasswordStrong(pwd);
            String feedback = getPasswordStrengthFeedback(pwd);
            System.out.printf("%-25s: %s\n", pwd, (isStrong ? "✅ STRONG" : "❌ WEAK - " + feedback));
        }
        System.out.println();
        
        // Test 4: Reset Token Generation
        System.out.println("TEST 4: Reset Token Generation");
        System.out.println("─────────────────────────────────────────────────────");
        String token1 = generateResetToken();
        String token2 = generateResetToken();
        System.out.println("Token 1: " + token1);
        System.out.println("Token 2: " + token2);
        System.out.println("Tokens are unique: " + (!token1.equals(token2) ? "✅ YES" : "❌ NO"));
        System.out.println("Token length: " + token1.length() + " characters");
        System.out.println();
        
        // Test 5: Token Expiry
        System.out.println("TEST 5: Token Expiry Time");
        System.out.println("─────────────────────────────────────────────────────");
        java.time.LocalDateTime expiryTime = getTokenExpiryTime();
        java.time.LocalDateTime pastTime = java.time.LocalDateTime.now().minusHours(2);
        System.out.println("Current time: " + java.time.LocalDateTime.now());
        System.out.println("Expiry time (1 hour): " + expiryTime);
        System.out.println("Future token expired: " + (isTokenExpired(expiryTime) ? "❌ YES (ERROR!)" : "✅ NO (correct)"));
        System.out.println("Past token expired: " + (isTokenExpired(pastTime) ? "✅ YES (correct)" : "❌ NO (ERROR!)"));
        System.out.println();
        
        // Test 6: BCrypt Same Password Different Hash
        System.out.println("TEST 6: Same Password, Different Hash (BCrypt Salt)");
        System.out.println("─────────────────────────────────────────────────────");
        String samePassword = "TestPassword123!";
        String hash1 = hashPassword(samePassword);
        String hash2 = hashPassword(samePassword);
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Hashes are different: " + (!hash1.equals(hash2) ? "✅ YES (BCrypt salt working)" : "❌ NO (ERROR!)"));
        System.out.println("Both verify correctly: " + 
            (verifyPassword(samePassword, hash1) && verifyPassword(samePassword, hash2) ? "✅ YES" : "❌ NO"));
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              ALL TESTS COMPLETED!                  ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
}