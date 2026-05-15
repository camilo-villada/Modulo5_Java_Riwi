package com.biblioteca.dao;

import com.biblioteca.model.Prestamo;
import java.util.List;

public interface PrestamoDAOInterface {
    
    
    boolean registrarPrestamo(Prestamo prestamo);
    List<Prestamo> obtenerPrestamosPorUsuario(int idUsuario);
    boolean devolverLibro(int idPrestamo);
}
