package com.corporatetalenthub.utils;

import java.util.List;
import java.util.Map;

public final class LocationsConfig {

    // List.of and Map.of create immutable collections:
    // they are safer for configuration because they avoid accidental runtime changes.
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
