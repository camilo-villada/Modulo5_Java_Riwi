package com.huellassanas.dao;

import com.huellassanas.model.Cliente;
import java.util.Optional;

/**
 * Contrato DAO específico para la entidad {@link Cliente}.
 */
public interface ClienteDao extends GenericDao<Cliente, Integer> {

    /**
     * Busca un cliente por su número de documento (DNI).
     *
     * @param dni número de documento de identidad
     * @return {@link Optional} con el cliente si existe
     */
    Optional<Cliente> buscarPorDni(String dni);
}
