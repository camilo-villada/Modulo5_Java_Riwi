package com.huellassanas.dao;

import com.huellassanas.model.Mascota;
import java.util.List;

/**
 * Contrato DAO específico para la entidad {@link Mascota}.
 */
public interface MascotaDao extends GenericDao<Mascota, Integer> {

    /**
     * Retorna todas las mascotas pertenecientes a un cliente.
     *
     * @param clienteId ID del cliente propietario
     * @return lista de mascotas (vacía si el cliente no tiene mascotas)
     */
    List<Mascota> listarPorCliente(int clienteId);

    /**
     * Versión transaccional: acepta una conexión externa.
     *
     * @param conn conexión activa con autoCommit=false
     * @param m mascota a persistir
     * @return mascota con ID generado
     */
    Mascota guardarConConexion(java.sql.Connection conn, Mascota m);
}
