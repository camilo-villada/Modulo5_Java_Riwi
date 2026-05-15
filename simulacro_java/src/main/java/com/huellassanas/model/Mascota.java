package com.huellassanas.model;

import java.time.LocalDate;

/**
 * Entidad que representa a una mascota registrada en la clínica.
 *
 * <p>Mantiene la referencia al {@link Cliente} propietario mediante su ID
 * (clave foránea lógica) para evitar dependencias circulares de objetos.</p>
 */
public class Mascota {

    /** Especies soportadas por el sistema. */
    public enum Especie {
        PERRO, GATO, AVE, REPTIL, ROEDOR, OTRO
    }

    // ─── Atributos ────────────────────────────────────────────────────────────
    private int        id;
    private String     nombre;
    private Especie    especie;
    private String     raza;
    private LocalDate  fechaNacimiento;
    private double     peso;           // kg
    private int        clienteId;      // FK → clientes.id

    // ─── Constructores ────────────────────────────────────────────────────────

    public Mascota() {}

    /**
     * Constructor de persistencia (carga desde DB).
     *
     * @param id              PK en base de datos
     * @param nombre          nombre de la mascota
     * @param especie         tipo de especie
     * @param raza            raza específica
     * @param fechaNacimiento fecha de nacimiento (puede ser aproximada)
     * @param peso            peso en kilogramos
     * @param clienteId       ID del cliente dueño
     */
    public Mascota(int id, String nombre, Especie especie, String raza,
                   LocalDate fechaNacimiento, double peso, int clienteId) {
        this.id              = id;
        this.nombre          = nombre;
        this.especie         = especie;
        this.raza            = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.peso            = peso;
        this.clienteId       = clienteId;
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }

    public String getNombre()                  { return nombre; }
    public void setNombre(String n)            { this.nombre = n; }

    public Especie getEspecie()                { return especie; }
    public void setEspecie(Especie e)          { this.especie = e; }

    public String getRaza()                    { return raza; }
    public void setRaza(String r)              { this.raza = r; }

    public LocalDate getFechaNacimiento()      { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate f){ this.fechaNacimiento = f; }

    public double getPeso()                    { return peso; }
    public void setPeso(double p)              { this.peso = p; }

    public int getClienteId()                  { return clienteId; }
    public void setClienteId(int c)            { this.clienteId = c; }

    @Override
    public String toString() {
        return String.format("Mascota{id=%d, nombre='%s', especie=%s, raza='%s', clienteId=%d}",
                id, nombre, especie, raza, clienteId);
    }
}
