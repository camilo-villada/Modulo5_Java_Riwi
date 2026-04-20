package app;

import model.Empleado;
import model.EmpresaRecord;

public class Main {

    public static void main(String[] args) {
        String encabezado = """
                ===============================
                Corporate Talent Hub - Semana 1
                ===============================
                """;
        System.out.println(encabezado);

        NotasArquitectura notas = new NotasArquitectura();
        notas.mostrarResumen();

        Empleado camila = new Empleado(
                (byte) 3,
                (short) 12,
                1024,
                8900123L,
                250000.50f,
                3200000.00,
                'A',
                true,
                "Camila"
        );

        camila.sumarBono(15000.0f);

        System.out.println(camila.resumenRapido());
        System.out.println("Salario final: " + camila.calcularSalarioFinal());
        System.out.println("ID par: " + camila.tieneIdPar());
        System.out.println("Elegible: " + camila.validarElegibilidad(90, 24, 2));

        EmpresaRecord empresa = new EmpresaRecord("Corporate Talent Hub", "900.123.456-7", 2020);
        System.out.println("Record empresa: " + empresa);

        // En Java 14+, el NPE indica con mas detalle cual referencia fue null.
        // En Java 8 el mensaje era mas corto y tocaba apoyarse mas en el stacktrace.
        String correoCorporativo = null;
        try {
            System.out.println(correoCorporativo.toUpperCase());
        } catch (NullPointerException ex) {
            System.out.println("Caso null capturado: " + ex.getMessage());
        }

        String areaA = new String("Backend");
        String areaB = new String("Backend");

        // == compara si apuntan al mismo objeto en heap, no si el texto coincide.
        System.out.println("Comparacion con == : " + (areaA == areaB));
        System.out.println("Comparacion con equals: " + areaA.equals(areaB));
    }
}
