package com.biblioteca.model;

import java.util.Date;

public class Prestamo {
    
    //atributos
    private int idPrestamo;
    private int idLibro;
    private int idUsuario;
    private Date fecha;

    // constructores
    public Prestamo() {}

    // constructor con parámetros
    public Prestamo(int idPrestamo, int idLibro, int idUsuario, Date fecha) {
        this.idPrestamo = idPrestamo;
        this.idLibro = idLibro;     
        this.idUsuario = idUsuario;
        this.fecha = fecha;
    }

    // getters y setters

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    
}
