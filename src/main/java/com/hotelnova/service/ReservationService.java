package com.hotelnova.service;

import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.dao.RoomDAO;
import com.hotelnova.dao.GuestDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.exception.InvalidReservationException;
import com.hotelnova.exception.RoomNotAvailableException;
import com.hotelnova.model.*;
import com.hotelnova.util.ConfigManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationService {
    @FunctionalInterface
    interface ConnectionProvider {
        Connection getConnection() throws SQLException;
    }

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final GuestDAO guestDAO;
    private final ConnectionProvider connectionProvider;
    private static final Logger logger = Logger.getLogger(ReservationService.class.getName());

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this(reservationDAO, roomDAO, null);
    }

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, GuestDAO guestDAO) {
        this(reservationDAO, roomDAO, guestDAO, DatabaseConnection::getConnection);
    }

    ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, GuestDAO guestDAO, ConnectionProvider connectionProvider) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.guestDAO = guestDAO;
        this.connectionProvider = connectionProvider;
    }

    // Retrieves all currently active reservations.
    public java.util.List<Reservation> getActiveReservations() throws InvalidReservationException {
        try (Connection conn = connectionProvider.getConnection()) {
            return reservationDAO.findActiveReservations(conn);
        } catch (SQLException e) {
            throw new InvalidReservationException("Error listing active reservations.");
        }
    }

    // Processes a check-in transaction, verifying availability and updating room status.
    public void processCheckIn(Reservation reservation) throws Exception {
        Connection conn = null;
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            if (reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null
                    || !reservation.getCheckInDate().isBefore(reservation.getCheckOutDate())) {
                logger.info("HTTP Trace: POST /reservations/check-in - 400 BAD REQUEST");
                throw new InvalidReservationException("Check-in date must be before check-out date.");
            }

            if (guestDAO != null) {
                Guest guest = guestDAO.findById(reservation.getGuestId(), conn);
                if (guest == null || !guest.isActive()) {
                    logger.info("HTTP Trace: POST /reservations/check-in - 400 BAD REQUEST");
                    throw new InvalidReservationException("The guest is inactive or does not exist.");
                }
            }

            Room room = roomDAO.findById(reservation.getRoomId(), conn);
            if (room == null || !room.isActive()) {
                logger.info("HTTP Trace: POST /reservations/check-in - 404 NOT FOUND");
                throw new InvalidReservationException("The room does not exist or is inactive.");
            }

            boolean isAvailable = reservationDAO.isRoomAvailable(
                    reservation.getRoomId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate(),
                    conn
            );
            if (!isAvailable) {
                logger.info("HTTP Trace: POST /reservations/check-in - 409 CONFLICT");
                throw new RoomNotAvailableException("The room is not available for the selected dates.");
            }

            reservationDAO.save(reservation, conn);

            room.setStatus(RoomStatus.OCCUPIED);
            roomDAO.update(room, conn);

            conn.commit();
            logger.info("HTTP Trace: POST /reservations/check-in - 200 OK");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "HTTP Trace: POST /reservations/check-in - 500 INTERNAL SERVER ERROR", rollbackEx);
                }
            }
            if (!(e instanceof InvalidReservationException) && !(e instanceof RoomNotAvailableException)) {
                logger.log(Level.SEVERE, "HTTP Trace: POST /reservations/check-in - 500 INTERNAL SERVER ERROR", e);
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "HTTP Trace: POST /reservations/check-in - 500 INTERNAL SERVER ERROR", e);
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "HTTP Trace: POST /reservations/check-in - 500 INTERNAL SERVER ERROR", e);
                }
            }
        }
    }

    // Processes a check-out transaction, calculating the total cost including VAT and freeing the room.
    public void processCheckOut(int reservationId) throws Exception {
        Connection conn = null;
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(false);

            Reservation reservation = reservationDAO.findById(reservationId, conn);
            if (reservation == null || reservation.getStatus() != ReservationStatus.ACTIVE) {
                logger.info("HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 404 NOT FOUND");
                throw new InvalidReservationException("No active reservation exists with that ID.");
            }

            Room room = roomDAO.findById(reservation.getRoomId(), conn);
            if (room == null) {
                logger.info("HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 404 NOT FOUND");
                throw new InvalidReservationException("The room associated with the reservation does not exist.");
            }

            long nights = ChronoUnit.DAYS.between(
                    reservation.getCheckInDate().toLocalDate(),
                    reservation.getCheckOutDate().toLocalDate()
            );
            if (nights <= 0) {
                nights = 1;
            }

            BigDecimal subtotal = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            String vatValue = ConfigManager.getProperty("vat", "iva");
            if (vatValue == null || vatValue.isBlank()) {
                logger.info("HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 500 INTERNAL SERVER ERROR");
                throw new IllegalStateException("VAT configuration is required.");
            }

            BigDecimal vatRate = new BigDecimal(vatValue);
            BigDecimal totalWithTax = subtotal
                    .add(subtotal.multiply(vatRate))
                    .setScale(2, RoundingMode.HALF_UP);

            reservation.setTotalCost(totalWithTax);
            reservation.setStatus(ReservationStatus.FINISHED);
            reservationDAO.update(reservation, conn);

            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room, conn);

            conn.commit();
            logger.info("HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 200 OK");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 500 INTERNAL SERVER ERROR", rollbackEx);
                }
            }
            if (!(e instanceof InvalidReservationException)) {
                logger.log(Level.SEVERE, "HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 500 INTERNAL SERVER ERROR", e);
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 500 INTERNAL SERVER ERROR", e);
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "HTTP Trace: PATCH /reservations/" + reservationId + "/check-out - 500 INTERNAL SERVER ERROR", e);
                }
            }
        }
    }
}
