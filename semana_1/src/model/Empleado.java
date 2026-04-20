package model;

public class Empleado {

    private byte nivelRiesgo;
    private short diasVacacionesPendientes;
    private int idEmpleado;
    private long numeroContrato;
    private float bonoMensual;
    private double salarioBase;
    private char categoria;
    private boolean esActivo;
    private String nombre;

    public Empleado(byte nivelRiesgo,
                    short diasVacacionesPendientes,
                    int idEmpleado,
                    long numeroContrato,
                    float bonoMensual,
                    double salarioBase,
                    char categoria,
                    boolean esActivo,
                    String nombre) {
        this.nivelRiesgo = nivelRiesgo;
        this.diasVacacionesPendientes = diasVacacionesPendientes;
        this.idEmpleado = idEmpleado;
        this.numeroContrato = numeroContrato;
        this.bonoMensual = bonoMensual;
        this.salarioBase = salarioBase;
        this.categoria = categoria;
        this.esActivo = esActivo;
        this.nombre = nombre;
    }

    public double calcularSalarioFinal() {
        // Se resuelve asi: parentesis, luego multiplicaciones, y al final suma/resta.
        return (salarioBase + (bonoMensual * 1.10)) - (salarioBase * 0.05);
    }

    public boolean tieneIdPar() {
        return idEmpleado % 2 == 0;
    }

    public boolean validarElegibilidad(int puntajeTest, int edad, int idSede) {
        // Precedencia aplicada: ! primero, despues &&, y por ultimo ||.
        return (puntajeTest > 85 && edad < 30) || (idSede == 1 && !esActivo);
    }

    public void sumarBono(float extra) {
        bonoMensual += extra;
    }

    public String resumenRapido() {
        return String.format(
                "Empleado %s | categoria %s | contrato %d | activo %s | riesgo %d | vacaciones %d",
                nombre, categoria, numeroContrato, esActivo, nivelRiesgo, diasVacacionesPendientes
        );
    }
}
