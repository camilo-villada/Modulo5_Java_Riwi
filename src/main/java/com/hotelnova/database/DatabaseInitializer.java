package com.hotelnova.database;

import com.hotelnova.util.ConfigManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseInitializer {
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());
    private static final String ADMIN_SEED_SQL =
            "INSERT INTO users (username, password, role, is_active) " +
            "VALUES ('admin', '$2a$10$z7B/hVqeNDUXUshLtoP3weUybnvFKgtxSV6xSGnIu07F0WUYzf1Yy', 'ADMIN', true) " +
            "ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), is_active = VALUES(is_active)";
    private static final String QA_ADMIN_SEED_SQL =
            "INSERT INTO users (username, password, role, is_active) " +
            "VALUES ('qa_admin', '$2a$10$0YJDFp6k.NTjsrTL05MmxO3eIZ3XkXkuSrEoIayAvVLJcYlrZPOWm', 'ADMIN', true) " +
            "ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), is_active = VALUES(is_active)";
    private static final String QA_RECEPTIONIST_SEED_SQL =
            "INSERT INTO users (username, password, role, is_active) " +
            "VALUES ('qa_recep', '$2a$10$O54qP5XXFTEHhwBWJ7O9eegjyIwBMzExzJmUnZBHjo6mP885KDoK6', 'RECEPTIONIST', true) " +
            "ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role), is_active = VALUES(is_active)";

    public static void initialize() {
        String configuredUrl = ConfigManager.getProperty("db.url");
        String urlBase = extractBaseUrl(configuredUrl);
        String user = ConfigManager.getProperty("db.user");
        String pass = ConfigManager.getProperty("db.password");
        String dbName = extractDatabaseName(configuredUrl);

        try (Connection conn = DriverManager.getConnection(urlBase, user, pass);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            logger.info("HTTP Trace: POST /database/initialize - 200 OK");

            stmt.executeUpdate("USE " + dbName);

            String sqlPath = "hotel_nova_db_ddl.sql";
            String sqlContent = Files.readString(Paths.get(sqlPath));

            String[] queries = sqlContent.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    stmt.executeUpdate(query);
                }
            }

            stmt.executeUpdate(ADMIN_SEED_SQL);
            stmt.executeUpdate(QA_ADMIN_SEED_SQL);
            stmt.executeUpdate(QA_RECEPTIONIST_SEED_SQL);

            logger.info("HTTP Trace: POST /database/initialize - 200 OK");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "HTTP Trace: POST /database/initialize - 500 INTERNAL SERVER ERROR", e);
        }
    }

    private static String extractBaseUrl(String configuredUrl) {
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return "jdbc:mysql://localhost:3306/";
        }
        int lastSlash = configuredUrl.lastIndexOf('/');
        if (lastSlash < 0) {
            return configuredUrl;
        }
        return configuredUrl.substring(0, lastSlash + 1);
    }

    private static String extractDatabaseName(String configuredUrl) {
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return "hotel_nova_db";
        }
        int lastSlash = configuredUrl.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == configuredUrl.length() - 1) {
            return "hotel_nova_db";
        }
        return configuredUrl.substring(lastSlash + 1);
    }
}
