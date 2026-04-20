package app;

public class NotasArquitectura {

    public void mostrarResumen() {
        System.out.println("Notas de arquitectura del ejercicio");

        // En Java 8, para un modelo simple se escribian constructor, getters y toString a mano.
        // En Java 17/21, con records y text blocks, el codigo queda mas directo para casos de datos.
        System.out.println("Java 8 vs Java 17/21: menos codigo repetitivo y mas herramientas del lenguaje.");

        // La JVM convierte bytecode en ejecucion real sobre cualquier SO con maquina virtual.
        // El GC libera objetos sin referencia para evitar que la memoria crezca sin control.
        System.out.println("La JVM ejecuta el bytecode y el GC recupera memoria de objetos no alcanzables.");
    }
}
