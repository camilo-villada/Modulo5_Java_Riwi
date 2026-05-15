package com.biblioteca.dao;

import java.util.List;

import com.biblioteca.model.Libro;

public interface LibroDAOInterface {
    
    void registrarLibro(String titulo, String autor);
    Libro obtenerLibro(int idLibro);
    List<Libro> obtenerTodosLosLibros();
    void actualizarLibro(Libro libro);
    void eliminarLibro(int idLibro);
}
