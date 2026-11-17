package com.ecommerce;
import com.ecommerce.model.User;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.util.PasswordUtil;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("Starting database test...");

        // 1. Create a dummy password
        String myPassword = "SuperSecretPassword123!";

        // 2. Hash the password securely
        String hashedPassword = PasswordUtil.hashPassword(myPassword);
        System.out.println("Generated Hash: " + hashedPassword);

        // 3. Create a User object with the hashed password
        User newAdmin = new User("admin_bhanu", hashedPassword, "ADMIN");

        // 4. Send the user to the DAO to be saved in MySQL
        UserDAO dao = new UserDAO();
        boolean isSaved = dao.registerUser(newAdmin);

        if (isSaved) {
            System.out.println("SUCCESS: User saved to the database!");
        } else {
            System.out.println("FAILED: Could not save user. Check your MySQL connection.");
        }
    }
}