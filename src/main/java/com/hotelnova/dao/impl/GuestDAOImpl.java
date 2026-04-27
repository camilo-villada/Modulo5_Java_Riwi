package com.hotelnova.dao.impl;

import com.hotelnova.dao.GuestDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.model.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuestDAOImpl implements GuestDAO {
    private static final Logger logger = Logger.getLogger(GuestDAOImpl.class.getName());

    @Override
    public void save(Guest guest) {
        String sql = "INSERT INTO guests (first_name, last_name, document_number, email, phone_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getDocumentNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getPhoneNumber());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) guest.setId(rs.getInt(1));
            }
            logger.info("HTTP Trace: POST /guests - Created: " + guest.getDocumentNumber());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving guest", e);
        }
    }

    @Override
    public Guest findByDocument(String documentNumber) {
        String sql = "SELECT * FROM guests WHERE document_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documentNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToGuest(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding guest", e);
        }
        return null;
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) guests.add(mapResultSetToGuest(rs));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listing guests", e);
        }
        return guests;
    }

    @Override
    public void update(Guest guest) { /* Implementar similar a save con UPDATE */ }

    @Override
    public Guest findById(int id) { /* Implementar con SELECT por ID */ return null; }

    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getInt("id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setDocumentNumber(rs.getString("document_number"));
        guest.setEmail(rs.getString("email"));
        guest.setPhoneNumber(rs.getString("phone_number"));
        guest.setActive(rs.getBoolean("is_active"));
        return guest;
    }
}