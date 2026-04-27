package com.huellassanas.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Módulo de seguridad: hashing de contraseñas con SHA-256.
 *
 * <p>Las contraseñas <strong>nunca</strong> se guardan en texto plano.
 * Antes de cualquier operación INSERT/UPDATE sobre la tabla {@code usuarios},
 * se debe llamar a {@link #hashSHA256(String)} para obtener el hash
 * hexadecimal que se persiste en la columna {@code password_hash}.</p>
 *
 * <p>Para verificar una contraseña ingresada por el usuario, se aplica el
 * mismo hash y se compara con el valor almacenado en DB usando
 * {@link #verificar(String, String)}.</p>
 *
 * <h3>Ejemplo de uso:</h3>
 * <pre>{@code
 * // Al registrar:
 * String hash = SecurityUtil.hashSHA256("miContraseña123");
 * usuarioDao.guardar(usuario);   // usuario.setPasswordHash(hash) previo
 *
 * // Al autenticar:
 * boolean ok = SecurityUtil.verificar("miContraseña123", hashGuardadoEnDB);
 * }</pre>
 */
public final class SecurityUtil {

    /** Constructor privado: clase de utilidad, no instanciable. */
    private SecurityUtil() {
        throw new UnsupportedOperationException("Clase de utilidad estática.");
    }

    /**
     * Calcula el hash SHA-256 de una cadena y lo retorna en formato hexadecimal.
     *
     * @param input texto plano a hashear (p.ej. la contraseña del usuario)
     * @return cadena hexadecimal de 64 caracteres con el hash SHA-256
     * @throws IllegalArgumentException si {@code input} es nulo o vacío
     * @throws RuntimeException         si el algoritmo SHA-256 no está disponible
     *                                  (no ocurre en JDK estándar)
     */
    public static String hashSHA256(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("La cadena a hashear no puede ser nula o vacía.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash   = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 siempre está disponible en JDK ≥ 1.4
            throw new RuntimeException("SHA-256 no disponible en esta JVM.", e);
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash SHA-256.
     *
     * @param plainPassword   contraseña ingresada por el usuario
     * @param storedHash      hash recuperado de la base de datos
     * @return {@code true} si coinciden; {@code false} en caso contrario
     */
    public static boolean verificar(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        return hashSHA256(plainPassword).equalsIgnoreCase(storedHash);
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Convierte un arreglo de bytes a representación hexadecimal.
     *
     * @param bytes arreglo de bytes (salida del MessageDigest)
     * @return cadena hexadecimal en minúsculas
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
