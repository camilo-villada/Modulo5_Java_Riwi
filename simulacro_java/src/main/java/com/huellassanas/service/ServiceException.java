package com.huellassanas.service;

/**
 * Excepción no verificada de la capa de servicio.
 *
 * <p>Envuelve errores de negocio o de persistencia que se producen en
 * {@link ClinicaService} y otros servicios, evitando que las excepciones
 * checked de JDBC se propaguen hacia la capa de presentación.</p>
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
