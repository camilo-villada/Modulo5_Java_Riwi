package com.hotelnova.dao.impl;

import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;

import java.time.LocalDateTime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReservationDAOImpl implements ReservationDAO {
    private static final Logger logger = Logger.getLogger(ReservationDAOImpl.class.getName());

    @Override
    public void save(Reservation reservation, Connection conn) throws SQLException {
        String sql = "INSERT INTO reservations (guest_id, room_id, user_id, check_in_date, check_out_date, total_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setInt(3, reservation.getUserId());
            pstmt.setTimestamp(4, Timestamp.valueOf(reservation.getCheckInDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(reservation.getCheckOutDate()));
            pstmt.setBigDecimal(6, reservation.getTotalCost());
            pstmt.setString(7, reservation.getStatus().name());

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) reservation.setId(rs.getInt(1));
            }
            logger.info("HTTP Trace: POST /reservations - 201 CREATED");
        }
    }

    @Override
    public void update(Reservation reservation, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET status = ?, total_cost = ?, check_out_date = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getStatus().name());
            pstmt.setBigDecimal(2, reservation.getTotalCost());
            pstmt.setTimestamp(3, Timestamp.valueOf(reservation.getCheckOutDate()));
            pstmt.setInt(4, reservation.getId());
            pstmt.executeUpdate();
            logger.info("HTTP Trace: PATCH /reservations/" + reservation.getId() + " - 200 OK");
        }
    }

    @Override
    public Reservation findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("HTTP Trace: GET /reservations/" + id + " - 200 OK");
                    return mapResultSetToReservation(rs);
                }
            }
        }
        logger.info("HTTP Trace: GET /reservations/" + id + " - 404 NOT FOUND");
        return null;
    }

    @Override
    public List<Reservation> findAll(Connection conn) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        }
        logger.info("HTTP Trace: GET /reservations - 200 OK");
        return list;
    }

    @Override
    public List<Reservation> findActiveReservations(Connection conn) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = 'ACTIVE'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSetToReservation(rs));
        }
        logger.info("HTTP Trace: GET /reservations/active - 200 OK");
        return list;
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDateTime checkIn, LocalDateTime checkOut, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations " +
                "WHERE room_id = ? " +
                "AND status = 'ACTIVE' " +
                "AND check_in_date < ? " +
                "AND check_out_date > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setTimestamp(2, Timestamp.valueOf(checkOut));
            pstmt.setTimestamp(3, Timestamp.valueOf(checkIn));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean isAvailable = rs.getInt(1) == 0;
                    logger.info("HTTP Trace: GET /reservations/availability - " + (isAvailable ? "200 OK" : "409 CONFLICT"));
                    return isAvailable;
                }
            }
        }
        return false;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setGuestId(rs.getInt("guest_id"));
        reservation.setRoomId(rs.getInt("room_id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setCheckInDate(rs.getTimestamp("check_in_date").toLocalDateTime());
        reservation.setCheckOutDate(rs.getTimestamp("check_out_date").toLocalDateTime());
        reservation.setTotalCost(rs.getBigDecimal("total_cost"));
        reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return reservation;
    }
}
