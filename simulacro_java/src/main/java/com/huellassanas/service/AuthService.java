package com.huellassanas.service;

import com.huellassanas.dao.UsuarioDao;
import com.huellassanas.model.Usuario;
import com.huellassanas.util.SecurityUtil;

import java.util.Optional;

/**
 * Servicio de autenticación y gestión de sesión del sistema.
 *
 * <p>Gestiona el ciclo de vida de la sesión del usuario actual:
 * login, logout y consulta del usuario activo.</p>
 *
 * <p>Las contraseñas se validan a través de {@link SecurityUtil#verificar}
 * para nunca comparar texto plano directamente.</p>
 */
public class AuthService {

    private final UsuarioDao usuarioDao;

    /** Usuario con sesión activa; {@code null} si no hay sesión. */
    private Usuario usuarioActual;

    /**
     * Constructor con inyección del DAO de usuarios.
     *
     * @param usuarioDao DAO para consultar credenciales en la DB
     */
    public AuthService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     *
     * <p>El hash SHA-256 de {@code plainPassword} se calcula internamente
     * y se compara con el {@code password_hash} almacenado en la DB.</p>
     *
     * @param username      nombre de usuario
     * @param plainPassword contraseña en texto plano
     * @return {@code true} si la autenticación fue exitosa; {@code false} si no
     */
    public boolean login(String username, String plainPassword) {
        Optional<Usuario> resultado = usuarioDao.autenticar(username, plainPassword);
        if (resultado.isPresent()) {
            usuarioActual = resultado.get();
            System.out.println("[AuthService] Sesión iniciada: " + usuarioActual);
            return true;
        }
        System.err.println("[AuthService] Credenciales inválidas para: " + username);
        return false;
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    /**
     * Cierra la sesión del usuario actual.
     */
    public void logout() {
        if (usuarioActual != null) {
            System.out.println("[AuthService] Sesión cerrada: " + usuarioActual.getUsername());
        }
        usuarioActual = null;
    }

    // ─── Consulta de sesión ───────────────────────────────────────────────────

    /**
     * Retorna el usuario con sesión activa.
     *
     * @return {@link Optional} con el usuario si hay sesión; vacío si no
     */
    public Optional<Usuario> getUsuarioActual() {
        return Optional.ofNullable(usuarioActual);
    }

    /**
     * Verifica si hay una sesión activa.
     *
     * @return {@code true} si un usuario ha iniciado sesión
     */
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    /**
     * Verifica si el usuario actual tiene un rol específico.
     *
     * @param rol rol a verificar
     * @return {@code true} si el usuario activo tiene ese rol
     */
    public boolean tieneRol(Usuario.Rol rol) {
        return haySesionActiva() && usuarioActual.getRolEnum() == rol;
    }
}
