package com.hotelnova.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.hotelnova.model.Reservation;

public interface ReservationDAO {
    
    void save(Reservation reservation, Connection conn) throws SQLException;
    void update(Reservation reservation, Connection conn) throws SQLException;
    Reservation findById(int id, Connection conn) throws SQLException;
    List<Reservation> findAll(Connection conn) throws SQLException;
    List<Reservation> findActiveReservations(Connection conn) throws SQLException;

    boolean isRoomAvailable(int roomId, LocalDateTime checkIn, LocalDateTime checkOut, Connection conn) throws SQLException;
}
