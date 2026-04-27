package com.huellassanas.service;

import com.huellassanas.dao.ClienteDao;
import com.huellassanas.dao.MascotaDao;
import com.huellassanas.dao.impl.CitaDaoImpl;
import com.huellassanas.model.Cita;
import com.huellassanas.model.Mascota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link ClinicaService} con Mockito.
 *
 * <p>Verifica la lógica de validaciones y el flujo de delegación
 * sin conectarse a la base de datos real.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicaService — Tests de lógica de negocio")
class ClinicaServiceTest {

    @Mock private ClienteDao  clienteDao;
    @Mock private MascotaDao  mascotaDao;
    @Mock private CitaDaoImpl citaDaoImpl;

    private ClinicaService service;

    @BeforeEach
    void setUp() {
        service = new ClinicaService(clienteDao, mascotaDao, citaDaoImpl);
    }

    // ─── Validaciones de registrarMascotaYCita ────────────────────────────────

    @Test
    @DisplayName("lanza ServiceException si mascota es nula")
    void registrarLanzaExcepcionSiMascotaNula() {
        Cita cita = citaValida();
        assertThrows(ServiceException.class,
                () -> service.registrarMascotaYCita(null, cita));
    }

    @Test
    @DisplayName("lanza ServiceException si cita es nula")
    void registrarLanzaExcepcionSiCitaNula() {
        Mascota mascota = mascotaValida();
        assertThrows(ServiceException.class,
                () -> service.registrarMascotaYCita(mascota, null));
    }

    @Test
    @DisplayName("lanza ServiceException si mascota no tiene clienteId válido")
    void registrarLanzaExcepcionSiClienteIdInvalido() {
        Mascota mascota = mascotaValida();
        mascota.setClienteId(0); // inválido
        assertThrows(ServiceException.class,
                () -> service.registrarMascotaYCita(mascota, citaValida()));
    }

    @Test
    @DisplayName("lanza ServiceException si cita no tiene fechaHora")
    void registrarLanzaExcepcionSiFechaHoraNula() {
        Cita cita = citaValida();
        cita.setFechaHora(null);
        assertThrows(ServiceException.class,
                () -> service.registrarMascotaYCita(mascotaValida(), cita));
    }

    @Test
    @DisplayName("lanza ServiceException si cita no tiene veterinarioId válido")
    void registrarLanzaExcepcionSiVeterinarioIdInvalido() {
        Cita cita = citaValida();
        cita.setVeterinarioId(0);
        assertThrows(ServiceException.class,
                () -> service.registrarMascotaYCita(mascotaValida(), cita));
    }

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private Mascota mascotaValida() {
        Mascota m = new Mascota();
        m.setNombre("Firulais");
        m.setEspecie(Mascota.Especie.PERRO);
        m.setClienteId(1);
        return m;
    }

    private Cita citaValida() {
        Cita c = new Cita();
        c.setVeterinarioId(1);
        c.setFechaHora(LocalDateTime.now().plusDays(1));
        c.setEstado(Cita.EstadoCita.PENDIENTE);
        return c;
    }
}
