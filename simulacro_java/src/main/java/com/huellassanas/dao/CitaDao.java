package com.huellassanas.dao;

import com.huellassanas.model.Cita;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrato DAO específico para la entidad {@link Cita}.
 */
public interface CitaDao extends GenericDao<Cita, Integer> {

    /**
     * Retorna todas las citas de una mascota específica.
     *
     * @param mascotaId ID de la mascota
     * @return lista de citas (vacía si la mascota no tiene citas)
     */
    List<Cita> listarPorMascota(int mascotaId);

    /**
     * Retorna todas las citas asignadas a un veterinario en una fecha concreta.
     *
     * @param veterinarioId ID del veterinario
     * @param fecha         fecha a consultar
     * @return lista de citas del día para ese veterinario
     */
    List<Cita> listarPorVeterinarioYFecha(int veterinarioId, LocalDate fecha);
}
