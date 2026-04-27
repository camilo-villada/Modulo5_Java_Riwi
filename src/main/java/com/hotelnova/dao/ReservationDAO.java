package com.hotelnova.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.hotelnova.model.Reservation;

public interface ReservationDAO {
    
    void save(Reservation reservation);
    void update(Reservation reservation);
    Reservation findById(int id);
    List<Reservation> findAll();    
    List<Reservation> findActiveReservations();

    boolean isRoomAvailable(int roomId, LocalDateTime checkIn, LocalDateTime checkOut);
}
