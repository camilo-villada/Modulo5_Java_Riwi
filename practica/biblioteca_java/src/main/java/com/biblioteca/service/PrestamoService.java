package com.biblioteca.service;

import com.biblioteca.dao.PrestamoDAO;
import com.biblioteca.model.Prestamo;


public class PrestamoService {
    
    private PrestamoDAO prestamoDAO = new PrestamoDAO();

    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
    }


    public void registrarPrestamo(int idLibro, int idUsuario) {
        Prestamo prestamo = new Prestamo();
        prestamo.setIdLibro(idLibro);
        prestamo.setIdUsuario(idUsuario);
        prestamo.setFecha(new java.util.Date());
    
        boolean ok = prestamoDAO.registrarPrestamo(prestamo);
        if (ok) {
            System.out.println("Préstamo registrado exitosamente.");
        } else {
            System.out.println("Error al registrar el préstamo.");
        }
    }


    public void listarPrestamosPorUsuario(int idUsuario) {
        
        prestamoDAO.obtenerPrestamosPorUsuario(idUsuario).forEach(prestamo -> {
            System.out.println("ID Préstamo: " + prestamo.getIdPrestamo() +
                               ", ID Libro: " + prestamo.getIdLibro() +
                               ", Fecha: " + prestamo.getFecha());
        });
    }


    public void devolverLibro(int idPrestamo) {
        boolean ok = prestamoDAO.devolverLibro(idPrestamo);
        if (ok) {
            System.out.println("Libro devuelto exitosamente.");
        } else {
            System.out.println("Error al devolver el libro.");
        }
    }


}