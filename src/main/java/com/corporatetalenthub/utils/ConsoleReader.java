package com.corporatetalenthub.utils;

import java.util.Scanner;

public class ConsoleReader {

    private final Scanner scanner;

    public ConsoleReader() {
        this.scanner = new Scanner(System.in);
    }

    public int readInt(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid integer number.");
            }
        }
    }

    public double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid decimal number.");
            }
        }
    }

    public String readNonBlank(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }
}
