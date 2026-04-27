package com.huellassanas.dao.impl;

import com.huellassanas.dao.DaoException;
import com.huellassanas.dao.UsuarioDao;
import com.huellassanas.model.Usuario;
import com.huellassanas.util.DatabaseConnection;
import com.huellassanas.util.SecurityUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC del contrato {@link UsuarioDao}.
 *
 * <p>Reglas de implementación:</p>
 * <ul>
 *   <li>Todos los recursos ({@link PreparedStatement}, {@link ResultSet}) se
 *       cierran con {@code try-with-resources} para evitar fugas.</li>
 *   <li>Las contraseñas se hashean con SHA-256 antes de cualquier escritura.</li>
 *   <li>Las excepciones JDBC se envuelven en {@link DaoException}.</li>
 * </ul>
 */
public class UsuarioDaoImpl implements UsuarioDao {

    // ─── SQL ──────────────────────────────────────────────────────────────────
    private static final String SQL_INSERT =
            "INSERT INTO usuarios (nombre, apellido, correo, telefono, username, password_hash, rol, activo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM usuarios WHERE id = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM usuarios ORDER BY apellido, nombre";
    private static final String SQL_UPDATE =
            "UPDATE usuarios SET nombre=?, apellido=?, correo=?, telefono=?, username=?, password_hash=?, rol=?, activo=? " +
            "WHERE id=?";
    private static final String SQL_DELETE =
            "DELETE FROM usuarios WHERE id = ?";
    private static final String SQL_SELECT_BY_USERNAME =
            "SELECT * FROM usuarios WHERE username = ?";
    private static final String SQL_AUTENTICAR =
            "SELECT * FROM usuarios WHERE username = ? AND password_hash = ? AND activo = TRUE";

    // ─── Helper: obtener conexión ─────────────────────────────────────────────
    private Connection conn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ─── Mapeo ResultSet → Entidad ────────────────────────────────────────────

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Usuario}.
     *
     * @param rs ResultSet posicionado en la fila a mapear
     * @return entidad Usuario construida con los datos de la fila
     * @throws SQLException si ocurre un error al leer columnas
     */
    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Usuario.Rol.valueOf(rs.getString("rol")),
                rs.getBoolean("activo")
        );
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    @Override
    public Usuario guardar(Usuario u) {
        // Hash de contraseña obligatorio antes de persistir
        String hash = SecurityUtil.hashSHA256(u.getPasswordHash());
        u.setPasswordHash(hash);

        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getUsername());
            ps.setString(6, u.getPasswordHash());
            ps.setString(7, u.getRolEnum().name());
            ps.setBoolean(8, u.isActivo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return u;
        } catch (SQLException e) {
            throw new DaoException("Error al guardar usuario: " + u.getUsername(), e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar usuario id=" + id, e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new DaoException("Error al listar usuarios.", e);
        }
        return lista;
    }

    @Override
    public boolean actualizar(Usuario u) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_UPDATE)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getUsername());
            ps.setString(6, u.getPasswordHash());
            ps.setString(7, u.getRolEnum().name());
            ps.setBoolean(8, u.isActivo());
            ps.setInt(9, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar usuario id=" + u.getId(), e);
        }
    }

    @Override
    public boolean eliminar(Integer id) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar usuario id=" + id, e);
        }
    }

    // ─── Operaciones específicas ──────────────────────────────────────────────

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        try (PreparedStatement ps = conn().prepareStatement(SQL_SELECT_BY_USERNAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar usuario por username: " + username, e);
        }
    }

    @Override
    public Optional<Usuario> autenticar(String username, String plainPassword) {
        String hash = SecurityUtil.hashSHA256(plainPassword);
        try (PreparedStatement ps = conn().prepareStatement(SQL_AUTENTICAR)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Error en autenticación para usuario: " + username, e);
        }
    }
}
