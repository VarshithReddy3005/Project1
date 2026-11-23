package com.ecommerce.service;

import java.sql.Connection;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.DatabaseConnection;
import com.ecommerce.util.PasswordUtil;

/**
 * Authentication and Registration Service
 * Handles secure user authentication, password hashing, user registration,
 * and user statistics without bypassing DAO layer.
 */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Checks if a username is already registered in the database.
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return userDAO.findByUsername(username.trim()) != null;
    }

    /**
     * Registers a new customer with secure BCrypt password hashing.
     * @param username The desired username
     * @param plainPassword The plain text password to be hashed
     * @param role The user role (e.g. "CUSTOMER" or "ADMIN")
     * @return true if user was successfully saved in the database, false otherwise
     */
    public boolean register(String username, String plainPassword, String role) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }

        String cleanUsername = username.trim();

        // Check for duplicate username
        if (isUsernameTaken(cleanUsername)) {
            return false;
        }

        // Securely hash password using BCrypt
        String hashed = PasswordUtil.hashPassword(plainPassword);
        String userRole = (role != null && !role.trim().isEmpty()) ? role.trim() : "CUSTOMER";
        User newUser = new User(cleanUsername, hashed, userRole);

        // Store user in the existing database
        return userDAO.registerUser(newUser);
    }

    /**
     * Authenticates an existing user against database records.
     * @param username The username entered
     * @param plainPassword The plain text password entered
     * @return User object on successful authentication, null on failure
     */
    public User login(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user != null && PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            return user; // Login successful; returns database User with role
        }
        return null; // Invalid username or password
    }

    /**
     * Retrieves live customer count from database.
     */
    public int getCustomerCount() {
        return userDAO.getCustomerCount();
    }

    /**
     * Retrieves live admin count from database.
     */
    public int getAdminCount() {
        return userDAO.getAdminCount();
    }

    /**
     * Retrieves all users currently in the database.
     */
    public java.util.List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Retrieves total user count from database.
     */
    public int getTotalUsersCount() {
        return userDAO.getTotalUsersCount();
    }

    /**
     * Verifies active connectivity to the database.
     * @return true if database connection can be established, false otherwise
     */
    public boolean isDatabaseConnected() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
