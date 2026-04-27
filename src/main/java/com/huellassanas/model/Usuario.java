package com.huellassanas.model;

/**
 * Representa a un usuario del sistema con credenciales de acceso.
 *
 * <p>La contraseña <strong>nunca se almacena en texto plano</strong>;
 * el campo {@code passwordHash} contiene únicamente el hash SHA-256
 * calculado por {@link com.huellassanas.util.SecurityUtil#hashSHA256(String)}.</p>
 */
public class Usuario extends Persona {

    /** Roles disponibles en el sistema. */
    public enum Rol {
        ADMINISTRADOR,
        RECEPCIONISTA,
        VETERINARIO
    }

    // ─── Atributos propios ────────────────────────────────────────────────────
    private String username;
    private String passwordHash;   // SHA-256, nunca texto plano
    private Rol    rol;
    private boolean activo;

    // ─── Constructores ────────────────────────────────────────────────────────

    public Usuario() { super(); }

    /**
     * Constructor de persistencia (carga desde DB).
     *
     * @param id           PK en base de datos
     * @param nombre       nombre de pila
     * @param apellido     apellido(s)
     * @param correo       correo electrónico
     * @param telefono     teléfono de contacto
     * @param username     nombre de usuario único
     * @param passwordHash hash SHA-256 de la contraseña
     * @param rol          rol asignado
     * @param activo       estado de la cuenta
     */
    public Usuario(int id, String nombre, String apellido, String correo,
                   String telefono, String username, String passwordHash,
                   Rol rol, boolean activo) {
        super(id, nombre, apellido, correo, telefono);
        this.username     = username;
        this.passwordHash = passwordHash;
        this.rol          = rol;
        this.activo       = activo;
    }

    // ─── Polimorfismo ─────────────────────────────────────────────────────────

    @Override
    public String getRol() {
        return rol != null ? rol.name() : "SIN_ROL";
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public String getUsername()                  { return username; }
    public void setUsername(String u)            { this.username = u; }

    public String getPasswordHash()              { return passwordHash; }
    public void setPasswordHash(String h)        { this.passwordHash = h; }

    public Rol getRolEnum()                      { return rol; }
    public void setRolEnum(Rol rol)              { this.rol = rol; }

    public boolean isActivo()                    { return activo; }
    public void setActivo(boolean activo)        { this.activo = activo; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, username='%s', rol=%s, activo=%b}",
                getId(), username, rol, activo);
    }
}
