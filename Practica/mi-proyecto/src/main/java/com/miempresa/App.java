package com.miempresa;

import java.util.HashMap;
import java.util.Scanner;


public class App 
{
    public static void main( String[] args )
    {
        Scanner leer = new Scanner(System.in);
        var coders = new HashMap<String, int[]>();
        int opcion = 0;
        
        
            do {

                try{    
                    System.out.println("==== RIWI ====");
                    System.out.println("1- Ingresar Coder.");
                    System.out.println("2- Mostrar coders.");
                    System.out.println("3- Salir. ");
                    opcion = leer.nextInt();

                    switch (opcion) {
                        case 1:
                            ingresarCoder(leer, coders);
                            break;
                        case 2:
                            mostrarCoders(coders);
                            break;
                        case 3:
                            System.out.println("Saliendo...");
                            break;
                        default:
                            break;
                        }

                }catch(Exception e){
                System.out.println("Error, intentalo de nuevo. ");
                leer.nextLine();
                }

            } while (opcion != 3);
                    
           

            


        leer.close();

    }


    public static void ingresarCoder(Scanner leer, HashMap<String, int[]> coders){


        leer.nextLine();
        System.out.print("Ingrese el nombre del coder: ");
        String nombre = leer.nextLine();

        System.out.print("Ingrese la edad del coder: ");
        int edad = leer.nextInt();

        System.out.print("Ingrese el promedio del coder: ");
        int promedio = leer.nextInt();

        
        coders.put(nombre, new int[]{edad, promedio});

        leer.nextLine();


        
        
    }


    public static void mostrarCoders(HashMap<String, int[]> coders){
    if (coders.isEmpty()) {
        System.out.println("No hay coders ingresados.");
        return;
    }
    System.out.println("==== LISTA DE CODERS ====");
    for (var entry : coders.entrySet()) {
        String nombre = entry.getKey();
        int[] datos = entry.getValue();
        System.out.println(nombre + " - Edad: " + datos[0] + ", Promedio: " + datos[1]);
    }
}
}
