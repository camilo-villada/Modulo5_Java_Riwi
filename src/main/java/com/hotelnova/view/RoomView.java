package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.util.List;

public class RoomView {

    public static void showMenu(HotelController controller, User currentUser) {
        String[] options = buildRoomMenuOptions(currentUser);
        
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Room Management",
                "HotelNova",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null || choice.equals("Back")) {
            return;
        }

        try {
            switch (choice) {
                case "List All":
                    displayRooms(controller.getAllRooms());
                    break;
                case "Register New":
                    MainView.requireAdminAccess(currentUser);
                    registerRoom(controller);
                    break;
                case "Edit Room":
                    MainView.requireAdminAccess(currentUser);
                    editRoom(controller);
                    break;
                case "Toggle Active/Inactive":
                    MainView.requireAdminAccess(currentUser);
                    toggleRoomActive(controller);
                    break;
                case "Delete Room":
                    MainView.requireAdminAccess(currentUser);
                    deleteRoom(controller);
                    break;
                case "Filter by Status":
                    filterRoomsByStatus(controller);
                    break;
                case "Filter by Type":
                    filterRoomsByType(controller);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            UIHelper.showError("Room Management", e.getMessage());
        }
    }

    public static String[] buildRoomMenuOptions(User user) {
        if (MainView.isAdmin(user)) {
            return new String[] {
                "List All", "Register New", "Edit Room", "Toggle Active/Inactive", "Delete Room", "Filter by Status", "Filter by Type", "Back"
            };
        }
        return new String[] {"List All", "Filter by Status", "Filter by Type", "Back"};
    }

    private static void registerRoom(HotelController controller) throws Exception {
        Room room = buildRoomFromInput(new Room());
        if (room == null) return;
        controller.saveRoom(room);
        UIHelper.showSuccess("Room registered successfully.");
    }

    private static void editRoom(HotelController controller) throws Exception {
        Integer roomId = UIHelper.promptInt("Room ID:");
        if (roomId == null) return;
        Room room = controller.getRoomById(roomId);
        Room updatedRoom = buildRoomFromInput(room);
        if (updatedRoom == null) return;
        controller.updateRoom(updatedRoom);
        UIHelper.showSuccess("Room updated successfully.");
    }

    private static void toggleRoomActive(HotelController controller) throws Exception {
        Integer roomId = UIHelper.promptInt("Room ID:");
        if (roomId == null) return;
        Room room = controller.toggleRoomActive(roomId);
        UIHelper.showSuccess("Room " + room.getRoomNumber() + " is now " + (room.isActive() ? "[ACTIVE]" : "[INACTIVE]") + ".");
    }

    private static void deleteRoom(HotelController controller) throws Exception {
        Integer roomId = UIHelper.promptInt("Room ID to delete:");
        if (roomId == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "This action will delete the room. Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        controller.deleteRoom(roomId);
        UIHelper.showSuccess("Room deleted successfully.");
    }

    private static void filterRoomsByStatus(HotelController controller) throws Exception {
        String statusText = UIHelper.promptText("Status (AVAILABLE/OCCUPIED):", "Filter Rooms", null);
        if (statusText == null || statusText.isBlank()) return;
        displayRooms(controller.getRoomsByStatus(RoomStatus.valueOf(statusText.trim().toUpperCase())));
    }

    private static void filterRoomsByType(HotelController controller) throws Exception {
        String type = UIHelper.promptText("Room Type:", "Filter Rooms", null);
        if (type == null || type.isBlank()) return;
        displayRooms(controller.getRoomsByType(type.trim().toUpperCase()));
    }

    private static Room buildRoomFromInput(Room room) {
        String roomNumber = UIHelper.promptText("Room Number:", "Room", room.getRoomNumber());
        if (roomNumber == null) return null;
        String type = UIHelper.promptText("Type:", "Room", room.getType());
        if (type == null) return null;
        Integer capacity = UIHelper.promptInt("Capacity:", room.getCapacity() > 0 ? String.valueOf(room.getCapacity()) : null);
        if (capacity == null) return null;
        BigDecimal price = UIHelper.promptDecimal("Price per Night:", room.getPricePerNight() != null ? room.getPricePerNight().toPlainString() : null);
        if (price == null) return null;
        String statusText = UIHelper.promptText("Status (AVAILABLE/OCCUPIED):", "Room", room.getStatus() != null ? room.getStatus().name() : RoomStatus.AVAILABLE.name());
        if (statusText == null) return null;

        room.setRoomNumber(roomNumber.trim().toUpperCase());
        room.setType(type.trim().toUpperCase());
        room.setCapacity(capacity);
        room.setPricePerNight(price);
        room.setStatus(RoomStatus.valueOf(statusText.trim().toUpperCase()));
        if (room.getId() == 0) room.setActive(true);
        return room;
    }

    public static void displayRooms(List<Room> rooms) {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-5s %-12s %-14s %-10s %-14s %-12s %-12s%n", "ID", "Number", "Type", "Cap.", "Price", "Status", "Active"));
        tableContent.append("-".repeat(95)).append(System.lineSeparator());

        for (Room room : rooms) {
            tableContent.append(String.format(
                    "%-5d %-12s %-14s %-10d %-14.2f %-12s %-12s%n",
                    room.getId(),
                    room.getRoomNumber(),
                    room.getType(),
                    room.getCapacity(),
                    room.getPricePerNight(),
                    room.getStatus() == RoomStatus.AVAILABLE ? "[AVAILABLE]" : "[OCCUPIED]",
                    room.isActive() ? "[ACTIVE]" : "[INACTIVE]"
            ));
        }
        UIHelper.showTableDialog("Room List", tableContent.toString());
    }
}
