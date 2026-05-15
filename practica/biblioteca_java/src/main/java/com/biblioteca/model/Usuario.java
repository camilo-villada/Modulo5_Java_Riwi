package com.biblioteca.model;

public class Usuario {

    //atributos
    private int idUsuario;
    private String nombre;
    
    // constructores
    public Usuario() {}

    // constructor con parámetros
    public Usuario(int idUsuario, String nombre) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
    }


    // getters y setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    
}
