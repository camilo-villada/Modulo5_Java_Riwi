package com.huellassanas.model;

/**
 * Representa a un profesional veterinario de la clínica.
 *
 * <p>Además de los datos de persona, almacena el número de licencia
 * profesional y la especialidad médica del veterinario.</p>
 */
public class Veterinario extends Persona {

    // ─── Atributos propios ────────────────────────────────────────────────────
    private String licencia;
    private String especialidad;
    private boolean disponible;

    // ─── Constructores ────────────────────────────────────────────────────────

    public Veterinario() { super(); }

    /**
     * Constructor de persistencia (carga desde DB).
     *
     * @param id           PK en base de datos
     * @param nombre       nombre de pila
     * @param apellido     apellido(s)
     * @param correo       correo electrónico
     * @param telefono     teléfono de contacto
     * @param licencia     número de licencia profesional
     * @param especialidad área de especialización veterinaria
     * @param disponible   indica si el veterinario acepta citas nuevas
     */
    public Veterinario(int id, String nombre, String apellido, String correo,
                       String telefono, String licencia, String especialidad,
                       boolean disponible) {
        super(id, nombre, apellido, correo, telefono);
        this.licencia     = licencia;
        this.especialidad = especialidad;
        this.disponible   = disponible;
    }

    // ─── Polimorfismo ─────────────────────────────────────────────────────────

    @Override
    public String getRol() { return "VETERINARIO"; }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public String getLicencia()              { return licencia; }
    public void setLicencia(String l)        { this.licencia = l; }

    public String getEspecialidad()          { return especialidad; }
    public void setEspecialidad(String e)    { this.especialidad = e; }

    public boolean isDisponible()            { return disponible; }
    public void setDisponible(boolean d)     { this.disponible = d; }

    @Override
    public String toString() {
        return String.format("Veterinario{id=%d, licencia='%s', especialidad='%s', disponible=%b}",
                getId(), licencia, especialidad, disponible);
    }
}
