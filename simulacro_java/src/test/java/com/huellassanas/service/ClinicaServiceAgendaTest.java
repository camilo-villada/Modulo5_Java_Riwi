package com.huellassanas.service;

import com.huellassanas.dao.ClienteDao;
import com.huellassanas.dao.MascotaDao;
import com.huellassanas.dao.impl.CitaDaoImpl;
import com.huellassanas.model.Cita;
import com.huellassanas.model.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicaServiceAgendaTest {

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private MascotaDao mascotaDao;

    @Mock
    private CitaDaoImpl citaDaoImpl;

    @InjectMocks
    private ClinicaService clinicaService;

    private Mascota mascotaPrueba;
    private Cita citaPrueba;
    private final int VETERINARIO_ID = 1;

    @BeforeEach
    void setUp() {
        mascotaPrueba = new Mascota(1, "Rex", Mascota.Especie.PERRO, "Labrador", null, 20.0, 1);
        citaPrueba = new Cita(0, 1, VETERINARIO_ID, LocalDateTime.of(2026, 5, 10, 10, 0), "Control", "", Cita.EstadoCita.PENDIENTE);
    }

    @Test
    void registrarMascotaYCita_SinConflicto_NoLanzaExcepcionParaValidacion() {
        // Arrange: El veterinario no tiene citas ese día
        when(citaDaoImpl.listarPorVeterinarioYFecha(eq(VETERINARIO_ID), any())).thenReturn(new ArrayList<>());
        
        try (org.mockito.MockedStatic<com.huellassanas.util.DatabaseConnection> mockedDb = org.mockito.Mockito.mockStatic(com.huellassanas.util.DatabaseConnection.class)) {
            com.huellassanas.util.DatabaseConnection mockInstance = org.mockito.Mockito.mock(com.huellassanas.util.DatabaseConnection.class);
            java.sql.Connection mockConn = org.mockito.Mockito.mock(java.sql.Connection.class);
            
            mockedDb.when(com.huellassanas.util.DatabaseConnection::getInstance).thenReturn(mockInstance);
            try {
                when(mockInstance.getConnection()).thenReturn(mockConn);
            } catch (Exception e) {}
            
            // Act & Assert
            assertDoesNotThrow(() -> {
                clinicaService.registrarMascotaYCita(mascotaPrueba, citaPrueba);
            });
        }
    }

    @Test
    void registrarMascotaYCita_ConConflictoExacto_LanzaServiceException() {
        // Arrange: El veterinario ya tiene una cita exactamente a la misma hora
        List<Cita> citasExistentes = new ArrayList<>();
        citasExistentes.add(new Cita(2, 2, VETERINARIO_ID, LocalDateTime.of(2026, 5, 10, 10, 0), "Vacuna", "", Cita.EstadoCita.PENDIENTE));
        
        when(citaDaoImpl.listarPorVeterinarioYFecha(eq(VETERINARIO_ID), any())).thenReturn(citasExistentes);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            clinicaService.registrarMascotaYCita(mascotaPrueba, citaPrueba);
        });
        
        assertTrue(exception.getMessage().contains("Conflicto de agenda"));
    }

    @Test
    void registrarMascotaYCita_ConConflictoMargenMenor30Min_LanzaServiceException() {
        // Arrange: El veterinario ya tiene una cita 15 minutos antes
        List<Cita> citasExistentes = new ArrayList<>();
        citasExistentes.add(new Cita(2, 2, VETERINARIO_ID, LocalDateTime.of(2026, 5, 10, 9, 45), "Vacuna", "", Cita.EstadoCita.PENDIENTE));
        
        when(citaDaoImpl.listarPorVeterinarioYFecha(eq(VETERINARIO_ID), any())).thenReturn(citasExistentes);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            clinicaService.registrarMascotaYCita(mascotaPrueba, citaPrueba);
        });
        
        assertTrue(exception.getMessage().contains("Conflicto de agenda"));
    }
}
