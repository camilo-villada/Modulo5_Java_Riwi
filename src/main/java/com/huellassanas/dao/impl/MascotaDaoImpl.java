package com.huellassanas.dao.impl;

import com.huellassanas.dao.DaoException;
import com.huellassanas.dao.MascotaDao;
import com.huellassanas.model.Mascota;
import com.huellassanas.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del contrato {@link MascotaDao}.
 * Todos los recursos se gestionan con {@code try-with-resources}.
 */
public class MascotaDaoImpl implements MascotaDao {

    private static final String SQL_INSERT =
            "INSERT INTO mascotas (nombre, especie, raza, fecha_nacimiento, peso, cliente_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM mascotas WHERE id = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM mascotas ORDER BY nombre";
    private static final String SQL_UPDATE =
            "UPDATE mascotas SET nombre=?, especie=?, raza=?, fecha_nacimiento=?, peso=?, cliente_id=? WHERE id=?";
    private static final String SQL_DELETE =
            "DELETE FROM mascotas WHERE id = ?";
    private static final String SQL_SELECT_BY_CLIENTE =
            "SELECT * FROM mascotas WHERE cliente_id = ? ORDER BY nombre";

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Mascota mapear(ResultSet rs) throws SQLException {
        Date fechaDate = rs.getDate("fecha_nacimiento");
        LocalDate fecha = (fechaDate != null) ? fechaDate.toLocalDate() : null;
        return new Mascota(
                rs.getInt("id"),
                rs.getString("nombre"),
                Mascota.Especie.valueOf(rs.getString("especie")),
                rs.getString("raza"),
                fecha,
                rs.getDouble("peso"),
                rs.getInt("cliente_id")
        );
    }

    @Override
    public Mascota guardar(Mascota m) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            return ejecutarGuardar(ps, m);
        } catch (SQLException e) {
            throw new DaoException("Error al guardar mascota: " + m.getNombre(), e);
        }
    }

    @Override
    public Mascota guardarConConexion(Connection conn, Mascota m) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            return ejecutarGuardar(ps, m);
        } catch (SQLException e) {
            throw new DaoException("Error al guardar mascota en transacción: " + m.getNombre(), e);
        }
    }

    private Mascota ejecutarGuardar(PreparedStatement ps, Mascota m) throws SQLException {
        ps.setString(1, m.getNombre());
        ps.setString(2, m.getEspecie().name());
        ps.setString(3, m.getRaza());
        ps.setDate(4, m.getFechaNacimiento() != null ? Date.valueOf(m.getFechaNacimiento()) : null);
        ps.setDouble(5, m.getPeso());
        ps.setInt(6, m.getClienteId());
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) m.setId(keys.getInt(1));
        }
        return m;
    }

    @Override
    public Optional<Mascota> buscarPorId(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar mascota id=" + id, e);
        }
    }

    @Override
    public List<Mascota> listarTodos() {
        List<Mascota> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(Mascota m) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getEspecie().name());
            ps.setString(3, m.getRaza());
            ps.setDate(4, m.getFechaNacimiento() != null ? Date.valueOf(m.getFechaNacimiento()) : null);
            ps.setDouble(5, m.getPeso());
            ps.setInt(6, m.getClienteId());
            ps.setInt(7, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar mascota id=" + m.getId(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar mascota id=" + id, e);
        }
    }

    @Override
    public List<Mascota> listarPorCliente(int clienteId) {
        List<Mascota> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_CLIENTE)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al listar mascotas del cliente id=" + clienteId, e);
        }
        return lista;
    }
}
