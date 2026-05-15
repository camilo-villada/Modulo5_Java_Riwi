package com.biblioteca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.biblioteca.database.DatabaseConnection;
import com.biblioteca.model.Prestamo;


public class PrestamoDAO implements PrestamoDAOInterface {
    
    @Override
    public boolean registrarPrestamo(Prestamo prestamo) {
        // Implementar lógica para registrar un préstamo en la base de datos

        String sql = "INSERT INTO prestamos (idLibro, idUsuario, fecha) VALUES (?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prestamo.getIdLibro());
            stmt.setInt(2, prestamo.getIdUsuario());
            stmt.setDate(3, new java.sql.Date(prestamo.getFecha().getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario) {
        
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT idPrestamo, idLibro, idUsuario, fecha FROM prestamos WHERE idUsuario = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    Prestamo prestamo = new Prestamo();
                    prestamo.setIdPrestamo(rs.getInt("idPrestamo"));
                    prestamo.setIdLibro(rs.getInt("idLibro"));
                    prestamo.setIdUsuario(rs.getInt("idUsuario"));
                    prestamo.setFecha(rs.getDate("fecha"));
                    prestamos.add(prestamo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    @Override
    public boolean devolverLibro(int idPrestamo) {

        String sql = "DELETE FROM prestamos WHERE idPrestamo = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPrestamo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

  

    
        
}
