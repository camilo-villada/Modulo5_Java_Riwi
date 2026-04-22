package com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.biblioteca.database.DatabaseConnection;
import com.biblioteca.model.Libro;

public class LibroDAO implements LibroDAOInterface {

    @Override
    public void registrarLibro(String titulo, String autor) {
        // Implementación para registrar un libro en la base de datos
        String sql = "INSERT INTO libros (titulo, autor, disponible) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setBoolean(3, true);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Libro obtenerLibro(int idLibro) {
        // Implementación para obtener un libro por su ID desde la base de datos
        String sql = "SELECT idLibro, titulo, autor FROM libros WHERE idLibro = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro();
                    libro.setIdLibro(rs.getInt("idLibro"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    return libro;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retornar el libro obtenido
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        // Implementación para obtener todos los libros desde la base de datos
        String sql = "SELECT idLibro, titulo, autor FROM libros";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            List<Libro> libros = new java.util.ArrayList<>();
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("idLibro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libros.add(libro);
            }
            return libros;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Retornar la lista de libros obtenida
    }

    @Override
    public void actualizarLibro(Libro libro) {
        // Implementación para actualizar un libro en la base de datos
        String sql = "UPDATE libros SET titulo = ?, autor = ? WHERE idLibro = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setInt(3, libro.getIdLibro());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminarLibro(int idLibro) {
        // Implementación para eliminar un libro por su ID desde la base de datos
        String sql = "DELETE FROM libros WHERE idLibro = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLibro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
