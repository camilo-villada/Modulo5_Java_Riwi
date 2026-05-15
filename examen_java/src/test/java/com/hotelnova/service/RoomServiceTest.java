package com.hotelnova.service;

import com.hotelnova.dao.RoomDAO;
import com.hotelnova.exception.RoomException;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomDAO roomDAO;

    private RoomService roomService;
    private Connection conn;

    @BeforeEach
    void setUp() {
        conn = mock(Connection.class);
        roomService = new RoomService(roomDAO, () -> conn);
    }

    @Test
    void shouldRejectDuplicatedRoomNumberOnRegister() throws Exception {
        Room room = new Room();
        room.setRoomNumber("101");
        room.setType("SINGLE");
        room.setCapacity(2);
        room.setPricePerNight(new BigDecimal("120.00"));
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);

        Room existingRoom = new Room();
        existingRoom.setId(5);
        existingRoom.setRoomNumber("101");

        when(roomDAO.findByNumber("101", conn)).thenReturn(existingRoom);

        RoomException exception = assertThrows(RoomException.class, () -> roomService.saveRoom(room));

        assertTrue(exception.getMessage().contains("A room with number"));
        verify(roomDAO, never()).save(room, conn);
    }
}
