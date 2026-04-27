package com.hotelnova.dao.impl;

import com.hotelnova.dao.RoomDAO;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoomDAOImpl implements RoomDAO {
    private static final Logger logger = Logger.getLogger(RoomDAOImpl.class.getName());

    @Override
    public void save(Room room, Connection conn) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, type, capacity, price_per_night, status, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            logger.info("HTTP Trace: POST /rooms - 201 CREATED");
        }
    }

    @Override
    public Room findByNumber(String roomNumber, Connection conn) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRoom(rs);
            }
        }
        return null;
    }

    @Override
    public List<Room> findAll(Connection conn) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            logger.info("HTTP Trace: GET /rooms - 200 OK");
        }
        return rooms;
    }

    @Override
    public List<Room> findByStatus(RoomStatus status, Connection conn) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = ? ORDER BY room_number";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            }
            logger.info("HTTP Trace: GET /rooms/filter?status=" + status + " - 200 OK");
        }
        return rooms;
    }

    @Override
    public void update(Room room, Connection conn) throws SQLException {
        String sql = "UPDATE rooms SET type = ?, capacity = ?, price_per_night = ?, status = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getType());
            pstmt.setInt(2, room.getCapacity());
            pstmt.setBigDecimal(3, room.getPricePerNight());
            pstmt.setString(4, room.getStatus().name());
            pstmt.setBoolean(5, room.isActive());
            pstmt.setInt(6, room.getId());

            pstmt.executeUpdate();
            logger.info("HTTP Trace: PATCH /rooms/" + room.getId() + " - 200 OK");
        }
    }

    @Override
    public void delete(int id, Connection conn) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            logger.info("HTTP Trace: DELETE /rooms/" + id + " - " + (rowsDeleted > 0 ? "200 OK" : "404 NOT FOUND"));
        }
    }

    @Override
    public Room findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToRoom(rs);
            }
        }
        return null;
    }

    @Override
    public List<Room> findByType(String type, Connection conn) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ? ORDER BY room_number";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) rooms.add(mapResultSetToRoom(rs));
            }
            logger.info("HTTP Trace: GET /rooms/filter?type=" + type + " - 200 OK");
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
