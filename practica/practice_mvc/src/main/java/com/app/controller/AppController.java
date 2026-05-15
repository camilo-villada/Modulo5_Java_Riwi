package com.app.controller;

import com.app.view.View;

public class AppController {

    private final View view;
    private final UsuarioController usuarioController;
    private final ProductoController productoController;

    public AppController(View view, UsuarioController usuarioController, ProductoController productoController) {
        this.view = view;
        this.usuarioController = usuarioController;
        this.productoController = productoController;
    }

    public void run() {
        String[] menuOptions = {"Gestionar usuarios", "Gestionar productos", "Salir"};
        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Menú principal");
            int choice = view.getMenuChoice();
            switch (choice) {
                case 1 -> usuarioController.run();
                case 2 -> productoController.run();
                case 3 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
        view.showMessage("¡Hasta luego!");
    }
}

