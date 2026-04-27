package com.huellassanas.dao.impl;

import com.huellassanas.dao.DaoException;
import com.huellassanas.dao.HistorialClinicoDao;
import com.huellassanas.model.HistorialClinico;
import com.huellassanas.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del contrato {@link HistorialClinicoDao}.
 */
public class HistorialClinicoDaoImpl implements HistorialClinicoDao {

    private static final String SQL_INSERT =
            "INSERT INTO historial_clinico (mascota_id, fecha_hora, diagnostico, tratamiento) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM historial_clinico WHERE id = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM historial_clinico ORDER BY fecha_hora DESC";
    private static final String SQL_UPDATE =
            "UPDATE historial_clinico SET mascota_id=?, fecha_hora=?, diagnostico=?, tratamiento=? WHERE id=?";
    private static final String SQL_DELETE =
            "DELETE FROM historial_clinico WHERE id = ?";
    private static final String SQL_SELECT_BY_MASCOTA =
            "SELECT * FROM historial_clinico WHERE mascota_id = ? ORDER BY fecha_hora DESC";

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private HistorialClinico mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = (ts != null) ? ts.toLocalDateTime() : null;
        return new HistorialClinico(
                rs.getInt("id"),
                rs.getInt("mascota_id"),
                fechaHora,
                rs.getString("diagnostico"),
                rs.getString("tratamiento")
        );
    }

    @Override
    public HistorialClinico guardar(HistorialClinico h) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getMascotaId());
            ps.setTimestamp(2, h.getFechaHora() != null ? Timestamp.valueOf(h.getFechaHora()) : null);
            ps.setString(3, h.getDiagnostico());
            ps.setString(4, h.getTratamiento());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) h.setId(keys.getInt(1));
            }
            return h;
        } catch (SQLException e) {
            throw new DaoException("Error al guardar historial clínico de la mascota id=" + h.getMascotaId(), e);
        }
    }

    @Override
    public Optional<HistorialClinico> buscarPorId(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar historial clínico id=" + id, e);
        }
    }

    @Override
    public List<HistorialClinico> listarTodos() {
        List<HistorialClinico> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar historiales clínicos.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(HistorialClinico h) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_UPDATE)) {
            ps.setInt(1, h.getMascotaId());
            ps.setTimestamp(2, h.getFechaHora() != null ? Timestamp.valueOf(h.getFechaHora()) : null);
            ps.setString(3, h.getDiagnostico());
            ps.setString(4, h.getTratamiento());
            ps.setInt(5, h.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar historial clínico id=" + h.getId(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar historial clínico id=" + id, e);
        }
    }

    @Override
    public List<HistorialClinico> listarPorMascota(int mascotaId) {
        List<HistorialClinico> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_MASCOTA)) {
            ps.setInt(1, mascotaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar historial de mascota id=" + mascotaId, e);
        }
        return lista;
    }
}
