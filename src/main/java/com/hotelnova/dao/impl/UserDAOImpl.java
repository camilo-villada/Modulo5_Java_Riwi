package com.hotelnova.dao.impl;

import com.hotelnova.dao.UserDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, password, role, is_active) VALUES (?, ?, ?, ?)";
        // Use try-with-resources to ensure resources are always closed
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            
            pstmt.executeUpdate();
            
            // Get the auto-generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("Simulating HTTP trace: POST /users - User created: " + user.getUsername());
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving user", e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by username", e);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listing users", e);
        }
        return users;
    }

    // update, delete and findById would follow the same JDBC logic...
    
    @Override
    public void update(User user) { /* Similar implementation to save */ }

    @Override
    public void delete(int id) { /* Implementation with DELETE FROM users WHERE id = ? */ }

    @Override
    public User findById(int id) { /* Implementation with SELECT * FROM users WHERE id = ? */ return null; }

    /**
     * Helper method to convert a ResultSet into a User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}