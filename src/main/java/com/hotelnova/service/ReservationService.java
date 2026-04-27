package com.hotelnova.service;

import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.dao.RoomDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.model.*;
import com.hotelnova.util.ConfigManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private static final Logger logger = Logger.getLogger(ReservationService.class.getName());

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
    }

    // ... (Aquí iría el processCheckIn que te pasé antes)

    public void processCheckOut(int reservationId) throws Exception {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false); // REQUISITO: Transacción

            Reservation res = reservationDAO.findById(reservationId);
            if (res == null || res.getStatus() != ReservationStatus.ACTIVE) {
                throw new Exception("There is no active reservation with that ID.");
            }

            Room room = roomDAO.findById(res.getRoomId());
            
            // 1. Calcular costo (Noches * Precio)
            long nights = Duration.between(res.getCheckInDate(), res.getCheckOutDate()).toDays();
            if (nights <= 0) nights = 1; // Mínimo cobrar una noche

            BigDecimal subtotal = room.getPricePerNight().multiply(new BigDecimal(nights));
            
            // 2. Aplicar IVA desde config.properties
            double ivaPercent = ConfigManager.getDoubleProperty("iva");
            BigDecimal totalWithTax = subtotal.multiply(new BigDecimal(1 + ivaPercent));

            // 3. Actualizar Reserva
            res.setTotalCost(totalWithTax);
            res.setStatus(ReservationStatus.FINISHED);
            reservationDAO.update(res);

            // 4. Liberar Habitación
            room.setStatus(RoomStatus.AVAILABLE);
            roomDAO.update(room);

            conn.commit();
            logger.info("Check-out exitoso. Total: " + totalWithTax);

        } catch (Exception e) {
            conn.rollback();
            logger.log(Level.SEVERE, "Error in the Check-out. Rollback executed.", e);
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}