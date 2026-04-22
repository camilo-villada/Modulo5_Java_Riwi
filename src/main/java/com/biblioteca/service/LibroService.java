package com.biblioteca.service;

import com.biblioteca.dao.LibroDAO;
import com.biblioteca.model.Libro;

public class LibroService {

    private LibroDAO libroDAO = new LibroDAO();

    public LibroService() {
        this.libroDAO = new LibroDAO();
    }

    public void registrarLibro(String titulo, String autor) {
        libroDAO.registrarLibro(titulo, autor);
    }


    public Libro obtenerLibro(int idLibro) {
        return libroDAO.obtenerLibro(idLibro);
    }


    public java.util.List<Libro> obtenerTodosLosLibros() {
        return libroDAO.obtenerTodosLosLibros();
    }

    
    public void actualizarLibro(int idLibro, String titulo, String autor) {
        // Obtener el libro existente
        var libro = libroDAO.obtenerLibro(idLibro);
        if (libro != null) {
            // Actualizar los campos del libro
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            // Guardar los cambios en la base de datos
            libroDAO.actualizarLibro(libro);
        }
    }


    public void eliminarLibro(int idLibro) {
        libroDAO.eliminarLibro(idLibro);
    }

    
}