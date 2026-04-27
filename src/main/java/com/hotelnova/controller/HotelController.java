package com.hotelnova.controller;

import com.hotelnova.dao.impl.GuestDAOImpl;
import com.hotelnova.dao.impl.ReservationDAOImpl;
import com.hotelnova.dao.impl.RoomDAOImpl;
import com.hotelnova.dao.impl.UserDAOImpl;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.exception.AuthenticationException;
import com.hotelnova.model.*;
import com.hotelnova.service.AuthService;
import com.hotelnova.service.ReservationService;
import com.hotelnova.util.CSVExportUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelController {
    private static final Logger logger = Logger.getLogger(HotelController.class.getName());

    private final AuthService authService;
    private final ReservationService reservationService;
    private final ReservationDAOImpl reservationDAO;
    private final RoomDAOImpl roomDAO;
    private final GuestDAOImpl guestDAO;
    private final UserDAOImpl userDAO;

    public HotelController() {
        this.userDAO = new UserDAOImpl();
        this.roomDAO = new RoomDAOImpl();
        this.guestDAO = new GuestDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();

        this.authService = new AuthService(userDAO);
        this.reservationService = new ReservationService(reservationDAO, roomDAO, guestDAO);
    }

    public User login(String username, String password) throws AuthenticationException {
        return authService.login(username, password);
    }

    public List<Room> getAvailableRooms() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return roomDAO.findByStatus(RoomStatus.AVAILABLE, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing available rooms", e);
        }
    }

    public List<Room> getAllRooms() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return roomDAO.findAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing rooms", e);
        }
    }

    public List<Room> getRoomsByStatus(RoomStatus status) {
        logger.info("HTTP Trace: GET /rooms/filter?status=" + status + " - 200 OK");
        try (Connection conn = DatabaseConnection.getConnection()) {
            return roomDAO.findByStatus(status, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering rooms by status", e);
        }
    }

    public List<Room> getRoomsByType(String type) {
        logger.info("HTTP Trace: GET /rooms/filter?type=" + type + " - 200 OK");
        try (Connection conn = DatabaseConnection.getConnection()) {
            return roomDAO.findByType(type, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering rooms by type", e);
        }
    }

    public void saveRoom(Room room) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Room existingRoom = roomDAO.findByNumber(room.getRoomNumber(), conn);
            if (existingRoom != null) {
                throw new Exception("A room with number " + room.getRoomNumber() + " already exists.");
            }
            roomDAO.save(room, conn);
            logger.info("HTTP Trace: POST /rooms - 201 CREATED");
        } catch (SQLException e) {
            throw new Exception("Error saving room", e);
        }
    }

    public void updateRoomPrice(int id, BigDecimal price) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Room room = roomDAO.findById(id, conn);
            if (room == null) {
                throw new IllegalArgumentException("Room not found with ID: " + id);
            }

            room.setPricePerNight(price);
            roomDAO.update(room, conn);
            logger.info("HTTP Trace: PATCH /rooms/" + id + " - 200 OK");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating room price", e);
        }
    }

    public void registerGuest(Guest guest) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            guestDAO.save(guest, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving guest", e);
        }
    }

    public Guest findGuestByDocument(String document) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return guestDAO.findByDocument(document, conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding guest", e);
        }
    }

    public List<Guest> getAllGuests() {
        logger.info("HTTP Trace: GET /guests - 200 OK");
        try (Connection conn = DatabaseConnection.getConnection()) {
            return guestDAO.findAll(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error listing guests", e);
        }
    }

    public void checkIn(Reservation reservation) throws Exception {
        reservationService.processCheckIn(reservation);
    }

    public void checkOut(int reservationId) throws Exception {
        reservationService.processCheckOut(reservationId);
    }

    public void exportReservationsToCSV() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Room> rooms = roomDAO.findAll(conn);
            List<Reservation> activeReservations = reservationDAO.findActiveReservations(conn);

            CSVExportUtil.exportRooms(rooms);
            CSVExportUtil.exportActiveReservations(activeReservations);
            logger.info("HTTP Trace: GET /exports/csv - 200 OK");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "HTTP Trace: GET /exports/csv - 500 INTERNAL SERVER ERROR", e);
            throw new RuntimeException("Error exporting CSV files", e);
        }
    }
}
