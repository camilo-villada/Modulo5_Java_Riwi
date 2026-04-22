package com.biblioteca;

import java.util.Scanner;
import com.biblioteca.service.*;


public class App 
{
    public static void main( String[] args )
    {
        var scanner = new Scanner(System.in);
        int opcion = 0;

        LibroService libroService = new LibroService();
        UsuarioService usuarioService = new UsuarioService();
        PrestamoService prestamoService = new PrestamoService();
     

        do {

            System.out.println("=== Bienvenido a la Biblioteca ===");
            System.out.println("1. Registrar libro");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Realizar préstamo");
            System.out.println("4. Devolver libro");
            System.out.println("5. Listar libros disponibles");
            System.out.println("6. Listar usuarios registrados");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    // Lógica para registrar un libro
                    System.out.print("Ingrese el título del libro: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Ingrese el autor del libro: ");
                    String autor = scanner.nextLine();
                    libroService.registrarLibro(titulo, autor);
                    break;
                case 2:
                    // Lógica para registrar un usuario
                    System.out.print("Ingrese el nombre del usuario: ");
                    String nombre = scanner.nextLine();
                    usuarioService.registrarUsuario(nombre);

                    break;
                case 3:
                    // Lógica para realizar un préstamo
                
                    System.out.print("Ingrese ID del libro: ");
                    int idLibro = Integer.parseInt(scanner.nextLine());

                    System.out.print("Ingrese ID del usuario: ");
                    int idUsuario = Integer.parseInt(scanner.nextLine());

                    prestamoService.registrarPrestamo(idLibro, idUsuario);

                    break;
                case 4:
                    // Lógica para devolver un libro
                    System.out.print("Ingrese ID del préstamo: ");
                    int idPrestamo = Integer.parseInt(scanner.nextLine());
                    prestamoService.devolverLibro(idPrestamo);
                    break;
                case 5:
                    // Lógica para listar libros disponibles
                    libroService.obtenerTodosLosLibros();
                    for (var libro : libroService.obtenerTodosLosLibros()) {
                        System.out.println("ID: " + libro.getIdLibro() + ", Título: " + libro.getTitulo() + ", Autor: " + libro.getAutor());
                    }
                    break;
                case 6:
                    // Lógica para listar usuarios registrados
                    usuarioService.obtenerTodosLosUsuarios();
                    for (var usuario : usuarioService.obtenerTodosLosUsuarios()) {
                        System.out.println("ID: " + usuario.getIdUsuario() + ", Nombre: " + usuario.getNombre());
                    }
                    break;
                case 7:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        } while (opcion != 7);


        
        scanner.close();
    }


}
