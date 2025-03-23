package com.ctang.zephyrsanctum.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private final PasswordEncoder passwordEncoder;

    public PasswordUtils() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Stronger hashing with higher cost factor
    }

    /**
     * Hashes a password using BCrypt algorithm
     * 
     * @param password The plain text password to hash
     * @return The hashed password
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verifies if a plain text password matches a previously hashed password
     * 
     * @param rawPassword The plain text password to check
     * @param encodedPassword The previously hashed password
     * @return true if the passwords match, false otherwise
     */
    public boolean verifyPassword(String password, String passwordHash) {
        return passwordEncoder.matches(password, passwordHash);
    }
}
