package com.hotelnova;

import com.hotelnova.controller.HotelController;
import com.hotelnova.exception.AuthenticationException;
import com.hotelnova.model.Guest;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class App {
    private static final String OPTION_MANAGE_ROOMS = "Manage Rooms";
    private static final String OPTION_MANAGE_GUESTS = "Manage Guests";
    private static final String OPTION_PROCESS_CHECK_IN = "Process Check-In";
    private static final String OPTION_PROCESS_CHECK_OUT = "Process Check-Out";
    private static final String OPTION_GENERATE_CSV = "Generate CSV Report";
    private static final String OPTION_EXIT = "Exit";

    private static HotelController controller;
    private static User currentUser;

    public static void main(String[] args) {
        com.hotelnova.database.DatabaseInitializer.initialize();
        controller = new HotelController();
        showLogin();
    }

    private static void showLogin() {
        while (currentUser == null) {
            String username = JOptionPane.showInputDialog(null, "Username:", "HotelNova Login", JOptionPane.QUESTION_MESSAGE);
            if (username == null) System.exit(0);

            String password = JOptionPane.showInputDialog(null, "Password:", "HotelNova Login", JOptionPane.QUESTION_MESSAGE);
            if (password == null) System.exit(0);

            try {
                currentUser = controller.login(username, password);
                JOptionPane.showMessageDialog(null, "Welcome, " + currentUser.getUsername() + "!");
                showMainMenu();
            } catch (AuthenticationException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showMainMenu() {
        String[] options = buildMainMenuOptions(currentUser);

        boolean exit = false;
        while (!exit) {
            String selection = (String) JOptionPane.showInputDialog(
                    null, "Select an option:", "HotelNova System - User: " + currentUser.getUsername(),
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]
            );

            if (selection == null || selection.contains(OPTION_EXIT)) {
                exit = true;
                continue;
            }

            try {
                handleMenuSelection(selection);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void handleMenuSelection(String selection) throws Exception {
        if (selection.contains(OPTION_MANAGE_ROOMS)) {
            showRoomMenu();
        } else if (selection.contains(OPTION_MANAGE_GUESTS)) {
            showGuestMenu();
        } else if (selection.contains(OPTION_PROCESS_CHECK_IN)) {
            processCheckInAction();
        } else if (selection.contains(OPTION_PROCESS_CHECK_OUT)) {
            processCheckOutAction();
        } else if (selection.contains(OPTION_GENERATE_CSV)) {
            controller.exportReservationsToCSV();
            JOptionPane.showMessageDialog(null, "Reports generated as 'rooms_export.csv' and 'active_reservations.csv'.");
        }
    }

    // --- ROOM MODULE (CRUD) ---
    private static void showRoomMenu() {
        String[] subOptions = buildRoomMenuOptions(currentUser);
        String choice = (String) JOptionPane.showInputDialog(null, "Room Management", "HotelNova",
                JOptionPane.PLAIN_MESSAGE, null, subOptions, subOptions[0]);

        if (choice == null || choice.equals("Back")) return;

        try {
            switch (choice) {
                case "List All":
                    displayRooms(controller.getAllRooms());
                    break;
                case "Register New":
                    requireAdminAccess();
                    registerRoom();
                    break;
                case "Edit Price":
                    requireAdminAccess();
                    updateRoomPrice();
                    break;
                case "Filter by Status":
                    filterRoomsByStatus();
                    break;
                case "Filter by Type":
                    filterRoomsByType();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static void displayRooms(List<Room> rooms) {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-5s %-12s %-12s %-10s %-15s %-10s%n", "ID", "Number", "Type", "Price", "Status", "Active"));
        tableContent.append("-".repeat(80)).append(System.lineSeparator());

        for (Room room : rooms) {
            String statusLabel = room.getStatus() == RoomStatus.AVAILABLE ? "[AVAILABLE]" : "[OCCUPIED]";
            String activeLabel = room.isActive() ? "[ACTIVE]" : "[INACTIVE]";
            tableContent.append(String.format(
                    "%-5d %-12s %-12s %-10.2f %-15s %-10s%n",
                    room.getId(),
                    room.getRoomNumber(),
                    room.getType(),
                    room.getPricePerNight(),
                    statusLabel,
                    activeLabel
            ));
        }
        showTableDialog("Room List", tableContent.toString());
    }

    private static void registerRoom() throws Exception {
        String roomNumber = JOptionPane.showInputDialog("Room number:");
        String roomType = JOptionPane.showInputDialog("Type (e.g. SINGLE/DOUBLE/SUITE/VIP):");
        int capacity = Integer.parseInt(JOptionPane.showInputDialog("Capacity:"));
        BigDecimal pricePerNight = new BigDecimal(JOptionPane.showInputDialog("Price per night:"));

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setType(roomType);
        room.setCapacity(capacity);
        room.setPricePerNight(pricePerNight);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);

        controller.saveRoom(room);
        JOptionPane.showMessageDialog(null, "Room registered successfully.");
    }

    private static void updateRoomPrice() throws Exception {
        int roomId = Integer.parseInt(JOptionPane.showInputDialog("Room ID:"));
        BigDecimal newPrice = new BigDecimal(JOptionPane.showInputDialog("New price:"));
        controller.updateRoomPrice(roomId, newPrice);
        JOptionPane.showMessageDialog(null, "Price updated.");
    }

    private static void filterRoomsByStatus() {
        String status = JOptionPane.showInputDialog("Enter status (AVAILABLE/OCCUPIED):");
        if (status == null || status.isBlank()) {
            return;
        }
        displayRooms(controller.getRoomsByStatus(RoomStatus.valueOf(status.trim().toUpperCase())));
    }

    private static void filterRoomsByType() {
        String type = JOptionPane.showInputDialog("Enter room type (e.g. SINGLE/DOUBLE/SUITE/VIP):");
        if (type == null || type.isBlank()) {
            return;
        }
        displayRooms(controller.getRoomsByType(type.trim().toUpperCase()));
    }

    // --- GUEST MODULE ---
    private static void showGuestMenu() {
        String[] subOptions = {"Search by Document", "Register New", "List All", "Back"};
        String choice = (String) JOptionPane.showInputDialog(null, "Guest Management", "HotelNova",
                JOptionPane.PLAIN_MESSAGE, null, subOptions, subOptions[0]);

        if (choice == null || choice.equals("Back")) return;

        try {
            switch (choice) {
                case "Search by Document":
                    String documentNumber = JOptionPane.showInputDialog("Document number:");
                    Guest guest = controller.findGuestByDocument(documentNumber);
                    if (guest != null) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Guest: " + guest.getFirstName() + " " + guest.getLastName()
                                        + (guest.isActive() ? " [ACTIVE]" : " [INACTIVE]")
                        );
                    } else {
                        JOptionPane.showMessageDialog(null, "Guest not found.");
                    }
                    break;
                case "Register New":
                    registerGuest();
                    break;
                case "List All":
                    displayGuests();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static void registerGuest() throws Exception {
        Guest guest = new Guest();
        guest.setFirstName(JOptionPane.showInputDialog("First name:"));
        guest.setLastName(JOptionPane.showInputDialog("Last name:"));
        guest.setDocumentNumber(JOptionPane.showInputDialog("Document number:"));
        guest.setEmail(JOptionPane.showInputDialog("Email:"));
        guest.setPhoneNumber(JOptionPane.showInputDialog("Phone number:"));
        guest.setActive(true);

        controller.registerGuest(guest);
        JOptionPane.showMessageDialog(null, "Guest registered.");
    }

    private static void displayGuests() {
        List<Guest> guests = controller.getAllGuests();
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-18s %-24s %-15s%n", "Document", "Full Name", "Status"));
        tableContent.append("-".repeat(60)).append(System.lineSeparator());
        for (Guest guest : guests) {
            tableContent.append(String.format(
                    "%-18s %-24s %-15s%n",
                    guest.getDocumentNumber(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.isActive() ? "[ACTIVE]" : "[INACTIVE]"
            ));
        }
        showTableDialog("Guest List", tableContent.toString());
    }

    // --- RESERVATIONS ---
    private static void processCheckInAction() throws Exception {
        try {
            int guestId = Integer.parseInt(JOptionPane.showInputDialog("Guest ID:"));
            int roomId = Integer.parseInt(JOptionPane.showInputDialog("Room ID:"));
            String checkInText = JOptionPane.showInputDialog(
                    "Check-in (YYYY-MM-DD HH:mm):",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );
            String checkOutText = JOptionPane.showInputDialog(
                    "Check-out (YYYY-MM-DD HH:mm):",
                    LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Reservation reservation = new Reservation();
            reservation.setGuestId(guestId);
            reservation.setRoomId(roomId);
            reservation.setUserId(currentUser.getId());
            reservation.setCheckInDate(LocalDateTime.parse(checkInText, formatter));
            reservation.setCheckOutDate(LocalDateTime.parse(checkOutText, formatter));
            reservation.setStatus(ReservationStatus.ACTIVE);
            reservation.setTotalCost(BigDecimal.ZERO);

            controller.checkIn(reservation);
            JOptionPane.showMessageDialog(null, "Check-in completed successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Check-in error: " + e.getMessage(), "Business Rule Failure", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void processCheckOutAction() throws Exception {
        String reservationIdText = JOptionPane.showInputDialog("Enter the reservation ID:");
        if (reservationIdText != null) {
            controller.checkOut(Integer.parseInt(reservationIdText));
            JOptionPane.showMessageDialog(null, "Check-out completed. The room is now [AVAILABLE].");
        }
    }

    private static void showTableDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(700, 400));
        JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    static String[] buildMainMenuOptions(User user) {
        if (isAdmin(user)) {
            return new String[] {
                "1. " + OPTION_MANAGE_ROOMS,
                "2. " + OPTION_GENERATE_CSV,
                "3. " + OPTION_EXIT
            };
        }

        return new String[] {
            "1. " + OPTION_MANAGE_ROOMS,
            "2. " + OPTION_MANAGE_GUESTS,
            "3. " + OPTION_PROCESS_CHECK_IN,
            "4. " + OPTION_PROCESS_CHECK_OUT,
            "5. " + OPTION_GENERATE_CSV,
            "6. " + OPTION_EXIT
        };
    }

    static String[] buildRoomMenuOptions(User user) {
        if (isAdmin(user)) {
            return new String[] {"List All", "Register New", "Edit Price", "Filter by Status", "Filter by Type", "Back"};
        }

        return new String[] {"List All", "Filter by Status", "Filter by Type", "Back"};
    }

    static boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    static void requireAdminAccess() {
        if (!isAdmin(currentUser)) {
            throw new SecurityException("Only ADMIN users can perform this action.");
        }
    }
}
