package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.Guest;
import javax.swing.JOptionPane;
import java.util.List;

public class GuestView {

    public static void showMenu(HotelController controller) {
        String[] options = {"Search by Document", "Register New", "Edit Guest", "Toggle Active/Inactive", "List All", "Back"};
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Guest Management",
                "HotelNova",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null || choice.equals("Back")) return;

        try {
            switch (choice) {
                case "Search by Document": searchGuestByDocument(controller); break;
                case "Register New": registerGuest(controller); break;
                case "Edit Guest": editGuest(controller); break;
                case "Toggle Active/Inactive": toggleGuestActive(controller); break;
                case "List All": displayGuests(controller.getAllGuests()); break;
            }
        } catch (Exception e) {
            UIHelper.showError("Guest Management", e.getMessage());
        }
    }

    private static void searchGuestByDocument(HotelController controller) throws Exception {
        String documentNumber = UIHelper.promptText("Document Number:", "Search Guest", null);
        if (documentNumber == null || documentNumber.isBlank()) return;
        Guest guest = controller.findGuestByDocument(documentNumber.trim());
        if (guest == null) {
            UIHelper.showSuccess("Guest not found.");
            return;
        }
        displayGuests(List.of(guest));
    }

    private static void registerGuest(HotelController controller) throws Exception {
        Guest guest = buildGuestFromInput(new Guest());
        if (guest == null) return;
        guest.setActive(true);
        controller.registerGuest(guest);
        UIHelper.showSuccess("Guest registered successfully.");
    }

    private static void editGuest(HotelController controller) throws Exception {
        Integer guestId = UIHelper.promptInt("Guest ID:");
        if (guestId == null) return;
        Guest guest = controller.findGuestById(guestId);
        Guest updatedGuest = buildGuestFromInput(guest);
        if (updatedGuest == null) return;
        controller.updateGuest(updatedGuest);
        UIHelper.showSuccess("Guest updated successfully.");
    }

    private static void toggleGuestActive(HotelController controller) throws Exception {
        Integer guestId = UIHelper.promptInt("Guest ID:");
        if (guestId == null) return;
        Guest guest = controller.toggleGuestActive(guestId);
        UIHelper.showSuccess("Guest " + guest.getFirstName() + " " + guest.getLastName() + " is now " + (guest.isActive() ? "[ACTIVE]" : "[INACTIVE]") + ".");
    }

    private static Guest buildGuestFromInput(Guest guest) {
        String firstName = UIHelper.promptText("First Name:", "Guest", guest.getFirstName());
        if (firstName == null) return null;
        String lastName = UIHelper.promptText("Last Name:", "Guest", guest.getLastName());
        if (lastName == null) return null;
        String documentNumber = UIHelper.promptText("Document Number:", "Guest", guest.getDocumentNumber());
        if (documentNumber == null) return null;
        String email = UIHelper.promptText("Email:", "Guest", guest.getEmail());
        if (email == null) return null;
        String phone = UIHelper.promptText("Phone:", "Guest", guest.getPhoneNumber());
        if (phone == null) return null;

        guest.setFirstName(firstName.trim());
        guest.setLastName(lastName.trim());
        guest.setDocumentNumber(documentNumber.trim());
        guest.setEmail(email.trim());
        guest.setPhoneNumber(phone.trim());
        return guest;
    }

    public static void displayGuests(List<Guest> guests) {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-5s %-18s %-28s %-24s %-15s%n", "ID", "Document", "Name", "Email", "Status"));
        tableContent.append("-".repeat(105)).append(System.lineSeparator());

        for (Guest guest : guests) {
            tableContent.append(String.format(
                    "%-5d %-18s %-28s %-24s %-15s%n",
                    guest.getId(),
                    guest.getDocumentNumber(),
                    guest.getFirstName() + " " + guest.getLastName(),
                    guest.getEmail(),
                    guest.isActive() ? "[ACTIVE]" : "[INACTIVE]"
            ));
        }
        UIHelper.showTableDialog("Guest List", tableContent.toString());
    }
}
