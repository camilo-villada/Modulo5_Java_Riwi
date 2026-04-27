package com.hotelnova.dao.impl;

import com.hotelnova.dao.GuestDAO;
import com.hotelnova.model.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GuestDAOImpl implements GuestDAO {
    private static final Logger logger = Logger.getLogger(GuestDAOImpl.class.getName());

    @Override
    public void save(Guest guest, Connection conn) throws SQLException {
        String sql = "INSERT INTO guests (first_name, last_name, document_number, email, phone_number) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getDocumentNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getPhoneNumber());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) guest.setId(rs.getInt(1));
            }
            logger.info("HTTP Trace: POST /guests - 201 CREATED");
        }
    }

    @Override
    public Guest findByDocument(String documentNumber, Connection conn) throws SQLException {
        String sql = "SELECT * FROM guests WHERE document_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, documentNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("HTTP Trace: GET /guests/document/" + documentNumber + " - 200 OK");
                    return mapResultSetToGuest(rs);
                }
            }
        }
        logger.info("HTTP Trace: GET /guests/document/" + documentNumber + " - 404 NOT FOUND");
        return null;
    }

    @Override
    public List<Guest> findAll(Connection conn) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) guests.add(mapResultSetToGuest(rs));
        }
        logger.info("HTTP Trace: GET /guests - 200 OK");
        return guests;
    }

    @Override
    public void update(Guest guest, Connection conn) throws SQLException {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, document_number = ?, email = ?, phone_number = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, guest.getFirstName());
            pstmt.setString(2, guest.getLastName());
            pstmt.setString(3, guest.getDocumentNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getPhoneNumber());
            pstmt.setBoolean(6, guest.isActive());
            pstmt.setInt(7, guest.getId());

            int rowsUpdated = pstmt.executeUpdate();
            logger.info("HTTP Trace: PATCH /guests/" + guest.getId() + " - " + (rowsUpdated > 0 ? "200 OK" : "404 NOT FOUND"));
        }
    }

    @Override
    public Guest findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM guests WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("HTTP Trace: GET /guests/" + id + " - 200 OK");
                    return mapResultSetToGuest(rs);
                }
            }
        }
        logger.info("HTTP Trace: GET /guests/" + id + " - 404 NOT FOUND");
        return null;
    }

    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getInt("id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setDocumentNumber(rs.getString("document_number"));
        guest.setEmail(rs.getString("email"));
        guest.setPhoneNumber(rs.getString("phone_number"));
        guest.setActive(rs.getBoolean("is_active"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        guest.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        return guest;
    }
}
