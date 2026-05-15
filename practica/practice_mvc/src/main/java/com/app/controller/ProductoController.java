package com.app.controller;

import com.app.dao.ProductoDAO;
import com.app.model.entity.Producto;
import com.app.view.View;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductoController {

    private final View view;
    private final ProductoDAO productoDAO;

    public ProductoController(View view, ProductoDAO productoDAO) {
        this.view = view;
        this.productoDAO = productoDAO;
    }

    public void run() {
        String[] menuOptions = {
                "Listar todos", "Buscar por ID", "Crear producto",
                "Actualizar producto", "Eliminar producto",
                "Buscar por nombre", "Filtrar por rango de precio",
                "Volver"
        };

        boolean running = true;
        while (running) {
            view.showMenu(menuOptions, "Gestión de Productos");
            int choice = view.getMenuChoice();

            switch (choice) {
                case 1 -> listarTodos();
                case 2 -> buscarPorId();
                case 3 -> crearProducto();
                case 4 -> actualizarProducto();
                case 5 -> eliminarProducto();
                case 6 -> buscarPorNombre();
                case 7 -> filtrarPorPrecio();
                case 8 -> running = false;
                default -> view.showError("Opción no válida");
            }
        }
    }

    private void listarTodos() {
        List<Producto> productos = productoDAO.findAll();
        if (productos.isEmpty()) {
            view.showMessage("No hay productos registrados.");
        } else {
            view.showList("Productos", formatProductos(productos));
        }
    }

    private void buscarPorId() {
        String input = view.askInput("ID del producto");
        try {
            int id = Integer.parseInt(input);
            Optional<Producto> producto = productoDAO.findById(id);
            if (producto.isPresent()) {
                view.showItem("Producto", formatProducto(producto.get()));
            } else {
                view.showError("No se encontró producto con ID " + id);
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido: " + input);
        }
    }

    private void crearProducto() {
        String nombre = view.askInput("Nombre del producto");
        String precioStr = view.askInput("Precio (ej: 19.99)");

        if (nombre == null || nombre.isBlank() || precioStr == null || precioStr.isBlank()) {
            view.showError("Nombre y precio son requeridos.");
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr.trim());
        } catch (NumberFormatException e) {
            view.showError("Precio inválido: " + precioStr);
            return;
        }
        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            view.showError("El precio no puede ser negativo.");
            return;
        }

        Producto nuevo = new Producto(0, nombre.trim(), precio);
        productoDAO.save(nuevo);
        view.showMessage("Producto creado con ID: " + nuevo.getId());
    }

    private void actualizarProducto() {
        String input = view.askInput("ID del producto a actualizar");
        try {
            int id = Integer.parseInt(input);
            Optional<Producto> opt = productoDAO.findById(id);
            if (opt.isEmpty()) {
                view.showError("No se encontró producto con ID " + id);
                return;
            }

            Producto p = opt.get();
            String nombre = view.askInput("Nuevo nombre [" + p.getNombre() + "]");
            String precioStr = view.askInput("Nuevo precio [" + p.getPrecio() + "]");

            if (nombre != null && !nombre.isBlank()) p.setNombre(nombre.trim());
            if (precioStr != null && !precioStr.isBlank()) {
                try {
                    BigDecimal precio = new BigDecimal(precioStr.trim());
                    if (precio.compareTo(BigDecimal.ZERO) < 0) {
                        view.showError("El precio no puede ser negativo.");
                        return;
                    }
                    p.setPrecio(precio);
                } catch (NumberFormatException e) {
                    view.showError("Precio inválido.");
                    return;
                }
            }

            boolean ok = productoDAO.update(p);
            view.showMessage(ok ? "Producto actualizado." : "No se pudo actualizar.");
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    private void eliminarProducto() {
        String input = view.askInput("ID del producto a eliminar");
        try {
            int id = Integer.parseInt(input);
            if (view.confirm("¿Confirmar eliminación del producto " + id + "?")) {
                boolean ok = productoDAO.deleteById(id);
                view.showMessage(ok ? "Producto eliminado." : "No se encontró el producto.");
            }
        } catch (NumberFormatException e) {
            view.showError("ID inválido.");
        }
    }

    private void buscarPorNombre() {
        String q = view.askInput("Nombre (o parte) a buscar");
        if (q == null || q.isBlank()) {
            view.showError("El nombre es requerido.");
            return;
        }
        List<Producto> productos = productoDAO.findByNombre(q.trim());
        if (productos.isEmpty()) {
            view.showMessage("Sin resultados.");
        } else {
            view.showList("Resultados", formatProductos(productos));
        }
    }

    private void filtrarPorPrecio() {
        String minStr = view.askInput("Precio mínimo");
        String maxStr = view.askInput("Precio máximo");
        try {
            BigDecimal min = new BigDecimal(minStr.trim());
            BigDecimal max = new BigDecimal(maxStr.trim());
            if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
                view.showError("Los precios no pueden ser negativos.");
                return;
            }
            if (min.compareTo(max) > 0) {
                view.showError("El mínimo no puede ser mayor que el máximo.");
                return;
            }

            List<Producto> productos = productoDAO.findByPrecioBetween(min, max);
            if (productos.isEmpty()) {
                view.showMessage("Sin resultados.");
            } else {
                view.showList("Resultados", formatProductos(productos));
            }
        } catch (Exception e) {
            view.showError("Rango inválido.");
        }
    }

    private String formatProductos(List<Producto> productos) {
        if (productos.isEmpty()) return "(Sin productos)";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-30s %-12s%n", "ID", "NOMBRE", "PRECIO"));
        sb.append("─".repeat(52)).append("\n");
        for (Producto p : productos) {
            sb.append(String.format("%-5d %-30s %-12s%n",
                    p.getId(), p.getNombre(), p.getPrecio()));
        }
        return sb.toString();
    }

    private String formatProducto(Producto p) {
        return String.format("ID: %d%nNombre: %s%nPrecio: %s",
                p.getId(), p.getNombre(), p.getPrecio());
    }
}

