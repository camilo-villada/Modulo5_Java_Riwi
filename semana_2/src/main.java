import java.util.InputMismatchException;
import java.util.Scanner;

public class main {

    public static void Main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n=== CORPORATE TALENT HUB ===");
            System.out.println("1. Registrar empleado");
            System.out.println("2. Calcular desempeño");
            System.out.println("0. Salir");

            // TASK 1 - Switch legacy Java 8: riesgo de "fall-through" si se olvida el break
            // Java 17/21: switch expression con -> elimina ese riesgo y es más conciso
            try {
                opcion = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: ingrese un número válido.");
                scanner.next();
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    registrarEmpleado();
                    break;
                case 2:
                    procesarDesempeno();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }

        } while (opcion != 0);

        scanner.close();
    }

    // TASK 1 - Switch Expression moderno Java 17/21 con sintaxis ->
    public static String obtenerCategoriaSalarial(double salario) {
        return switch ((int) salario / 1000) {
            case 1, 2 -> "Bajo";
            case 3, 4 -> "Medio";
            case 5, 6 -> "Alto";
            default   -> "Premium";
        };
    }

    // TASK 2 - Scanner en do-while + var (Java 11+) + if/else para validar rangos
    public static void registrarEmpleado() {
        Scanner scanner = new Scanner(System.in);

        try {
            var nombre = ""; // Java 11+: var infiere String | Java 8: String nombre = "";
            var edad   = 0;  // Java 11+: var infiere int    | Java 8: int edad = 0;

            System.out.print("Ingrese nombre: ");
            nombre = scanner.nextLine();

            do {
                System.out.print("Ingrese edad (18-65): ");
                edad = scanner.nextInt();

                if (edad < 18 || edad > 65) {
                    System.out.println("Edad fuera de rango. Debe estar entre 18 y 65.");
                } else {
                    System.out.println("Edad registrada: " + edad);
                }
            } while (edad < 18 || edad > 65);

            System.out.print("Ingrese salario: ");
            var salario = scanner.nextDouble(); // Java 11+: var infiere double | Java 8: double salario = 0;

            if (salario <= 0) {
                System.out.println("El salario debe ser mayor a 0.");
            } else {
                System.out.println("Nombre: " + nombre);
                System.out.println("Categoría salarial: " + obtenerCategoriaSalarial(salario));
            }

        } catch (InputMismatchException e) {
            // TASK 4 - Captura de InputMismatchException al leer datos del Scanner
            System.out.println("Error: tipo de dato incorrecto. Se esperaba un número.");
        }

        scanner.close();
    }

    // TASK 3 - Matriz de desempeño + for anidados + casting + TASK 4 operador ternario
    public static void procesarDesempeno() {

        // Matriz double[][]: 2 empleados, 3 trimestres cada uno
        double[][] calificaciones = {
            {4.5, 3.8, 4.2},
            {3.0, 3.5, 4.0}
        };

        for (int i = 0; i < calificaciones.length; i++) {
            double suma = 0;

            for (int j = 0; j < calificaciones[i].length; j++) {
                suma += calificaciones[i][j];
            }

            double promedio = suma / calificaciones[i].length;

            // Casting explícito double -> int: se pierde la parte decimal (precisión)
            // Ejemplo: 4.16 se convierte en 4, no hay redondeo
            int promedioEntero = (int) promedio;

            System.out.println("\nEmpleado " + (i + 1));
            System.out.println("Promedio real: " + promedio);
            System.out.println("Puntaje simplificado: " + promedioEntero);

            // TASK 4 - Operador ternario para estado de promoción
            String estado = (promedio >= 3.5) ? "Promovido" : "No promovido";
            System.out.println("Estado: " + estado);
        }

        /*
         * TASK 4 - Análisis LTS: Diagnóstico de errores en excepciones
         * Java 8:
         *   Los mensajes de excepción eran genéricos y poco descriptivos.
         *   Ejemplo: NullPointerException sin indicar qué variable era null.
         *
         * Java 17/21:
         *   Se introdujeron "Helpful NullPointerExceptions" (JEP 358, Java 14+),
         *   que indican exactamente qué variable o referencia causó el error.
         *   Ejemplo: "Cannot invoke "String.length()" because "nombre" is null"
         *   Esto reduce el tiempo de diagnóstico y depuración.
         */
    }
}