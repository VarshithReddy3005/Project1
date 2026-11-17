package com.ecommerce.dao;

import com.ecommerce.model.User;
import com.ecommerce.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDAO {
    
    // This method takes a User object and saves it to the MySQL database
    public boolean registerUser(User user) {
        // The question marks are placeholders for our prepared statement
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Replace the question marks with the actual user data
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash()); 
            pstmt.setString(3, user.getRole());
            
            // Execute the command and check if rows were affected
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}