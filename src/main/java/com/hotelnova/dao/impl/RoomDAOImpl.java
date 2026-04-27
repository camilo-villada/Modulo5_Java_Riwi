package com.hotelnova.dao.impl;

import com.hotelnova.dao.RoomDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomDAOImpl implements RoomDAO {
    private static final Logger logger = Logger.getLogger(RoomDAOImpl.class.getName());

    @Override
    public void save(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, capacity, price_per_night, status, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setBigDecimal(4, room.getPricePerNight());
            pstmt.setString(5, room.getStatus().name());
            pstmt.setBoolean(6, room.isActive());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) room.setId(rs.getInt(1));
            }
            logger.info("HTTP Trace: POST /rooms - Room created: " + room.getRoomNumber());
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving room. Check if room_number is unique.", e);
        }
    }

    @Override
    public Room findByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding room by number", e);
        }
        return null;
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            logger.info("HTTP Trace: GET /rooms - Listing all active rooms");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listing rooms", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(RoomStatus status) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            }
            logger.info("HTTP Trace: GET /rooms/filter?status=" + status);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error filtering rooms by status", e);
        }
        return rooms;
    }

    @Override
    public void update(Room room) {
        String sql = "UPDATE rooms SET type = ?, capacity = ?, price_per_night = ?, status = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, room.getType());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setBigDecimal(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus().name());
            pstmt.setBoolean(5, room.isActive());
            pstmt.setInt(6, room.getId());
            
            pstmt.executeUpdate();
            logger.info("HTTP Trace: PATCH /rooms/" + room.getId() + " - Room updated");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating room", e);
        }
    }

    @Override
    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding room by ID", e);
        }
        return null;
    }

    @Override
    public List<Room> findByType(String type) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            }
            logger.info("HTTP Trace: GET /rooms/filter?type=" + type);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error filtering rooms by type", e);
        }
        return rooms;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setType(rs.getString("type"));
        room.setCapacity(rs.getInt("capacity"));
        room.setPricePerNight(rs.getBigDecimal("price_per_night"));
        room.setStatus(RoomStatus.valueOf(rs.getString("status")));
        room.setActive(rs.getBoolean("is_active"));
        room.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return room;
    }
}