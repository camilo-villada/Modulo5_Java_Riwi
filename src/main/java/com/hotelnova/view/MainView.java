package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;
import javax.swing.JOptionPane;

public class MainView {
    private static final String OPTION_ROOMS = "Rooms";
    private static final String OPTION_GUESTS = "Guests";
    private static final String OPTION_USERS = "Users";
    private static final String OPTION_RESERVATIONS = "Reservations";
    private static final String OPTION_EXPORTS = "Exports";
    private static final String OPTION_EXIT = "Exit";

    public static void showMenu(HotelController controller, User currentUser) {
        boolean exit = false;
        while (!exit) {
            String[] options = buildMainMenuOptions(currentUser);
            String selection = (String) JOptionPane.showInputDialog(
                    null,
                    "Select an option:",
                    "HotelNova - User: " + currentUser.getUsername(),
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (selection == null || selection.contains(OPTION_EXIT)) {
                exit = true;
                continue;
            }

            try {
                handleMenuSelection(selection, controller, currentUser);
            } catch (Exception e) {
                UIHelper.showError("System Error", e.getMessage());
            }
        }
    }

    private static void handleMenuSelection(String selection, HotelController controller, User currentUser) throws Exception {
        if (selection.contains(OPTION_ROOMS)) {
            RoomView.showMenu(controller, currentUser);
        } else if (selection.contains(OPTION_GUESTS)) {
            GuestView.showMenu(controller);
        } else if (selection.contains(OPTION_USERS)) {
            UserView.showMenu(controller, currentUser);
        } else if (selection.contains(OPTION_RESERVATIONS)) {
            ReservationView.showMenu(controller, currentUser);
        } else if (selection.contains(OPTION_EXPORTS)) {
            showExportMenu(controller);
        }
    }

    private static void showExportMenu(HotelController controller) {
        String[] options = {"Generate CSV", "Back"};
        String choice = (String) JOptionPane.showInputDialog(null, "Exports", "HotelNova", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null || choice.equals("Back")) return;

        if ("Generate CSV".equals(choice)) {
            try {
                controller.exportDataToCSV();
                UIHelper.showSuccess("Files generated: rooms_export.csv, active_reservations.csv, legacy_rooms_export.csv, and legacy_active_reservations.csv.");
            } catch (Exception e) {
                UIHelper.showError("Exports", e.getMessage());
            }
        }
    }

    public static String[] buildMainMenuOptions(User user) {
        if (isAdmin(user)) {
            return new String[] {"1. " + OPTION_ROOMS, "2. " + OPTION_GUESTS, "3. " + OPTION_USERS, "4. " + OPTION_RESERVATIONS, "5. " + OPTION_EXPORTS, "6. " + OPTION_EXIT};
        }
        return new String[] {"1. " + OPTION_ROOMS, "2. " + OPTION_GUESTS, "3. " + OPTION_RESERVATIONS, "4. " + OPTION_EXPORTS, "5. " + OPTION_EXIT};
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    public static void requireAdminAccess(User currentUser) {
        if (!isAdmin(currentUser)) {
            throw new SecurityException("Only ADMIN users can perform this action.");
        }
    }
}
