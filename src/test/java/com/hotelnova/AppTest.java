package com.hotelnova;

import com.hotelnova.model.User;
import com.hotelnova.model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AppTest {

    @Test
    void shouldShowAdminMainMenuOnlyWithPrivilegedOptions() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        String[] options = App.buildMainMenuOptions(admin);

        assertArrayEquals(
                new String[] {
                        "1. Manage Rooms",
                        "2. Generate CSV Report",
                        "3. Exit"
                },
                options
        );
    }

    @Test
    void shouldShowReceptionistMainMenuWithOperationalOptions() {
        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        String[] options = App.buildMainMenuOptions(receptionist);

        assertArrayEquals(
                new String[] {
                        "1. Manage Rooms",
                        "2. Manage Guests",
                        "3. Process Check-In",
                        "4. Process Check-Out",
                        "5. Generate CSV Report",
                        "6. Exit"
                },
                options
        );
    }

    @Test
    void shouldShowTypeFilterInRoomMenuForAllRoles() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        assertArrayEquals(
                new String[] {"List All", "Register New", "Edit Price", "Filter by Status", "Filter by Type", "Back"},
                App.buildRoomMenuOptions(admin)
        );
        assertArrayEquals(
                new String[] {"List All", "Filter by Status", "Filter by Type", "Back"},
                App.buildRoomMenuOptions(receptionist)
        );
    }

    @Test
    void shouldDetectAdminRoleCorrectly() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        User receptionist = new User();
        receptionist.setRole(UserRole.RECEPTIONIST);

        assertTrue(App.isAdmin(admin));
        assertFalse(App.isAdmin(receptionist));
    }
}
