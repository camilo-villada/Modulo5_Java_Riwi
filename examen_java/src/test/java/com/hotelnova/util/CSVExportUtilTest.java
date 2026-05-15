package com.hotelnova.util;

import com.hotelnova.model.Reservation;
import com.hotelnova.model.ReservationStatus;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVExportUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExportRoomsCsv() throws Exception {
        Room room = new Room();
        room.setId(1);
        room.setRoomNumber("A-101");
        room.setType("SUITE");
        room.setCapacity(4);
        room.setPricePerNight(new BigDecimal("220.00"));
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);

        Path output = tempDir.resolve("rooms.csv");
        CSVExportUtil.exportRooms(List.of(room), output.toString());

        String content = Files.readString(output);
        assertTrue(content.contains("id,room_number,type,capacity,price_per_night,status,is_active"));
        assertTrue(content.contains("1,A-101,SUITE,4,220.00,AVAILABLE,true"));
    }

    @Test
    void shouldExportActiveReservationsCsv() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(9);
        reservation.setGuestId(5);
        reservation.setRoomId(3);
        reservation.setUserId(1);
        reservation.setCheckInDate(LocalDateTime.of(2026, 8, 10, 15, 0));
        reservation.setCheckOutDate(LocalDateTime.of(2026, 8, 12, 12, 0));
        reservation.setTotalCost(new BigDecimal("261.80"));
        reservation.setStatus(ReservationStatus.ACTIVE);

        Path output = tempDir.resolve("active_reservations.csv");
        CSVExportUtil.exportActiveReservations(List.of(reservation), output.toString());

        String content = Files.readString(output);
        assertTrue(content.contains("id,guest_id,room_id,user_id,check_in_date,check_out_date,total_cost,status"));
        assertTrue(content.contains("9,5,3,1,2026-08-10T15:00,2026-08-12T12:00,261.80,ACTIVE"));
    }
}
