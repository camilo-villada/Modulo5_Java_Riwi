import java.util.InputMismatchException;
import java.util.Scanner;

public class main {

    public static void Main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n=== CORPORATE TALENT HUB ===");
            System.out.println("1. Register employee");
            System.out.println("2. Calculate performance");
            System.out.println("0. Exit");

            // TASK 1 - Legacy Java 8 switch: risk of "fall-through" if break is forgotten
            // Java 17/21: switch expression with -> removes that risk and is more concise
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Error: enter a valid number.");
                scanner.next();
                option = -1;
            }

            switch (option) {
                case 1:
                    registerEmployee();
                    break;
                case 2:
                    processPerformance();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }

        } while (option != 0);

        scanner.close();
    }

    // TASK 1 - Modern Java 17/21 switch expression with -> syntax
    public static String getSalaryCategory(double salary) {
        return switch ((int) salary / 1000) {
            case 1, 2 -> "Low";
            case 3, 4 -> "Medium";
            case 5, 6 -> "High";
            default   -> "Premium";
        };
    }

    // TASK 2 - Scanner in do-while + var (Java 11+) + if/else to validate ranges
    public static void registerEmployee() {
        Scanner scanner = new Scanner(System.in);

        try {
            var name = ""; // Java 11+: var infers String | Java 8: String name = "";
            var age  = 0;  // Java 11+: var infers int    | Java 8: int age = 0;

            System.out.print("Enter name: ");
            name = scanner.nextLine();

            do {
                System.out.print("Enter age (18-65): ");
                age = scanner.nextInt();

                if (age < 18 || age > 65) {
                    System.out.println("Age out of range. Must be between 18 and 65.");
                } else {
                    System.out.println("Age recorded: " + age);
                }
            } while (age < 18 || age > 65);

            System.out.print("Enter salary: ");
            var salary = scanner.nextDouble(); // Java 11+: var infers double | Java 8: double salary = 0;

            if (salary <= 0) {
                System.out.println("Salary must be greater than 0.");
            } else {
                System.out.println("Name: " + name);
                System.out.println("Salary category: " + getSalaryCategory(salary));
            }

        } catch (InputMismatchException e) {
            // TASK 4 - Catch InputMismatchException when reading Scanner data
            System.out.println("Error: incorrect data type. A number was expected.");
        }

        scanner.close();
    }

    // TASK 3 - Performance matrix + nested for loops + casting + TASK 4 ternary operator
    public static void processPerformance() {

        // double[][] matrix: 2 employees, 3 quarters each
        double[][] ratings = {
            {4.5, 3.8, 4.2},
            {3.0, 3.5, 4.0}
        };

        for (int i = 0; i < ratings.length; i++) {
            double sum = 0;

            for (int j = 0; j < ratings[i].length; j++) {
                sum += ratings[i][j];
            }

            double average = sum / ratings[i].length;

            // Explicit casting double -> int: the decimal part is lost (precision)
            // Example: 4.16 becomes 4, no rounding
            int averageInt = (int) average;

            System.out.println("\nEmployee " + (i + 1));
            System.out.println("Actual average: " + average);
            System.out.println("Simplified score: " + averageInt);

            // TASK 4 - Ternary operator for promotion status
            String status = (average >= 3.5) ? "Promoted" : "Not promoted";
            System.out.println("Status: " + status);
        }

        /*
         * TASK 4 - LTS analysis: Error diagnostics in exceptions
         * Java 8:
         *   Exception messages were generic and not very descriptive.
         *   Example: NullPointerException without indicating which variable was null.
         *
         * Java 17/21:
         *   "Helpful NullPointerExceptions" (JEP 358, Java 14+) were introduced,
         *   which indicate exactly which variable or reference caused the error.
         *   Example: "Cannot invoke "String.length()" because "name" is null"
         *   This reduces diagnostic and debugging time.
         */
    }
}
