package com.ejemplo;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        try{
            do {
                
                System.out.println("\n=== Calculadora ===");
                System.out.println("1. Suma");
                System.out.println("2. Resta");
                System.out.println("3. Multiplicación");
                System.out.println("4. División");
                System.out.println("5. Salir");
                opcion = sc.nextInt();

                switch (opcion) {
                    case 1:
                        Sumar(sc);
                        break;
                    case 2:
                        restar(sc);
                        break;
                    case 3:
                        Multiplicación(sc);
                        break;

                    case 4:
                        division(sc);
                        break;

                    case 5:
                        System.out.println("saliendo...");
                        break;


                    default:
                        break;
                }

            } while (opcion != 5);

        }catch(Exception e){
            System.out.println("error");

        }

        sc.close();
    }


    public static void Sumar(Scanner sc){
        try{
            System.out.print("Ingrese el primer numero: ");
            int n1 = sc.nextInt();

            System.out.print("Ingrese otro numero: ");
            int n2 = sc.nextInt();

            int resultado = n1 + n2;

            System.out.println("Resultado: " + resultado);

        }catch(Exception e){
            System.out.println("Error " + e.getMessage());
            sc.nextLine();
        }

    }


    public static void restar(Scanner sc){
        try{
            System.out.print("Ingrese un numero: ");
            int n1 = sc.nextInt();

            System.out.print("Ingrese otro numero: ");
            int n2 = sc.nextInt();

            int resultado = n1 - n2;

            System.out.println("resultado: "+ resultado);
        }catch(Exception e){
            System.out.println("Error " + e.getMessage());
            sc.nextLine();
        }
    }


    public static void Multiplicación(Scanner sc){
        try {
            System.out.print("Ingrese un numero: ");
            int n1 = sc.nextInt();

            System.out.print("Ingrese otro numero: ");
            int n2 = sc.nextInt();

            int resultado = n1 * n2;

            System.out.println("resultado: "+ resultado);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            sc.nextLine();
        }
    }

    public static void division(Scanner sc){

        try {
            System.out.print("Ingrese un numero: ");
            int n1 = sc.nextInt();

            System.out.print("Ingrese otro numero: ");
            int n2 = sc.nextInt();
            
            int resultado = n1 / n2;

            System.out.println("resultado" + resultado);

        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            sc.nextLine();

        }
    }

}