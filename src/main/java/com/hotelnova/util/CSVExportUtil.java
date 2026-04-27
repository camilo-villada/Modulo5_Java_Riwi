package com.hotelnova.util;

import com.hotelnova.model.Room;
import com.hotelnova.model.Reservation;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVExportUtil {
    private static final Logger logger = Logger.getLogger(CSVExportUtil.class.getName());
    public static final String ROOMS_EXPORT_FILE = "rooms_export.csv";
    public static final String ACTIVE_RESERVATIONS_EXPORT_FILE = "active_reservations.csv";

    public static void exportRooms(List<Room> rooms) {
        exportRooms(rooms, ROOMS_EXPORT_FILE);
    }

    public static void exportRooms(List<Room> rooms, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,room_number,type,capacity,price_per_night,status,is_active");
            for (Room room : rooms) {
                writer.printf(
                        "%d,%s,%s,%d,%s,%s,%s%n",
                        room.getId(),
                        escapeCsv(room.getRoomNumber()),
                        escapeCsv(room.getType()),
                        room.getCapacity(),
                        room.getPricePerNight() != null ? room.getPricePerNight().toPlainString() : "",
                        room.getStatus() != null ? room.getStatus().name() : "",
                        room.isActive()
                );
            }
            logger.info("HTTP Trace: GET /exports/rooms - 200 OK");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "HTTP Trace: GET /exports/rooms - 500 INTERNAL SERVER ERROR", e);
        }
    }

    public static void exportActiveReservations(List<Reservation> reservations) {
        exportActiveReservations(reservations, ACTIVE_RESERVATIONS_EXPORT_FILE);
    }

    public static void exportActiveReservations(List<Reservation> reservations, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,guest_id,room_id,user_id,check_in_date,check_out_date,total_cost,status");
            for (Reservation reservation : reservations) {
                writer.printf(
                        "%d,%d,%d,%d,%s,%s,%s,%s%n",
                        reservation.getId(),
                        reservation.getGuestId(),
                        reservation.getRoomId(),
                        reservation.getUserId(),
                        reservation.getCheckInDate(),
                        reservation.getCheckOutDate(),
                        reservation.getTotalCost() != null ? reservation.getTotalCost().toPlainString() : "",
                        reservation.getStatus() != null ? reservation.getStatus().name() : ""
                );
            }
            logger.info("HTTP Trace: GET /exports/active-reservations - 200 OK");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "HTTP Trace: GET /exports/active-reservations - 500 INTERNAL SERVER ERROR", e);
        }
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
