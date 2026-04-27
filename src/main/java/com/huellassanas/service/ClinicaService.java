package com.huellassanas.service;

import com.huellassanas.dao.ClienteDao;
import com.huellassanas.dao.DaoException;
import com.huellassanas.dao.MascotaDao;
import com.huellassanas.dao.impl.CitaDaoImpl;
import com.huellassanas.model.Cita;
import com.huellassanas.model.Cliente;
import com.huellassanas.model.Mascota;
import com.huellassanas.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

/**
 * Servicio de negocio de la clínica veterinaria.
 *
 * <p>Esta capa es el núcleo de la lógica de negocio:
 * <ul>
 *   <li>Coordina múltiples DAOs para operaciones complejas.</li>
 *   <li>Implementa la <strong>atomicidad</strong> de la operación crítica
 *       {@link #registrarMascotaYCita} mediante manejo explícito de
 *       transacciones JDBC ({@code setAutoCommit}, {@code commit},
 *       {@code rollback}).</li>
 *   <li>Es completamente agnóstica de la interfaz de usuario.</li>
 * </ul>
 * </p>
 */
public class ClinicaService {

    // ─── Dependencias (inyección por constructor) ─────────────────────────────
    private final ClienteDao  clienteDao;
    private final MascotaDao  mascotaDao;
    private final CitaDaoImpl citaDaoImpl;   // Referencia concreta para guardarConConexion

    /**
     * Constructor con inyección de dependencias.
     *
     * @param clienteDao  DAO de clientes
     * @param mascotaDao  DAO de mascotas
     * @param citaDaoImpl implementación concreta del DAO de citas
     *                    (necesaria para la variante transaccional)
     */
    public ClinicaService(ClienteDao clienteDao,
                          MascotaDao mascotaDao,
                          CitaDaoImpl citaDaoImpl) {
        this.clienteDao  = clienteDao;
        this.mascotaDao  = mascotaDao;
        this.citaDaoImpl = citaDaoImpl;
    }

    // ─── Flujo Crítico: Registro Atómico ──────────────────────────────────────

    /**
     * Registra una {@link Mascota} y su primera {@link Cita} de forma
     * <strong>atómica</strong>: si cualquiera de los dos INSERT falla,
     * se hace {@code rollback} y no quedan datos huérfanos en la base de datos.
     *
     * <p><strong>Flujo transaccional:</strong></p>
     * <ol>
     *   <li>Obtiene la conexión compartida.</li>
     *   <li>Deshabilita el auto-commit: {@code setAutoCommit(false)}.</li>
     *   <li>INSERT mascota → recupera ID generado.</li>
     *   <li>Asigna el ID de la mascota a la cita.</li>
     *   <li>INSERT cita (dentro de la misma conexión/transacción).</li>
     *   <li>{@code commit()} si ambos INSERT tuvieron éxito.</li>
     *   <li>{@code rollback()} en bloque {@code catch} si alguno falla.</li>
     *   <li>Restaura {@code autoCommit(true)} en bloque {@code finally}.</li>
     * </ol>
     *
     * @param mascota mascota a registrar (sin ID)
     * @param cita    primera cita asociada (sin mascotaId ni ID)
     * @throws ServiceException si la transacción falla o si los datos
     *                          de entrada no son válidos
     */
    public void registrarMascotaYCita(Mascota mascota, Cita cita) {
        validarRegistro(mascota, cita);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);  // ← Inicia transacción manual

            // Paso 1: Persistir la mascota en la transacción
            mascotaDao.guardarConConexion(conn, mascota);

            // Paso 2: Vincular el ID de la mascota recién creada a la cita
            cita.setMascotaId(mascota.getId());

            // Paso 3: Persistir la cita dentro de la MISMA conexión/transacción
            citaDaoImpl.guardarConConexion(conn, cita);

            conn.commit();  // ← Confirma ambas operaciones atomicamente
            System.out.println("[ClinicaService] Mascota y cita registradas con éxito. " +
                               "MascotaId=" + mascota.getId() + ", CitaId=" + cita.getId());

        } catch (SQLException | DaoException e) {
            // Rollback: revierte todo si cualquier operación falló
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[ClinicaService] ROLLBACK ejecutado. Causa: " + e.getMessage());
                } catch (SQLException rbEx) {
                    System.err.println("[ClinicaService] Error en ROLLBACK: " + rbEx.getMessage());
                }
            }
            throw new ServiceException("No se pudo registrar la mascota y la cita de forma atómica.", e);
        } finally {
            // Siempre restaurar autoCommit a true para no afectar otras operaciones
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("[ClinicaService] Error al restaurar autoCommit: " + e.getMessage());
                }
            }
        }
    }

    // ─── Validaciones de negocio ──────────────────────────────────────────────

    /**
     * Valida que los datos mínimos de mascota y cita sean correctos antes
     * de iniciar la transacción.
     *
     * @param mascota mascota a validar
     * @param cita    cita a validar
     * @throws ServiceException si algún dato es inválido
     */
    private void validarRegistro(Mascota mascota, Cita cita) {
        if (mascota == null) {
            throw new ServiceException("La mascota no puede ser nula.");
        }
        if (mascota.getClienteId() <= 0) {
            throw new ServiceException("La mascota debe estar asociada a un cliente válido.");
        }
        if (cita == null) {
            throw new ServiceException("La cita no puede ser nula.");
        }
        if (cita.getVeterinarioId() <= 0) {
            throw new ServiceException("La cita debe tener un veterinario asignado.");
        }
        if (cita.getFechaHora() == null) {
            throw new ServiceException("La cita debe tener una fecha y hora válidas.");
        }
        validarConflictoCita(cita);
    }

    /**
     * Valida que no haya conflicto de horario para el mismo veterinario.
     * Se considera un conflicto si hay una cita agendada dentro de un margen
     * de 30 minutos de diferencia.
     *
     * @param nuevaCita cita a validar
     * @throws ServiceException si hay conflicto
     */
    private void validarConflictoCita(Cita nuevaCita) {
        List<Cita> citasDelDia = citaDaoImpl.listarPorVeterinarioYFecha(
                nuevaCita.getVeterinarioId(),
                nuevaCita.getFechaHora().toLocalDate()
        );

        for (Cita existente : citasDelDia) {
            long minutosDiferencia = Math.abs(Duration.between(
                    existente.getFechaHora(), nuevaCita.getFechaHora()
            ).toMinutes());

            if (minutosDiferencia < 30) {
                throw new ServiceException("Conflicto de agenda: El veterinario ya tiene una cita a las " +
                        existente.getFechaHora() + " (margen mínimo 30 min).");
            }
        }
    }

    // ─── Operaciones simples (no transaccionales) ─────────────────────────────

    /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param cliente cliente a persistir
     * @return cliente con ID asignado
     */
    public Cliente registrarCliente(Cliente cliente) {
        if (cliente == null || cliente.getDni() == null || cliente.getDni().isBlank()) {
            throw new ServiceException("El cliente debe tener un DNI válido.");
        }
        return clienteDao.guardar(cliente);
    }
}
