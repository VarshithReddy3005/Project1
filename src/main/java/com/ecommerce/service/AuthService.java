package com.ecommerce.service;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.model.User;
import com.ecommerce.util.PasswordUtil;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public boolean register(String username, String plainPassword, String role) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }
        String hashed = PasswordUtil.hashPassword(plainPassword);
        User newUser = new User(username.trim(), hashed, role);
        return userDAO.registerUser(newUser);
    }

    public User login(String username, String plainPassword) {
        User user = userDAO.findByUsername(username);
        if (user != null && PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            return user; // Login successful; returns User with role (ADMIN or CUSTOMER)
        }
        return null; // Invalid username or password
    }
}