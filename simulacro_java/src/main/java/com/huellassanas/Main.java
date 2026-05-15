package com.huellassanas;

import com.huellassanas.dao.impl.CitaDaoImpl;
import com.huellassanas.dao.impl.ClienteDaoImpl;
import com.huellassanas.dao.impl.MascotaDaoImpl;
import com.huellassanas.dao.impl.UsuarioDaoImpl;
import com.huellassanas.controller.ClinicaController;
import com.huellassanas.service.AuthService;
import com.huellassanas.service.ClinicaService;
import com.huellassanas.view.console.ConsoleView;
import com.huellassanas.view.desktop.MainFrame;
import com.huellassanas.util.DatabaseConnection;

/**
 * Punto de entrada principal del sistema Huellas Sanas.
 *
 * <p>Realiza la composición raíz (Composition Root) del sistema:
 * instancia e inyecta todas las dependencias en el orden correcto
 * y decide qué interfaz lanzar según el argumento de línea de comandos.</p>
 *
 * <h3>Modos de ejecución:</h3>
 * <ul>
 *   <li>{@code java -jar huellas-sanas.jar}        → Interfaz de consola (por defecto)</li>
 *   <li>{@code java -jar huellas-sanas.jar --swing} → Interfaz gráfica Swing</li>
 * </ul>
 */
public class Main {

    public static void main(String[] args) {
        // ── 1. Composición de DAOs ────────────────────────────────────────────
        UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
        ClienteDaoImpl clienteDao = new ClienteDaoImpl();
        MascotaDaoImpl mascotaDao = new MascotaDaoImpl();
        CitaDaoImpl    citaDao    = new CitaDaoImpl();

        // ── 2. Composición de Servicios ───────────────────────────────────────
        AuthService    authService    = new AuthService(usuarioDao);
        ClinicaService clinicaService = new ClinicaService(clienteDao, mascotaDao, citaDao);

        // ── 3. Composición del Controlador (único, compartido por ambas vistas)
        ClinicaController controller = new ClinicaController(authService, clinicaService);

        // ── 4. Selección de interfaz ─────────────────────────────────────────
        boolean modoSwing = args.length > 0 && "--swing".equalsIgnoreCase(args[0]);

        if (modoSwing) {
            System.out.println("[Main] Lanzando interfaz gráfica Swing...");
            MainFrame.lanzar(controller);
        } else {
            System.out.println("[Main] Lanzando interfaz de consola...");
            ConsoleView consolaView = new ConsoleView(controller);
            consolaView.iniciar();
            // Cierra la conexión al terminar la vista de consola
            DatabaseConnection.getInstance().closeConnection();
        }
    }
}
