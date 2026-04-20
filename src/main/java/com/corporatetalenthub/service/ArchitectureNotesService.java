package com.corporatetalenthub.service;

import java.util.List;

public class ArchitectureNotesService {

    public List<String> getSummaryLines() {
        return List.of(
                "Architecture notes for the exercise",
                "Java 8 vs Java 17/21: less repetitive code and more language-level tools.",
                "The JVM runs bytecode and the GC recovers memory from unreachable objects."
        );
    }
}
