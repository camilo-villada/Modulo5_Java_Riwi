package com.hotelnova;

import com.hotelnova.controller.HotelController;
import com.hotelnova.model.User;
import com.hotelnova.util.LoggingConfig;
import com.hotelnova.view.AuthView;
import com.hotelnova.view.MainView;

public class App {
    // Entry point of the application.
    public static void main(String[] args) {
        LoggingConfig.configure();
        com.hotelnova.database.DatabaseInitializer.initialize();
        HotelController controller = new HotelController();
        
        User currentUser = AuthView.showLogin(controller);
        
        if (currentUser != null) {
            MainView.showMenu(controller, currentUser);
        }
    }
}
