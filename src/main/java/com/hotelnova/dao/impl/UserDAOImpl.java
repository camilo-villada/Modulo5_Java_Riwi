package com.hotelnova.dao.impl;

import com.hotelnova.dao.UserDAO;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = Logger.getLogger(UserDAOImpl.class.getName());

    @Override
    public void save(User user, Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, is_active) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
            logger.info("HTTP Trace: POST /users - 201 CREATED");
        }
    }

    @Override
    public User findByUsername(String username, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("HTTP Trace: GET /users/username/" + username + " - 200 OK");
                    return mapResultSetToUser(rs);
                }
            }
        }
        logger.info("HTTP Trace: GET /users/username/" + username + " - 404 NOT FOUND");
        return null;
    }

    @Override
    public List<User> findAll(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        logger.info("HTTP Trace: GET /users - 200 OK");
        return users;
    }
    
    @Override
    public void update(User user, Connection conn) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setInt(5, user.getId());

            int rowsUpdated = pstmt.executeUpdate();
            logger.info("HTTP Trace: PATCH /users/" + user.getId() + " - " + (rowsUpdated > 0 ? "200 OK" : "404 NOT FOUND"));
        }
    }

    @Override
    public void delete(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            logger.info("HTTP Trace: DELETE /users/" + id + " - " + (rowsDeleted > 0 ? "200 OK" : "404 NOT FOUND"));
        }
    }

    @Override
    public User findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("HTTP Trace: GET /users/" + id + " - 200 OK");
                    return mapResultSetToUser(rs);
                }
            }
        }
        logger.info("HTTP Trace: GET /users/" + id + " - 404 NOT FOUND");
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        user.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return user;
    }
}
