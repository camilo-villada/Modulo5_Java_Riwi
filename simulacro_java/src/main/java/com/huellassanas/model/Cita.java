package com.huellassanas.model;

import java.time.LocalDateTime;

/**
 * Entidad que representa una cita médica en la clínica.
 *
 * <p>Una cita vincula a un {@link Mascota}, un {@link Veterinario}
 * y una fecha/hora específica. El estado sigue un ciclo de vida
 * definido por el enum {@link EstadoCita}.</p>
 */
public class Cita {

    /** Ciclo de vida de una cita. */
    public enum EstadoCita {
        PENDIENTE,
        CONFIRMADA,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    // ─── Atributos ────────────────────────────────────────────────────────────
    private int           id;
    private int           mascotaId;       // FK → mascotas.id
    private int           veterinarioId;   // FK → veterinarios.id
    private LocalDateTime fechaHora;
    private String        motivo;
    private String        diagnostico;     // Se completa post-consulta
    private EstadoCita    estado;

    // ─── Constructores ────────────────────────────────────────────────────────

    public Cita() {}

    /**
     * Constructor mínimo para registrar una nueva cita (sin diagnóstico aún).
     *
     * @param mascotaId     ID de la mascota
     * @param veterinarioId ID del veterinario asignado
     * @param fechaHora     fecha y hora programada
     * @param motivo        motivo de la consulta
     */
    public Cita(int mascotaId, int veterinarioId,
                LocalDateTime fechaHora, String motivo) {
        this.mascotaId     = mascotaId;
        this.veterinarioId = veterinarioId;
        this.fechaHora     = fechaHora;
        this.motivo        = motivo;
        this.estado        = EstadoCita.PENDIENTE;
    }

    /**
     * Constructor de conveniencia usado por las vistas al capturar datos del usuario.
     * El {@code mascotaId} se asigna después por {@link com.huellassanas.service.ClinicaService}.
     *
     * @param mascotaIdPlaceholder pasar 0; el ID real se asigna en el servicio
     * @param fechaHora            fecha y hora seleccionada por el usuario
     * @param motivo               motivo de la consulta
     * @param veterinarioId        ID del veterinario seleccionado
     */
    public Cita(int mascotaIdPlaceholder, LocalDateTime fechaHora,
                String motivo, int veterinarioId) {
        this.mascotaId     = mascotaIdPlaceholder;
        this.veterinarioId = veterinarioId;
        this.fechaHora     = fechaHora;
        this.motivo        = motivo;
        this.estado        = EstadoCita.PENDIENTE;
    }

    /**
     * Constructor de persistencia completo (carga desde DB).
     *
     * @param id            PK en base de datos
     * @param mascotaId     ID de la mascota
     * @param veterinarioId ID del veterinario
     * @param fechaHora     fecha y hora de la cita
     * @param motivo        motivo de la consulta
     * @param diagnostico   diagnóstico emitido
     * @param estado        estado actual
     */
    public Cita(int id, int mascotaId, int veterinarioId,
                LocalDateTime fechaHora, String motivo,
                String diagnostico, EstadoCita estado) {
        this.id            = id;
        this.mascotaId     = mascotaId;
        this.veterinarioId = veterinarioId;
        this.fechaHora     = fechaHora;
        this.motivo        = motivo;
        this.diagnostico   = diagnostico;
        this.estado        = estado;
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getMascotaId()                   { return mascotaId; }
    public void setMascotaId(int m)             { this.mascotaId = m; }

    public int getVeterinarioId()               { return veterinarioId; }
    public void setVeterinarioId(int v)         { this.veterinarioId = v; }

    public LocalDateTime getFechaHora()         { return fechaHora; }
    public void setFechaHora(LocalDateTime f)   { this.fechaHora = f; }

    public String getMotivo()                   { return motivo; }
    public void setMotivo(String m)             { this.motivo = m; }

    public String getDiagnostico()              { return diagnostico; }
    public void setDiagnostico(String d)        { this.diagnostico = d; }

    public EstadoCita getEstado()               { return estado; }
    public void setEstado(EstadoCita e)         { this.estado = e; }

    @Override
    public String toString() {
        return String.format(
                "Cita{id=%d, mascotaId=%d, vetId=%d, fecha=%s, estado=%s}",
                id, mascotaId, veterinarioId, fechaHora, estado);
    }
}
