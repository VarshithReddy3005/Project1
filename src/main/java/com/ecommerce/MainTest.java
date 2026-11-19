package com.ecommerce;

import com.ecommerce.model.User;
import com.ecommerce.service.AuthService;

public class MainTest {
    public static void main(String[] args) {
        AuthService authService = new AuthService();

        // Test login using the admin_bhanu account you already saved
        User loggedIn = authService.login("admin_bhanu", "SuperSecretPassword123!");

        if (loggedIn != null) {
            System.out.println("LOGIN SUCCESS! Welcome " + loggedIn.getUsername() + " | Role: " + loggedIn.getRole());
        } else {
            System.out.println("LOGIN FAILED: Check credentials or db.properties.");
        }
    }
}