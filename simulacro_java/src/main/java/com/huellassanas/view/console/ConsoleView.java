package com.huellassanas.view.console;

import com.huellassanas.controller.ClinicaController;
import com.huellassanas.model.Cita;
import com.huellassanas.model.Cliente;
import com.huellassanas.model.Mascota;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Vista de consola del sistema Huellas Sanas.
 *
 * <p><strong>Principio MVC:</strong> esta clase se ocupa únicamente de
 * la presentación (entrada/salida por consola). Toda la lógica de negocio
 * se delega al {@link ClinicaController}; la vista nunca accede
 * directamente a la base de datos ni a los servicios.</p>
 *
 * <h3>Ciclo de uso:</h3>
 * <pre>{@code
 * ClinicaController controller = new ClinicaController(authService, clinicaService);
 * ConsoleView vista = new ConsoleView(controller);
 * vista.iniciar();
 * }</pre>
 */
public class ConsoleView {

    private static final DateTimeFormatter FMT_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ClinicaController controller;
    private final Scanner            scanner;

    /**
     * Construye la vista de consola con el controlador compartido.
     *
     * @param controller controlador agnóstico de UI
     */
    public ConsoleView(ClinicaController controller) {
        this.controller = controller;
        this.scanner    = new Scanner(System.in);
    }

    // ─── Punto de entrada ─────────────────────────────────────────────────────

    /**
     * Inicia el ciclo principal de la interfaz de consola.
     */
    public void iniciar() {
        imprimirBienvenida();
        if (!realizarLogin()) {
            System.out.println("No se pudo autenticar. Saliendo del sistema.");
            return;
        }
        menuPrincipal();
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    private boolean realizarLogin() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("         INICIO DE SESIÓN");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        for (int intentos = 0; intentos < 3; intentos++) {
            System.out.print("Usuario: ");
            String user = scanner.nextLine().trim();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine().trim();

            ClinicaController.Resultado r = controller.iniciarSesion(user, pass);
            if (r.exito()) {
                System.out.println("✔ " + r.mensaje());
                return true;
            }
            System.out.println("✘ " + r.mensaje() +
                               " (Intento " + (intentos + 1) + "/3)");
        }
        return false;
    }

    // ─── Menú principal ───────────────────────────────────────────────────────

    private void menuPrincipal() {
        boolean salir = false;
        while (!salir) {
            imprimirMenuPrincipal();
            String opcion = scanner.nextLine().trim();
            switch (opcion) {
                case "1" -> flujoRegistrarCliente();
                case "2" -> flujoRegistrarMascotaYCita();
                case "3" -> {
                    ClinicaController.Resultado r = controller.cerrarSesion();
                    System.out.println("✔ " + r.mensaje());
                    salir = true;
                }
                default  -> System.out.println("⚠ Opción inválida. Intente de nuevo.");
            }
        }
    }

    // ─── Flujos de casos de uso ───────────────────────────────────────────────

    /** Flujo UI para registrar un nuevo cliente. */
    private void flujoRegistrarCliente() {
        System.out.println("\n── REGISTRAR CLIENTE ──────────────────────");
        Cliente c = new Cliente();
        System.out.print("Nombre: ");       c.setNombre(scanner.nextLine().trim());
        System.out.print("Apellido: ");     c.setApellido(scanner.nextLine().trim());
        System.out.print("DNI: ");          c.setDni(scanner.nextLine().trim());
        System.out.print("Teléfono: ");     c.setTelefono(scanner.nextLine().trim());
        System.out.print("Correo: ");       c.setCorreo(scanner.nextLine().trim());
        System.out.print("Dirección: ");    c.setDireccion(scanner.nextLine().trim());

        ClinicaController.Resultado r = controller.registrarCliente(c);
        System.out.println(r.exito() ? "✔ " + r.mensaje() : "✘ " + r.mensaje());
    }

    /** Flujo UI para registrar una mascota con su primera cita (atómico). */
    private void flujoRegistrarMascotaYCita() {
        System.out.println("\n── REGISTRAR MASCOTA Y CITA (ATÓMICO) ────");

        Mascota mascota = capturarDatosMascota();
        if (mascota == null) return;

        Cita cita = capturarDatosCita();
        if (cita == null) return;

        ClinicaController.Resultado r = controller.registrarMascotaYCita(mascota, cita);
        System.out.println(r.exito() ? "✔ " + r.mensaje() : "✘ " + r.mensaje());
    }

    // ─── Captura de datos ─────────────────────────────────────────────────────

    private Mascota capturarDatosMascota() {
        Mascota m = new Mascota();
        System.out.print("Nombre de la mascota: ");
        m.setNombre(scanner.nextLine().trim());

        System.out.print("Especie (PERRO/GATO/AVE/REPTIL/ROEDOR/OTRO): ");
        try {
            m.setEspecie(Mascota.Especie.valueOf(scanner.nextLine().trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            System.out.println("✘ Especie inválida.");
            return null;
        }

        System.out.print("Raza: ");
        m.setRaza(scanner.nextLine().trim());

        System.out.print("Peso (kg): ");
        try {
            m.setPeso(Double.parseDouble(scanner.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("✘ Peso inválido.");
            return null;
        }

        System.out.print("Fecha de nacimiento (dd/MM/yyyy, Enter=desconocida): ");
        String fechaStr = scanner.nextLine().trim();
        if (!fechaStr.isBlank()) {
            try {
                m.setFechaNacimiento(LocalDate.parse(fechaStr,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Fecha inválida; se omitirá.");
            }
        }

        System.out.print("ID del cliente propietario: ");
        try {
            m.setClienteId(Integer.parseInt(scanner.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("✘ ID de cliente inválido.");
            return null;
        }
        return m;
    }

    private Cita capturarDatosCita() {
        System.out.print("ID del veterinario: ");
        int vetId;
        try {
            vetId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✘ ID de veterinario inválido.");
            return null;
        }

        System.out.print("Fecha y hora de la cita (dd/MM/yyyy HH:mm): ");
        LocalDateTime fechaHora;
        try {
            fechaHora = LocalDateTime.parse(scanner.nextLine().trim(), FMT_FECHA_HORA);
        } catch (DateTimeParseException e) {
            System.out.println("✘ Formato de fecha inválido.");
            return null;
        }

        System.out.print("Motivo de la consulta: ");
        String motivo = scanner.nextLine().trim();

        return new Cita(0, fechaHora, motivo, vetId);  // mascotaId se asigna en el servicio
    }

    // ─── Presentación ─────────────────────────────────────────────────────────

    private void imprimirBienvenida() {
        System.out.println("""
                ╔══════════════════════════════════════════╗
                ║     🐾  CLÍNICA VETERINARIA               ║
                ║         HUELLAS SANAS                     ║
                ║     Sistema de Gestión v1.0               ║
                ╚══════════════════════════════════════════╝
                """);
    }

    private void imprimirMenuPrincipal() {
        String usuario = controller.getUsuarioActual()
                .map(u -> u.getNombreCompleto() + " [" + u.getRol() + "]")
                .orElse("Desconocido");
        System.out.println("\n─────────────────────────────────────────");
        System.out.println(" Sesión: " + usuario);
        System.out.println("─────────────────────────────────────────");
        System.out.println(" 1. Registrar Cliente");
        System.out.println(" 2. Registrar Mascota + Cita (atómico)");
        System.out.println(" 3. Cerrar Sesión");
        System.out.println("─────────────────────────────────────────");
        System.out.print("Seleccione una opción: ");
    }
}
