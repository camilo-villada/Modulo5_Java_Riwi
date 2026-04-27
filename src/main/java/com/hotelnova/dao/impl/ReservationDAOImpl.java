package com.hotelnova.dao.impl;

import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;

import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationDAOImpl implements ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAOImpl.class.getName());

    @Override
    public void save(Reservation res) {
        String sql = "INSERT INTO reservations (guest_id, room_id, user_id, check_in_date, check_out_date, total_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, res.getGuestId());
            pstmt.setInt(2, res.getRoomId());
            pstmt.setInt(3, res.getUserId());
            pstmt.setTimestamp(4, Timestamp.valueOf(res.getCheckInDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(res.getCheckOutDate()));
            pstmt.setBigDecimal(6, res.getTotalCost());
            pstmt.setString(7, res.getStatus().name());
            
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) res.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving reservation", e);
        }
    }

    @Override
    public void update(Reservation res) {
        String sql = "UPDATE reservations SET status = ?, total_cost = ?, check_out_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getStatus().name());
            pstmt.setBigDecimal(2, res.getTotalCost());
            pstmt.setTimestamp(3, Timestamp.valueOf(res.getCheckOutDate()));
            pstmt.setInt(4, res.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating reservation", e);
        }
    }

    @Override
    public Reservation findById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding reservation", e);
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listing all reservations", e);
        }
        return list;
    }

    @Override
    public List<Reservation> findActiveReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error listing active reservations", e);
        }
        return list;
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND status = 'ACTIVE' " +
                     "AND check_in_date < ? AND check_out_date > ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, Timestamp.valueOf(checkOut));
            pstmt.setTimestamp(3, Timestamp.valueOf(checkIn));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking room availability", e);
        }
        return false;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setId(rs.getInt("id"));
        res.setGuestId(rs.getInt("guest_id"));
        res.setRoomId(rs.getInt("room_id"));
        res.setUserId(rs.getInt("user_id"));
        res.setCheckInDate(rs.getTimestamp("check_in_date").toLocalDateTime());
        res.setCheckOutDate(rs.getTimestamp("check_out_date").toLocalDateTime());
        res.setTotalCost(rs.getBigDecimal("total_cost"));
        res.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        res.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return res;
    }
}