package org.coffee.util;

import org.mindrot.jbcrypt.BCrypt; // Import the jBCrypt class

public final class PasswordUtil {

    // Define the work factor (log rounds). Higher means more computation time.
    // 10-12 is a common range. Adjust based on your server's performance and security needs.
    private static final int WORK_FACTOR = 12;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PasswordUtil() {
        throw new IllegalStateException("Utility class should not be instantiated.");
    }

    /**
     * Hashes a plain text password using BCrypt with a randomly generated salt.
     *
     * @param plainPassword The plain text password to hash. Must not be null or empty.
     * @return The BCrypt hash string (including the salt and work factor).
     * @throws IllegalArgumentException if plainPassword is null or empty.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }

        // BCrypt.gensalt() automatically generates a random salt
        // The work factor determines the complexity (higher is slower/more secure)
        String salt = BCrypt.gensalt(WORK_FACTOR);

        // BCrypt.hashpw hashes the password using the generated salt
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Checks if a given plain text password matches a stored BCrypt hash.
     *
     * @param plainPassword  The plain text password attempt. Must not be null.
     * @param hashedPassword The stored BCrypt hash string (which includes salt and work factor). Must not be null or empty.
     * @return {@code true} if the password matches the hash, {@code false} otherwise.
     * @throws IllegalArgumentException if either argument is null or if hashedPassword is empty.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Plain password cannot be null.");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty.");
        }

        try {
            // BCrypt.checkpw handles extracting the salt from the stored hash
            // and comparing the result. It's robust against timing attacks.
            System.out.println(hashPassword(plainPassword));
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // This can happen if the hashedPassword format is invalid (not a real BCrypt hash)
            // Log this occurrence for security monitoring if desired
            System.err.println("Attempted to check password against an invalid hash format: " + e.getMessage());
            return false;
        }
    }
}