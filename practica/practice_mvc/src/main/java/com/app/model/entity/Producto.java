package com.app.model.entity;

import java.math.BigDecimal;

public class Producto {

    private int id;
    private String nombre;
    private BigDecimal precio;

    public Producto() {}

    public Producto(int id, String nombre, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }

    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=%s}", id, nombre, precio);
    }
}

