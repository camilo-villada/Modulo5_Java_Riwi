package com.hotelnova.service;

import com.hotelnova.dao.GuestDAO;
import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.dao.RoomDAO;
import com.hotelnova.exception.InvalidReservationException;
import com.hotelnova.exception.RoomNotAvailableException;
import com.hotelnova.model.Guest;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationDAO reservationDAO;

    @Mock
    private RoomDAO roomDAO;

    @Mock
    private GuestDAO guestDAO;

    private ReservationService reservationService;
    private Connection conn;

    @BeforeEach
    void setUp() {
        conn = mock(Connection.class);
        reservationService = new ReservationService(reservationDAO, roomDAO, guestDAO, () -> conn);
    }

    @Test
    void shouldCalculateTotalCostWithVatOnCheckOut() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(10);
        reservation.setRoomId(101);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCheckInDate(LocalDateTime.of(2026, 4, 1, 15, 0));
        reservation.setCheckOutDate(LocalDateTime.of(2026, 4, 4, 12, 0));

        Room room = new Room();
        room.setId(101);
        room.setPricePerNight(new BigDecimal("150.00"));
        room.setStatus(RoomStatus.OCCUPIED);

        when(reservationDAO.findById(10, conn)).thenReturn(reservation);
        when(roomDAO.findById(101, conn)).thenReturn(room);

        reservationService.processCheckOut(10);

        BigDecimal expected = new BigDecimal("150.00")
                .multiply(BigDecimal.valueOf(3))
                .multiply(new BigDecimal("1.19"))
                .setScale(2);

        assertEquals(0, expected.compareTo(reservation.getTotalCost()));
        assertEquals(2, reservation.getTotalCost().scale());
        assertEquals(ReservationStatus.FINISHED, reservation.getStatus());
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        verify(reservationDAO).update(eq(reservation), eq(conn));
        verify(roomDAO).update(eq(room), eq(conn));
    }

    @Test
    void shouldThrowExceptionWhenCheckInIsAfterOrEqualCheckOut() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setGuestId(1);
        reservation.setRoomId(101);
        reservation.setCheckInDate(LocalDateTime.of(2026, 5, 10, 15, 0));
        reservation.setCheckOutDate(LocalDateTime.of(2026, 5, 10, 12, 0));

        InvalidReservationException ex = assertThrows(
                InvalidReservationException.class,
                () -> reservationService.processCheckIn(reservation)
        );

        assertTrue(ex.getMessage().contains("Check-in date must be before check-out date"));

        verify(reservationDAO, never()).isRoomAvailable(any(Integer.class), any(), any(), any(Connection.class));
        verify(reservationDAO, never()).save(any(), any());
    }

    @Test
    void shouldRejectReservationWhenGuestIsInactive() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setGuestId(77);
        reservation.setRoomId(101);
        reservation.setCheckInDate(LocalDateTime.of(2026, 6, 10, 15, 0));
        reservation.setCheckOutDate(LocalDateTime.of(2026, 6, 12, 12, 0));

        Guest inactiveGuest = new Guest();
        inactiveGuest.setId(77);
        inactiveGuest.setActive(false);

        when(guestDAO.findById(77, conn)).thenReturn(inactiveGuest);

        InvalidReservationException ex = assertThrows(
                InvalidReservationException.class,
                () -> reservationService.processCheckIn(reservation)
        );

        assertTrue(ex.getMessage().contains("inactive"));

        verify(reservationDAO, never()).save(any(), any());
    }

    @Test
    void shouldThrowWhenRoomIsNotAvailableForOverlappingDates() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setGuestId(33);
        reservation.setRoomId(101);
        reservation.setCheckInDate(LocalDateTime.of(2026, 7, 1, 15, 0));
        reservation.setCheckOutDate(LocalDateTime.of(2026, 7, 5, 12, 0));

        Guest activeGuest = new Guest();
        activeGuest.setId(33);
        activeGuest.setActive(true);

        Room activeRoom = new Room();
        activeRoom.setId(101);
        activeRoom.setActive(true);

        when(guestDAO.findById(33, conn)).thenReturn(activeGuest);
        when(roomDAO.findById(101, conn)).thenReturn(activeRoom);
        when(reservationDAO.isRoomAvailable(101, reservation.getCheckInDate(), reservation.getCheckOutDate(), conn))
                .thenReturn(false);

        RoomNotAvailableException ex = assertThrows(
                RoomNotAvailableException.class,
                () -> reservationService.processCheckIn(reservation)
        );

        assertTrue(ex.getMessage().contains("not available"));

        verify(reservationDAO, never()).save(any(), any());
    }

    @Test
    void shouldRejectCheckOutWithoutActiveReservation() throws Exception {
        Reservation finishedReservation = new Reservation();
        finishedReservation.setId(40);
        finishedReservation.setStatus(ReservationStatus.FINISHED);

        when(reservationDAO.findById(40, conn)).thenReturn(finishedReservation);

        InvalidReservationException ex = assertThrows(
                InvalidReservationException.class,
                () -> reservationService.processCheckOut(40)
        );

        assertTrue(ex.getMessage().contains("No active reservation exists"));
        verify(roomDAO, never()).findById(any(Integer.class), eq(conn));
    }
}
