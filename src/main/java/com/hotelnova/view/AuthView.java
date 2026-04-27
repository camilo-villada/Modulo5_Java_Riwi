package com.hotelnova.view;

import com.hotelnova.controller.HotelController;
import com.hotelnova.exception.AuthenticationException;
import com.hotelnova.model.User;

public class AuthView {
    public static User showLogin(HotelController controller) {
        User currentUser = null;
        while (currentUser == null) {
            String username = UIHelper.promptText("Username:", "HotelNova Login", null);
            if (username == null) {
                System.exit(0);
            }

            String password = UIHelper.promptPassword("Password:");
            if (password == null) {
                System.exit(0);
            }

            try {
                currentUser = controller.login(username.trim(), password);
                UIHelper.showSuccess("Welcome, " + currentUser.getUsername() + ".");
                return currentUser;
            } catch (AuthenticationException e) {
                UIHelper.showError("Authentication Error", e.getMessage());
            }
        }
        return null;
    }
}
