package com.ecommerce.util;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Scrambles the password into a secure hash
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Checks if a typed password matches the saved hash during login
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}