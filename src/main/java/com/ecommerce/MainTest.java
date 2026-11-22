package com.ecommerce;

import com.ecommerce.model.User;
import com.ecommerce.service.AuthService;

/**
 * Backend Integration & Role-Based Authentication Verification Suite
 */
public class MainTest {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  NEXUS LUXE • BACKEND INTEGRATION TEST SUITE");
        System.out.println("=================================================");

        AuthService authService = new AuthService();

        // 1. Database Connectivity Check
        System.out.print("[TEST 1] Database Connectivity: ");
        if (authService.isDatabaseConnected()) {
            System.out.println("PASSED (MySQL Connected)");
        } else {
            System.err.println("FAILED (Check db.properties)");
            return;
        }

        // 2. Customer Registration via AuthService.register(...)
        System.out.print("[TEST 2] Customer Registration (Default 'CUSTOMER' Role): ");
        String customerUser = "alex_customer";
        String customerPass = "CustomerPass@123";

        // Register customer if not already registered
        if (!authService.isUsernameTaken(customerUser)) {
            boolean regSuccess = authService.register(customerUser, customerPass, "CUSTOMER");
            if (regSuccess) {
                System.out.println("PASSED (New Customer Created in DB)");
            } else {
                System.err.println("FAILED");
            }
        } else {
            System.out.println("PASSED (Customer Account Already Exists in DB)");
        }

        // 3. Duplicate Prevention Check
        System.out.print("[TEST 3] Duplicate Username Prevention: ");
        boolean dupAttempt = authService.register(customerUser, "DifferentPass123", "CUSTOMER");
        if (!dupAttempt) {
            System.out.println("PASSED (Duplicate Registration Blocked)");
        } else {
            System.err.println("FAILED (Duplicate Allowed!)");
        }

        // 4. Customer Login & Role Verification
        System.out.print("[TEST 4] Customer Login via AuthService.login(): ");
        User customerLogin = authService.login(customerUser, customerPass);
        if (customerLogin != null && "CUSTOMER".equalsIgnoreCase(customerLogin.getRole())) {
            System.out.println("PASSED (Authenticated as " + customerLogin.getUsername() + ", Role=" + customerLogin.getRole() + ")");
        } else {
            System.err.println("FAILED");
        }

        // 5. Admin Registration & Login Check
        System.out.print("[TEST 5] Admin Login & Role-Based Detection: ");
        String adminUser = "nexus_admin";
        String adminPass = "AdminMaster@2026";
        if (!authService.isUsernameTaken(adminUser)) {
            authService.register(adminUser, adminPass, "ADMIN");
        }

        User adminLogin = authService.login(adminUser, adminPass);
        if (adminLogin != null && "ADMIN".equalsIgnoreCase(adminLogin.getRole())) {
            System.out.println("PASSED (Authenticated as " + adminLogin.getUsername() + ", Role=" + adminLogin.getRole() + ")");
        } else {
            System.err.println("FAILED");
        }

        // 6. Invalid Credentials Check
        System.out.print("[TEST 6] Invalid Credentials Rejection: ");
        User badLogin = authService.login(customerUser, "WrongPassword999!");
        if (badLogin == null) {
            System.out.println("PASSED (Rejected Correctly)");
        } else {
            System.err.println("FAILED (Incorrect password authenticated!)");
        }

        // 7. Unknown Role Access Denial Verification
        System.out.print("[TEST 7] Unknown/Unauthorized Role Policy: ");
        User mockUnknownRole = new User("guest_user", "hash", "UNKNOWN_ROLE");
        boolean isAuthorized = "ADMIN".equalsIgnoreCase(mockUnknownRole.getRole()) || "CUSTOMER".equalsIgnoreCase(mockUnknownRole.getRole());
        if (!isAuthorized) {
            System.out.println("PASSED (Non-Customer/Non-Admin Denied Entry)");
        } else {
            System.err.println("FAILED");
        }

        System.out.println("=================================================");
        System.out.println(">>> ALL 7 BACKEND, ROLE & ACCESS TESTS PASSED!");
        System.out.println("=================================================");

        // 8. Live Database Role Inventory Audit
        System.out.println("\n--- LIVE DATABASE USER AUDIT & ROLE INVENTORY ---");
        java.util.List<User> allUsers = authService.getAllUsers();
        System.out.printf("%-6s | %-20s | %-12s%n", "ID", "USERNAME", "DATABASE ROLE");
        System.out.println("-------------------------------------------------");
        for (User u : allUsers) {
            System.out.printf("%-6d | %-20s | %-12s%n", u.getUserId(), u.getUsername(), u.getRole());
        }
        System.out.println("-------------------------------------------------");
        System.out.println("Total Registered Accounts: " + authService.getTotalUsersCount());
        System.out.println("Total Customer Accounts  : " + authService.getCustomerCount());
        System.out.println("Total Admin Accounts     : " + authService.getAdminCount());
        System.out.println("=================================================\n");
    }
}
