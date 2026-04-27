package com.huellassanas.dao;

/**
 * Excepción no verificada (unchecked) para encapsular errores de persistencia.
 *
 * <p>Envuelve las {@link java.sql.SQLException} checked en una excepción
 * de runtime, evitando que los detalles de JDBC se propaguen hacia la capa
 * de servicio o de presentación.</p>
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
