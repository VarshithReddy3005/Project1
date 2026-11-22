package com.ecommerce.util;

import java.util.Scanner;

import com.ecommerce.service.AuthService;

/**
 * Secure Administrator Account Provisioning Utility
 * Run only by authorized system administrators to provision new ADMIN users.
 * Public web/Swing customer registration can never create ADMIN accounts.
 */
public class AdminSetup {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  NEXUS LUXE • AUTHORIZED ADMIN PROVISIONING");
        System.out.println("=================================================");

        AuthService authService = new AuthService();

        if (!authService.isDatabaseConnected()) {
            System.err.println("[ERROR] Cannot connect to MySQL. Check db.properties.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String username;
        String password;

        if (args.length >= 2) {
            username = args[0].trim();
            password = args[1].trim();
        } else {
            System.out.print("Enter desired Administrator username: ");
            username = scanner.hasNextLine() ? scanner.nextLine().trim() : "nexus_admin";

            System.out.print("Enter desired Administrator password: ");
            password = scanner.hasNextLine() ? scanner.nextLine().trim() : "AdminMaster@2026";
        }

        if (username.isEmpty() || password.isEmpty()) {
            System.err.println("[ERROR] Username and password cannot be empty.");
            return;
        }

        if (authService.isUsernameTaken(username)) {
            System.out.println("[INFO] Admin user '" + username + "' is already registered in MySQL.");
            System.out.println("You can sign in with this account on LoginFrame.");
            return;
        }

        boolean success = authService.register(username, password, "ADMIN");
        if (success) {
            System.out.println("[SUCCESS] Administrator account '" + username + "' created successfully in MySQL!");
            System.out.println("Role: ADMIN (BCrypt salted hash verified).");
            System.out.println("You can now log in via LoginFrame to open AdminDashboard.");
        } else {
            System.err.println("[ERROR] Failed to save administrator into database.");
        }
    }
}
