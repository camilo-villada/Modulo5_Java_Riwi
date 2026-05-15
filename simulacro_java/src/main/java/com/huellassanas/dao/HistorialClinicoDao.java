package com.huellassanas.dao;

import com.huellassanas.model.HistorialClinico;
import java.util.List;

/**
 * Contrato DAO específico para la entidad {@link HistorialClinico}.
 */
public interface HistorialClinicoDao extends GenericDao<HistorialClinico, Integer> {

    /**
     * Retorna todos los registros de historial clínico de una mascota específica.
     *
     * @param mascotaId ID de la mascota
     * @return lista de registros de historial clínico, ordenados cronológicamente
     */
    List<HistorialClinico> listarPorMascota(int mascotaId);
}
