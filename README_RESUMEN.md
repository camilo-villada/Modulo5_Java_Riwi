# Resumen del Proyecto: Sistema de Biblioteca (Java + JDBC + Maven)

## Objetivo
Desarrollar un sistema de gestión de biblioteca por consola usando Java, JDBC y Maven, aplicando buenas prácticas de ingeniería de software y principios SOLID.

## Estado actual
- Proyecto Maven creado y configurado con dependencia MySQL.
- Estructura profesional de carpetas y paquetes:
  - `model`: Clases Libro, Usuario, Prestamo.
  - `database`: Clase de conexión JDBC centralizada.
  - `dao`: Interfaces y clases DAO para acceso a datos (implementando CRUD y consultas con JDBC, usando PreparedStatement y try-with-resources).
- Principios SOLID aplicados:
  - Separación de responsabilidades (cada clase y método tiene un propósito claro).
  - Uso de interfaces para desacoplar lógica y facilitar cambios/pruebas.
- Métodos implementados:
  - registrar libro
  - registrar usuario
  - registrar préstamo
  - listar libros disponibles
  - listar usuarios registrados

## Próximos pasos
- Implementar actualización y eliminación de libros y usuarios.
- Agregar manejo de excepciones más robusto.
- devolver libro


## Buenas prácticas y aprendizajes
- Organización clara del código y carpetas.
- Uso de PreparedStatement para seguridad y eficiencia.
- Manejo seguro de recursos con try-with-resources.
- Flujo profesional de desarrollo y aprendizaje activo.



