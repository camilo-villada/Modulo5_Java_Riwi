package com.huellassanas.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link SecurityUtil}.
 *
 * <p>Verifica el comportamiento de la función de hashing SHA-256
 * sin necesidad de base de datos ni de Mockito (lógica pura).</p>
 */
@DisplayName("SecurityUtil — Tests de hashing SHA-256")
class SecurityUtilTest {

    // ─── hashSHA256 ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("hash produce 64 caracteres hexadecimales")
    void hashProduces64HexChars() {
        String hash = SecurityUtil.hashSHA256("huellasSanas2024");
        assertEquals(64, hash.length(),
                "SHA-256 hexadecimal debe tener exactamente 64 caracteres.");
    }

    @Test
    @DisplayName("mismo input siempre produce el mismo hash (determinismo)")
    void hashEsDeterminista() {
        String h1 = SecurityUtil.hashSHA256("miPassword");
        String h2 = SecurityUtil.hashSHA256("miPassword");
        assertEquals(h1, h2, "El hash de la misma entrada debe ser idéntico.");
    }

    @Test
    @DisplayName("inputs diferentes producen hashes diferentes")
    void hashesDistintosParaPasswordsDiferentes() {
        String h1 = SecurityUtil.hashSHA256("password1");
        String h2 = SecurityUtil.hashSHA256("password2");
        assertNotEquals(h1, h2, "Contraseñas distintas no deben producir el mismo hash.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("lanza IllegalArgumentException si input es nulo o vacío")
    void hashLanzaExcepcionConInputVacioONulo(String input) {
        assertThrows(IllegalArgumentException.class,
                () -> SecurityUtil.hashSHA256(input),
                "Debe lanzarse IllegalArgumentException con input nulo o vacío.");
    }

    // ─── verificar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("verificar retorna true cuando la contraseña coincide")
    void verificarRetornaTrueCuandoCoincide() {
        String hash = SecurityUtil.hashSHA256("admin123");
        assertTrue(SecurityUtil.verificar("admin123", hash),
                "verificar debe retornar true con la contraseña correcta.");
    }

    @Test
    @DisplayName("verificar retorna false con contraseña incorrecta")
    void verificarRetornaFalseCuandoNoCoincide() {
        String hash = SecurityUtil.hashSHA256("admin123");
        assertFalse(SecurityUtil.verificar("wrongPassword", hash),
                "verificar debe retornar false con contraseña incorrecta.");
    }

    @Test
    @DisplayName("verificar retorna false cuando el hash es nulo")
    void verificarRetornaFalseSiHashNulo() {
        assertFalse(SecurityUtil.verificar("password", null));
    }

    @Test
    @DisplayName("verificar retorna false cuando la contraseña es nula")
    void verificarRetornaFalseSiPasswordNula() {
        String hash = SecurityUtil.hashSHA256("algo");
        assertFalse(SecurityUtil.verificar(null, hash));
    }
}
