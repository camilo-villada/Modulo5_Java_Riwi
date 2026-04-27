package com.hotelnova.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    
    private static final Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(ConfigManager.class.getName());

    static {
        LoggingConfig.configure();
        try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                logger.log(Level.SEVERE, "Error: config.properties was not found in src/main/resources.");
            } else {
                properties.load(is);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading application properties.", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String... keys) {
        for (String key : keys) {
            String value = properties.getProperty(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    public static double getDoubleProperty(String key) {
        String value = properties.getProperty(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }

    public static double getDoubleProperty(String... keys) {
        String value = getProperty(keys);
        return value != null ? Double.parseDouble(value) : 0.0;
    }
}
