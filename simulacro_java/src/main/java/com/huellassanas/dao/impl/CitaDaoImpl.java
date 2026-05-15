package com.huellassanas.dao.impl;

import com.huellassanas.dao.CitaDao;
import com.huellassanas.dao.DaoException;
import com.huellassanas.model.Cita;
import com.huellassanas.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del contrato {@link CitaDao}.
 *
 * <p>El método {@link #guardar(Cita)} recibe una {@link Connection} externa
 * cuando se llama desde el flujo transaccional
 * {@code ClinicaService.registrarMascotaYCita()}, evitando abrir una
 * segunda conexión dentro de la misma transacción.</p>
 */
public class CitaDaoImpl implements CitaDao {

    private static final String SQL_INSERT =
            "INSERT INTO citas (mascota_id, veterinario_id, fecha_hora, motivo, diagnostico, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM citas WHERE id = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM citas ORDER BY fecha_hora DESC";
    private static final String SQL_UPDATE =
            "UPDATE citas SET mascota_id=?, veterinario_id=?, fecha_hora=?, motivo=?, diagnostico=?, estado=? WHERE id=?";
    private static final String SQL_DELETE =
            "DELETE FROM citas WHERE id = ?";
    private static final String SQL_BY_MASCOTA =
            "SELECT * FROM citas WHERE mascota_id = ? ORDER BY fecha_hora DESC";
    private static final String SQL_BY_VET_FECHA =
            "SELECT * FROM citas WHERE veterinario_id = ? AND DATE(fecha_hora) = ? ORDER BY fecha_hora";

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Cita mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = (ts != null) ? ts.toLocalDateTime() : null;
        return new Cita(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                rs.getInt("veterinario_id"),
                fechaHora,
                rs.getString("motivo"),
                rs.getString("diagnostico"),
                Cita.EstadoCita.valueOf(rs.getString("estado"))
        );
    }

    // ─── CRUD estándar ────────────────────────────────────────────────────────

    @Override
    public Cita guardar(Cita c) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            return ejecutarGuardar(ps, c);
        } catch (SQLException e) {
            throw new DaoException("Error al guardar cita para mascota id=" + c.getMascotaId(), e);
        }
    }

    /**
     * Versión transaccional: acepta una {@link Connection} externa controlada
     * por la capa de servicio. El llamador gestiona commit/rollback.
     *
     * @param conn conexión activa con autoCommit=false
     * @param cita cita a persistir
     * @return cita con ID generado
     * @throws DaoException si ocurre un error SQL
     */
    public Cita guardarConConexion(Connection conn, Cita cita) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            return ejecutarGuardar(ps, cita);
        } catch (SQLException e) {
            throw new DaoException("Error al guardar cita en transacción.", e);
        }
    }

    /** Lógica común de inserción para ambas variantes de guardar. */
    private Cita ejecutarGuardar(PreparedStatement ps, Cita c) throws SQLException {
        ps.setInt(1, c.getMascotaId());
        ps.setInt(2, c.getVeterinarioId());
        ps.setTimestamp(3, c.getFechaHora() != null ? Timestamp.valueOf(c.getFechaHora()) : null);
        ps.setString(4, c.getMotivo());
        ps.setString(5, c.getDiagnostico());
        ps.setString(6, c.getEstado().name());
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) c.setId(keys.getInt(1));
        }
        return c;
    }

    @Override
    public Optional<Cita> buscarPorId(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar cita id=" + id, e);
        }
    }

    @Override
    public List<Cita> listarTodos() {
        List<Cita> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar citas.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(Cita c) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, c.getMascotaId());
            ps.setInt(2, c.getVeterinarioId());
            ps.setTimestamp(3, c.getFechaHora() != null ? Timestamp.valueOf(c.getFechaHora()) : null);
            ps.setString(4, c.getMotivo());
            ps.setString(5, c.getDiagnostico());
            ps.setString(6, c.getEstado().name());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar cita id=" + c.getId(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar cita id=" + id, e);
        }
    }

    // ─── Consultas específicas ────────────────────────────────────────────────

    @Override
    public List<Cita> listarPorMascota(int mascotaId) {
        List<Cita> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_BY_MASCOTA)) {
            ps.setInt(1, mascotaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar citas de mascota id=" + mascotaId, e);
        }
        return lista;
    }

    @Override
    public List<Cita> listarPorVeterinarioYFecha(int veterinarioId, LocalDate fecha) {
        List<Cita> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_BY_VET_FECHA)) {
            ps.setInt(1, veterinarioId);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar citas del veterinario id=" + veterinarioId, e);
        }
        return lista;
    }
}
