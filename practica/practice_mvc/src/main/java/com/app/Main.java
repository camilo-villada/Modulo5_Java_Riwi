package com.app;

import com.app.config.AppConfig;
import com.app.controller.AppController;
import com.app.controller.ProductoController;
import com.app.controller.UsuarioController;
import com.app.dao.ProductoDAO;
import com.app.dao.UsuarioDAO;
import com.app.dao.impl.ProductoDAOImpl;
import com.app.dao.impl.UsuarioDAOImpl;
import com.app.view.ConsoleView;
import com.app.view.SwingView;
import com.app.view.View;

public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.getInstance();

        // ── Factory: elige la vista según app.properties ──
        View view = createView(config.getViewType());

        // ── Inyección de dependencias ──
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
        ProductoDAO productoDAO = new ProductoDAOImpl();

        UsuarioController usuarioController = new UsuarioController(view, usuarioDAO);
        ProductoController productoController = new ProductoController(view, productoDAO);
        AppController appController = new AppController(view, usuarioController, productoController);

        view.showMessage("Bienvenido a " + config.getAppName());

        // ── Arrancar la aplicación ──
        appController.run();
    }

    private static View createView(String type) {
        return switch (type.toLowerCase()) {
            case "swing" -> new SwingView();
            default       -> new ConsoleView();
        };
    }
}