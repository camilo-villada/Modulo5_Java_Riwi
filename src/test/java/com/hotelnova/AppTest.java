package com.hotelnova;

import com.hotelnova.model.User;
import com.hotelnova.view.MainView;
import com.hotelnova.view.RoomView;
import com.hotelnova.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    @Test
    void shouldShowAdminMainMenuWithAllManagementModules() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        assertArrayEquals(
                new String[] {
                        "1. Rooms",
                        "2. Guests",
                        "3. Users",
                        "4. Reservations",
                        "5. Exports",
                        "6. Exit"
                },
                MainView.buildMainMenuOptions(admin)
        );
    }

    @Test
    void shouldShowReceptionistMainMenuWithoutUserAdministration() {
        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        assertArrayEquals(
                new String[] {
                        "1. Rooms",
                        "2. Guests",
                        "3. Reservations",
                        "4. Exports",
                        "5. Exit"
                },
                MainView.buildMainMenuOptions(receptionist)
        );
    }

    @Test
    void shouldShowRoomMenusByRole() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        assertArrayEquals(
                new String[] {
                        "List All",
                        "Register New",
                        "Edit Room",
                        "Toggle Active/Inactive",
                        "Delete Room",
                        "Filter by Status",
                        "Filter by Type",
                        "Back"
                },
                RoomView.buildRoomMenuOptions(admin)
        );
        assertArrayEquals(
                new String[] {"List All", "Filter by Status", "Filter by Type", "Back"},
                RoomView.buildRoomMenuOptions(receptionist)
        );
    }

    @Test
    void shouldDetectAdminRoleCorrectly() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        assertTrue(MainView.isAdmin(admin));
        assertFalse(MainView.isAdmin(receptionist));
    }
}
