package com.huellassanas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utilidad de conexión a la base de datos MySQL mediante el patrón Singleton.
 *
 * <p><strong>Responsabilidad única:</strong> proveer una única instancia de
 * {@link Connection} compartida durante el ciclo de vida de la aplicación.
 * Para entornos de producción con alta concurrencia, esta implementación
 * debería reemplazarse por un pool (HikariCP), pero para el alcance de este
 * sistema clínico es suficiente.</p>
 *
 * <p><strong>Configuración:</strong> editar las constantes {@code URL},
 * {@code USER} y {@code PASSWORD} o externalizarlas a un archivo
 * {@code db.properties} en {@code src/main/resources/}.</p>
 *
 * <h3>Uso:</h3>
 * <pre>{@code
 * Connection conn = DatabaseConnection.getInstance().getConnection();
 * }</pre>
 */
public final class DatabaseConnection {

    // ─── Parámetros de conexión ───────────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/huellassanas"
                                         + "?useSSL=false"
                                         + "&serverTimezone=America/Bogota"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";

    // ─── Singleton ────────────────────────────────────────────────────────────
    private static DatabaseConnection instance;
    private Connection connection;

    /** Constructor privado: impide instanciación externa. */
    private DatabaseConnection() {}

    /**
     * Retorna la única instancia del gestor de conexión (lazy initialization).
     *
     * @return instancia única de {@code DatabaseConnection}
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtiene la conexión activa, creando una nueva si no existe o está cerrada.
     *
     * @return objeto {@link Connection} listo para usar
     * @throws SQLException si la conexión falla
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    /**
     * Cierra la conexión y limpia la instancia para liberar recursos.
     * Llamar al apagar la aplicación.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Error al cerrar: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
