package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;
import com.hotelnova.model.User;
import com.hotelnova.util.ConfigManager;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void showMenu(HotelController controller, User currentUser) {
        String[] options = {"List Active", "Process Check-In", "Process Check-Out", "Back"};
        String choice = (String) JOptionPane.showInputDialog(null, "Reservation Management", "HotelNova", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null || choice.equals("Back")) return;

        try {
            switch (choice) {
                case "List Active": displayReservations(controller.getActiveReservations()); break;
                case "Process Check-In": processCheckInAction(controller, currentUser); break;
                case "Process Check-Out": processCheckOutAction(controller); break;
            }
        } catch (Exception e) {
            UIHelper.showError("Reservation Management", e.getMessage());
        }
    }

    private static void processCheckInAction(HotelController controller, User currentUser) throws Exception {
        Integer guestId = UIHelper.promptInt("Guest ID:");
        if (guestId == null) return;
        Integer roomId = UIHelper.promptInt("Room ID:");
        if (roomId == null) return;

        LocalDate today = LocalDate.now();
        int checkInHour = getConfigHour("checkInHour", "horaCheckIn", 15);
        int checkOutHour = getConfigHour("checkOutHour", "horaCheckOut", 12);

        String checkInText = UIHelper.promptText("Check-in (yyyy-MM-dd HH:mm):", "Reservation", today.atTime(checkInHour, 0).format(DATE_TIME_FORMATTER));
        if (checkInText == null) return;

        String checkOutText = UIHelper.promptText("Check-out (yyyy-MM-dd HH:mm):", "Reservation", today.plusDays(1).atTime(checkOutHour, 0).format(DATE_TIME_FORMATTER));
        if (checkOutText == null) return;

        Reservation reservation = new Reservation();
        reservation.setGuestId(guestId);
        reservation.setRoomId(roomId);
        reservation.setUserId(currentUser.getId());
        reservation.setCheckInDate(LocalDateTime.parse(checkInText, DATE_TIME_FORMATTER));
        reservation.setCheckOutDate(LocalDateTime.parse(checkOutText, DATE_TIME_FORMATTER));
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setTotalCost(BigDecimal.ZERO);

        controller.checkIn(reservation);
        UIHelper.showSuccess("Check-in registered successfully.");
    }

    private static void processCheckOutAction(HotelController controller) throws Exception {
        Integer reservationId = UIHelper.promptInt("Reservation ID:");
        if (reservationId == null) return;
        controller.checkOut(reservationId);
        UIHelper.showSuccess("Check-out completed. The room is now [AVAILABLE].");
    }

    private static int getConfigHour(String primaryKey, String fallbackKey, int defaultValue) {
        String value = ConfigManager.getProperty(primaryKey, fallbackKey);
        if (value == null || value.isBlank()) return defaultValue;
        return Integer.parseInt(value);
    }

    public static void displayReservations(List<Reservation> reservations) {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-5s %-10s %-10s %-10s %-18s %-18s %-12s%n", "ID", "Guest", "Room", "User", "Check-In", "Check-Out", "Status"));
        tableContent.append("-".repeat(100)).append(System.lineSeparator());

        for (Reservation reservation : reservations) {
            tableContent.append(String.format(
                    "%-5d %-10d %-10d %-10d %-18s %-18s %-12s%n",
                    reservation.getId(),
                    reservation.getGuestId(),
                    reservation.getRoomId(),
                    reservation.getUserId(),
                    reservation.getCheckInDate().format(DATE_TIME_FORMATTER),
                    reservation.getCheckOutDate().format(DATE_TIME_FORMATTER),
                    reservation.getStatus().name()
            ));
        }
        UIHelper.showTableDialog("Active Reservations", tableContent.toString());
    }
}
