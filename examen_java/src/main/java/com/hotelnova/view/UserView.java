package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;
import javax.swing.JOptionPane;
import java.util.List;

public class UserView {

    public static void showMenu(HotelController controller, User currentUser) {
        MainView.requireAdminAccess(currentUser);
        String[] options = {"List All", "Register New", "Edit User", "Toggle Active/Inactive", "Delete User", "Back"};
        String choice = (String) JOptionPane.showInputDialog(null, "User Management", "HotelNova", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null || choice.equals("Back")) return;

        try {
            switch (choice) {
                case "List All": displayUsers(controller.getAllUsers()); break;
                case "Register New": registerUser(controller); break;
                case "Edit User": editUser(controller); break;
                case "Toggle Active/Inactive": toggleUserActive(controller); break;
                case "Delete User": deleteUser(controller); break;
            }
        } catch (Exception e) {
            UIHelper.showError("User Management", e.getMessage());
        }
    }

    private static void registerUser(HotelController controller) throws Exception {
        User user = buildUserFromInput(new User());
        if (user == null) return;
        String password = UIHelper.promptPassword("User Password:");
        if (password == null) return;
        user.setActive(true);
        controller.registerUser(user, password);
        UIHelper.showSuccess("User registered successfully.");
    }

    private static void editUser(HotelController controller) throws Exception {
        Integer userId = UIHelper.promptInt("User ID:");
        if (userId == null) return;
        User user = controller.findUserById(userId);
        User updatedUser = buildUserFromInput(user);
        if (updatedUser == null) return;
        String password = UIHelper.promptPassword("New password (leave blank to keep the current one):", true);
        if (password == null) return;
        controller.updateUser(updatedUser, password);
        UIHelper.showSuccess("User updated successfully.");
    }

    private static void toggleUserActive(HotelController controller) throws Exception {
        Integer userId = UIHelper.promptInt("User ID:");
        if (userId == null) return;
        User user = controller.toggleUserActive(userId);
        UIHelper.showSuccess("User " + user.getUsername() + " is now " + (user.isActive() ? "[ACTIVE]" : "[INACTIVE]") + ".");
    }

    private static void deleteUser(HotelController controller) throws Exception {
        Integer userId = UIHelper.promptInt("User ID to delete:");
        if (userId == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "This action will delete the user. Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        controller.deleteUser(userId);
        UIHelper.showSuccess("User deleted successfully.");
    }

    private static User buildUserFromInput(User user) {
        String username = UIHelper.promptText("Username:", "User", user.getUsername());
        if (username == null) return null;
        String roleText = UIHelper.promptText("Role (ADMIN/RECEPTIONIST):", "User", user.getRole() != null ? user.getRole().name() : UserRole.RECEPTIONIST.name());
        if (roleText == null) return null;

        user.setUsername(username.trim());
        user.setRole(UserRole.valueOf(roleText.trim().toUpperCase()));
        return user;
    }

    public static void displayUsers(List<User> users) {
        StringBuilder tableContent = new StringBuilder();
        tableContent.append(String.format("%-5s %-20s %-18s %-15s%n", "ID", "Username", "Role", "Status"));
        tableContent.append("-".repeat(70)).append(System.lineSeparator());

        for (User user : users) {
            tableContent.append(String.format(
                    "%-5d %-20s %-18s %-15s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name(),
                    user.isActive() ? "[ACTIVE]" : "[INACTIVE]"
            ));
        }
        UIHelper.showTableDialog("User List", tableContent.toString());
    }
}
