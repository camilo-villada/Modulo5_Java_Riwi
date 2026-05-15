package com.huellassanas.model;

/**
 * Clase abstracta base que representa a cualquier persona en el sistema.
 *
 * <p>Aplica el principio de herencia del paradigma POO: todas las entidades
 * humanas del sistema (Usuario, Cliente, Veterinario) extienden esta clase,
 * garantizando polimorfismo y reutilización de atributos comunes.</p>
 *
 * <p><strong>Encapsulamiento:</strong> todos los campos son {@code private};
 * el acceso se realiza exclusivamente a través de getters/setters.</p>
 */
public abstract class Persona {

    // ─── Atributos comunes ────────────────────────────────────────────────────
    private int    id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;

    // ─── Constructores ────────────────────────────────────────────────────────

    /** Constructor vacío necesario para instanciación desde ResultSet. */
    protected Persona() {}

    /**
     * Constructor completo para creación de personas con id conocido (DB).
     *
     * @param id       identificador único en base de datos
     * @param nombre   nombre de pila
     * @param apellido apellido(s)
     * @param correo   dirección de correo electrónico
     * @param telefono número de teléfono de contacto
     */
    protected Persona(int id, String nombre, String apellido,
                      String correo, String telefono) {
        this.id       = id;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.correo   = correo;
        this.telefono = telefono;
    }

    // ─── Método abstracto (polimorfismo) ──────────────────────────────────────

    /**
     * Retorna el rol que desempeña esta persona dentro del sistema.
     *
     * <p>Cada subclase debe proveer su propia implementación, lo que
     * permite tratar cualquier {@code Persona} de forma polimórfica
     * sin necesidad de {@code instanceof}.</p>
     *
     * @return cadena descriptiva del rol (p.ej. "CLIENTE", "VETERINARIO")
     */
    public abstract String getRol();

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public int getId()                 { return id; }
    public void setId(int id)          { this.id = id; }

    public String getNombre()          { return nombre; }
    public void setNombre(String n)    { this.nombre = n; }

    public String getApellido()        { return apellido; }
    public void setApellido(String a)  { this.apellido = a; }

    public String getCorreo()          { return correo; }
    public void setCorreo(String c)    { this.correo = c; }

    public String getTelefono()        { return telefono; }
    public void setTelefono(String t)  { this.telefono = t; }

    /** Nombre completo formateado (nombre + apellido). */
    public String getNombreCompleto()  { return nombre + " " + apellido; }

    @Override
    public String toString() {
        return String.format("[%s] id=%d | %s %s | %s",
                getRol(), id, nombre, apellido, correo);
    }
}
