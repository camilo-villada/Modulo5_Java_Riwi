package com.huellassanas.model;

import java.time.LocalDateTime;

/**
 * Representa un registro en el historial clínico unificado de un paciente (mascota).
 */
public class HistorialClinico {

    private int id;
    private int mascotaId;
    private LocalDateTime fechaHora;
    private String diagnostico;
    private String tratamiento;

    // ─── Constructores ────────────────────────────────────────────────────────

    public HistorialClinico() {}

    public HistorialClinico(int id, int mascotaId, LocalDateTime fechaHora, String diagnostico, String tratamiento) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fechaHora = fechaHora;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
    }

    public HistorialClinico(int mascotaId, LocalDateTime fechaHora, String diagnostico, String tratamiento) {
        this.mascotaId = mascotaId;
        this.fechaHora = fechaHora;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
    }

    // ─── Getters y Setters ────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMascotaId() { return mascotaId; }
    public void setMascotaId(int mascotaId) { this.mascotaId = mascotaId; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    @Override
    public String toString() {
        return "HistorialClinico{" +
                "id=" + id +
                ", mascotaId=" + mascotaId +
                ", fechaHora=" + fechaHora +
                ", diagnostico='" + diagnostico + '\'' +
                ", tratamiento='" + tratamiento + '\'' +
                '}';
    }
}
