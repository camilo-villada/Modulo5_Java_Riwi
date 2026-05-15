package com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.biblioteca.database.DatabaseConnection;
import com.biblioteca.model.Usuario;

public class UsuarioDAO implements UsuarioDAOInterface {

    @Override
    public void registrarUsuario(String nombre) {
        // Implementación para registrar un nuevo usuario en la base de datos
        String sql = "INSERT INTO usuarios (nombre) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        
    }
    }

    @Override
    public Usuario obtenerUsuario(int idUsuario) {
        // Implementación para obtener un usuario por su ID desde la base de datos
        String sql = "SELECT idUsuario, nombre FROM usuarios WHERE idUsuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retornar el usuario obtenido
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        // Implementación para obtener todos los usuarios desde la base de datos
        String sql = "SELECT idUsuario, nombre FROM usuarios";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Usuario> usuarios = new java.util.ArrayList<>();
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuarios.add(usuario);
            }
            return usuarios;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retornar la lista de usuarios obtenida
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ? WHERE idUsuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setInt(2, usuario.getIdUsuario());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Implementación para actualizar la información de un usuario en la base de datos
    }

    @Override
    public void eliminarUsuario(int idUsuario) {
        // Implementación para eliminar un usuario por su ID desde la base de datos
        String sql = "DELETE FROM usuarios WHERE idUsuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


