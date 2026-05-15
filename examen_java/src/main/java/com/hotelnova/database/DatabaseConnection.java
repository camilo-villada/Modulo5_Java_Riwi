package com.hotelnova.database;

import com.hotelnova.util.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

	private DatabaseConnection() {
		// Utility class
	}

	public static Connection getConnection() throws SQLException {
		String url = ConfigManager.getProperty("db.url");
		String user = ConfigManager.getProperty("db.user");
		String password = ConfigManager.getProperty("db.password");

		if (url == null || user == null || password == null) {
			throw new IllegalStateException("Database configuration is missing in config.properties");
		}

		return DriverManager.getConnection(url, user, password);
	}
}
