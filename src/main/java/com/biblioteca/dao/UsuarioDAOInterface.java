package com.biblioteca.dao;

import java.util.List;

import com.biblioteca.model.Usuario;

public interface UsuarioDAOInterface {
    
    void registrarUsuario(String nombre);
    Usuario obtenerUsuario(int idUsuario);
    List<Usuario> obtenerTodosLosUsuarios();
    void actualizarUsuario(Usuario usuario);
    void eliminarUsuario(int idUsuario);
    
}
