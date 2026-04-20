package config;

import java.util.List;
import java.util.Map;

public class LocationsConfig {

    // List.of and Map.of create immutable collections:
    // they are safer for configuration because they avoid accidental runtime changes.
    // Key difference: these collections do not allow .add(), .remove(), or .put().
    public static final List<String> TECHNOLOGIES = List.of(
            "Java",
            "Spring Boot",
            "SQL",
            "React"
    );

    public static final Map<String, String> LOCATIONS = Map.of(
            "BOG", "Bogota Office",
            "MED", "Medellin Office",
            "CAL", "Cali Office",
            "BAR", "Barranquilla Office"
    );

    private LocationsConfig() {
    }
}
