package com.huellassanas.dao.impl;

import com.huellassanas.dao.ClienteDao;
import com.huellassanas.dao.DaoException;
import com.huellassanas.model.Cliente;
import com.huellassanas.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del contrato {@link ClienteDao}.
 * Todos los recursos se gestionan con {@code try-with-resources}.
 */
public class ClienteDaoImpl implements ClienteDao {

    private static final String SQL_INSERT =
            "INSERT INTO clientes (nombre, apellido, correo, telefono, dni, direccion) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM clientes WHERE id = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM clientes ORDER BY apellido, nombre";
    private static final String SQL_UPDATE =
            "UPDATE clientes SET nombre=?, apellido=?, correo=?, telefono=?, dni=?, direccion=? WHERE id=?";
    private static final String SQL_DELETE =
            "DELETE FROM clientes WHERE id = ?";
    private static final String SQL_SELECT_BY_DNI =
            "SELECT * FROM clientes WHERE dni = ?";

    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("dni"),
                rs.getString("direccion")
        );
    }

    @Override
    public Cliente guardar(Cliente c) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDni());
            ps.setString(6, c.getDireccion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            throw new DaoException("Error al guardar cliente: " + c.getDni(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente id=" + id, e);
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar clientes.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(Cliente c) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getCorreo());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDni());
            ps.setString(6, c.getDireccion());
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar cliente id=" + c.getId(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar cliente id=" + id, e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorDni(String dni) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_DNI)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar cliente por DNI: " + dni, e);
        }
    }
}
