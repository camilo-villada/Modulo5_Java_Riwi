package com.huellassanas.controller;

import com.huellassanas.model.Cita;
import com.huellassanas.model.Cliente;
import com.huellassanas.model.Mascota;
import com.huellassanas.model.Usuario;
import com.huellassanas.service.AuthService;
import com.huellassanas.service.ClinicaService;
import com.huellassanas.service.ServiceException;

import java.util.Optional;

/**
 * Controlador principal del sistema — capa MVC.
 *
 * <p><strong>Responsabilidad:</strong> actúa como punto de coordinación entre
 * las vistas (consola o Swing) y los servicios de negocio. Es completamente
 * <em>agnóstico</em> respecto a la interfaz de usuario: no imprime en consola
 * ni maneja componentes Swing directamente.</p>
 *
 * <p>Tanto la vista de consola como la vista Swing instancian y usan
 * este mismo controlador, garantizando un único punto de lógica de
 * control sin duplicación.</p>
 *
 * <h3>Contrato con las vistas:</h3>
 * <ul>
 *   <li>Las vistas llaman a los métodos públicos de este controlador.</li>
 *   <li>Este controlador retorna resultados ({@link Resultado}) en lugar
 *       de lanzar excepciones, permitiendo que cada vista decida cómo
 *       mostrar el mensaje de éxito o error.</li>
 * </ul>
 */
public class ClinicaController {

    // ─── Dependencias ─────────────────────────────────────────────────────────
    private final AuthService    authService;
    private final ClinicaService clinicaService;

    /**
     * Constructor con inyección de servicios.
     *
     * @param authService    servicio de autenticación y sesión
     * @param clinicaService servicio de negocio de la clínica
     */
    public ClinicaController(AuthService authService, ClinicaService clinicaService) {
        this.authService    = authService;
        this.clinicaService = clinicaService;
    }

    // ─── Resultado genérico (DTO de respuesta) ────────────────────────────────

    /**
     * DTO de respuesta del controlador hacia la vista.
     *
     * <p>Permite que la vista sepa si la operación fue exitosa
     * y muestre el mensaje apropiado sin manejar excepciones.</p>
     *
     * @param exito   {@code true} si la operación completó con éxito
     * @param mensaje descripción del resultado o del error ocurrido
     */
    public record Resultado(boolean exito, String mensaje) {
        public static Resultado ok(String msg)    { return new Resultado(true,  msg); }
        public static Resultado error(String msg) { return new Resultado(false, msg); }
    }

    // ─── Autenticación ────────────────────────────────────────────────────────

    /**
     * Intenta autenticar al usuario con las credenciales dadas.
     *
     * @param username      nombre de usuario
     * @param plainPassword contraseña en texto plano
     * @return {@link Resultado} con éxito/error y mensaje descriptivo
     */
    public Resultado iniciarSesion(String username, String plainPassword) {
        if (username == null || username.isBlank() ||
            plainPassword == null || plainPassword.isBlank()) {
            return Resultado.error("El usuario y la contraseña son obligatorios.");
        }
        boolean ok = authService.login(username, plainPassword);
        return ok
            ? Resultado.ok("Bienvenido, " + getUsuarioActual().map(Usuario::getNombreCompleto).orElse(username))
            : Resultado.error("Credenciales incorrectas o cuenta inactiva.");
    }

    /**
     * Cierra la sesión del usuario actual.
     *
     * @return {@link Resultado} confirmando el cierre de sesión
     */
    public Resultado cerrarSesion() {
        authService.logout();
        return Resultado.ok("Sesión cerrada correctamente.");
    }

    /**
     * Retorna el usuario actualmente autenticado.
     *
     * @return {@link Optional} con el usuario, o vacío si no hay sesión
     */
    public Optional<Usuario> getUsuarioActual() {
        return authService.getUsuarioActual();
    }

    /**
     * Verifica si existe una sesión activa.
     *
     * @return {@code true} si hay un usuario autenticado
     */
    public boolean haySesion() {
        return authService.haySesionActiva();
    }

    // ─── Operaciones de clínica ───────────────────────────────────────────────

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param cliente cliente a registrar (sin ID)
     * @return {@link Resultado} con éxito/error y el cliente registrado o mensaje
     */
    public Resultado registrarCliente(Cliente cliente) {
        if (!haySesion()) {
            return Resultado.error("Debe iniciar sesión para realizar esta operación.");
        }
        try {
            clinicaService.registrarCliente(cliente);
            return Resultado.ok("Cliente '" + cliente.getNombreCompleto() +
                                "' registrado con éxito. ID=" + cliente.getId());
        } catch (ServiceException e) {
            return Resultado.error("Error al registrar cliente: " + e.getMessage());
        }
    }

    /**
     * Registra una mascota y su primera cita de forma atómica.
     *
     * <p>Delega la transaccionalidad a
     * {@link ClinicaService#registrarMascotaYCita(Mascota, Cita)}.
     * Si el proceso falla, ningún dato queda en la base de datos.</p>
     *
     * @param mascota mascota a registrar
     * @param cita    primera cita asociada a la mascota
     * @return {@link Resultado} con éxito/error
     */
    public Resultado registrarMascotaYCita(Mascota mascota, Cita cita) {
        if (!haySesion()) {
            return Resultado.error("Debe iniciar sesión para realizar esta operación.");
        }
        try {
            clinicaService.registrarMascotaYCita(mascota, cita);
            return Resultado.ok("Mascota '" + mascota.getNombre() +
                                "' y cita registradas con éxito (MascotaId=" +
                                mascota.getId() + ", CitaId=" + cita.getId() + ").");
        } catch (ServiceException e) {
            return Resultado.error("Error en el registro atómico: " + e.getMessage());
        }
    }
}
