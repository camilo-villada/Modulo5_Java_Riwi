package com.biblioteca.service;

import java.util.List;

import com.biblioteca.dao.UsuarioDAO;
import com.biblioteca.model.Usuario;

public class UsuarioService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void registrarUsuario(String nombre) {
        usuarioDAO.registrarUsuario(nombre);
    }

    public Usuario obtenerUsuario(int idUsuario) {
        return usuarioDAO.obtenerUsuario(idUsuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioDAO.obtenerTodosLosUsuarios();
    }
    
    
}
