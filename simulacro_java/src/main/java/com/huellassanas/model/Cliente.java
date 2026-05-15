package com.huellassanas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa al dueño/tutor de una o varias mascotas registradas en la clínica.
 *
 * <p>La lista de mascotas es de solo lectura desde el exterior para preservar
 * la integridad del grafo de objetos; las modificaciones se delegan al DAO.</p>
 */
public class Cliente extends Persona {

    // ─── Atributos propios ────────────────────────────────────────────────────
    private String dni;
    private String direccion;

    /**
     * Relación de composición: las mascotas pertenecen al cliente.
     * No se persiste directamente aquí; se carga de forma lazy desde el DAO.
     */
    private final List<Mascota> mascotas = new ArrayList<>();

    // ─── Constructores ────────────────────────────────────────────────────────

    public Cliente() { super(); }

    /**
     * Constructor de persistencia (carga desde DB).
     *
     * @param id        PK en base de datos
     * @param nombre    nombre de pila
     * @param apellido  apellido(s)
     * @param correo    correo electrónico
     * @param telefono  teléfono de contacto
     * @param dni       documento nacional de identidad
     * @param direccion dirección postal
     */
    public Cliente(int id, String nombre, String apellido, String correo,
                   String telefono, String dni, String direccion) {
        super(id, nombre, apellido, correo, telefono);
        this.dni       = dni;
        this.direccion = direccion;
    }

    // ─── Polimorfismo ─────────────────────────────────────────────────────────

    @Override
    public String getRol() { return "CLIENTE"; }

    // ─── Gestión de mascotas (composición) ───────────────────────────────────

    /**
     * Agrega una mascota a la lista interna.
     *
     * @param mascota la mascota a agregar (no nula)
     */
    public void agregarMascota(Mascota mascota) {
        if (mascota != null) mascotas.add(mascota);
    }

    /**
     * Retorna una vista inmutable de la lista de mascotas.
     *
     * @return lista no modificable de {@link Mascota}
     */
    public List<Mascota> getMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public String getDni()               { return dni; }
    public void setDni(String dni)       { this.dni = dni; }

    public String getDireccion()         { return direccion; }
    public void setDireccion(String d)   { this.direccion = d; }

    @Override
    public String toString() {
        return String.format("Cliente{id=%d, dni='%s', nombre='%s', mascotas=%d}",
                getId(), dni, getNombreCompleto(), mascotas.size());
    }
}
