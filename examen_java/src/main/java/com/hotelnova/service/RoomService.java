package com.hotelnova.service;

import com.hotelnova.dao.RoomDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.exception.RoomException;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoomService {
    @FunctionalInterface
    interface ConnectionProvider {
        Connection getConnection() throws SQLException;
    }

    private final RoomDAO roomDAO;
    private final ConnectionProvider connectionProvider;

    public RoomService(RoomDAO roomDAO) {
        this(roomDAO, DatabaseConnection::getConnection);
    }

    RoomService(RoomDAO roomDAO, ConnectionProvider connectionProvider) {
        this.roomDAO = roomDAO;
        this.connectionProvider = connectionProvider;
    }

    public List<Room> getAllRooms() throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            return roomDAO.findAll(conn);
        } catch (SQLException e) {
            throw new RoomException("Error listing rooms.", e);
        }
    }

    public List<Room> getRoomsByStatus(RoomStatus status) throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            return roomDAO.findByStatus(status, conn);
        } catch (SQLException e) {
            throw new RoomException("Error filtering rooms by status.", e);
        }
    }

    public List<Room> getRoomsByType(String type) throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            return roomDAO.findByType(type, conn);
        } catch (SQLException e) {
            throw new RoomException("Error filtering rooms by type.", e);
        }
    }

    public Room findById(int id) throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            Room room = roomDAO.findById(id, conn);
            if (room == null) {
                throw new RoomException("No room exists with ID " + id + ".");
            }
            return room;
        } catch (SQLException e) {
            throw new RoomException("Error retrieving the room.", e);
        }
    }

    public void saveRoom(Room room) throws RoomException {
        validateRoom(room);
        try (Connection conn = connectionProvider.getConnection()) {
            Room existingRoom = roomDAO.findByNumber(room.getRoomNumber(), conn);
            if (existingRoom != null) {
                throw new RoomException("A room with number " + room.getRoomNumber() + " already exists.");
            }
            roomDAO.save(room, conn);
        } catch (SQLException e) {
            throw new RoomException("Error registering the room.", e);
        }
    }

    public void updateRoom(Room room) throws RoomException {
        validateRoom(room);
        try (Connection conn = connectionProvider.getConnection()) {
            Room existingRoom = roomDAO.findById(room.getId(), conn);
            if (existingRoom == null) {
                throw new RoomException("No room exists with ID " + room.getId() + ".");
            }

            Room roomWithSameNumber = roomDAO.findByNumber(room.getRoomNumber(), conn);
            if (roomWithSameNumber != null && roomWithSameNumber.getId() != room.getId()) {
                throw new RoomException("A room with number " + room.getRoomNumber() + " already exists.");
            }

            roomDAO.update(room, conn);
        } catch (SQLException e) {
            throw new RoomException("Error updating the room.", e);
        }
    }

    public Room toggleRoomActive(int roomId) throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            Room room = roomDAO.findById(roomId, conn);
            if (room == null) {
                throw new RoomException("No room exists with ID " + roomId + ".");
            }
            room.setActive(!room.isActive());
            roomDAO.update(room, conn);
            return room;
        } catch (SQLException e) {
            throw new RoomException("Error changing the room status.", e);
        }
    }

    public void deleteRoom(int roomId) throws RoomException {
        try (Connection conn = connectionProvider.getConnection()) {
            Room room = roomDAO.findById(roomId, conn);
            if (room == null) {
                throw new RoomException("No room exists with ID " + roomId + ".");
            }
            roomDAO.delete(roomId, conn);
        } catch (SQLException e) {
            throw new RoomException("The room could not be deleted. Check whether it has associated reservations.", e);
        }
    }

    private void validateRoom(Room room) throws RoomException {
        if (room == null) {
            throw new RoomException("The room is required.");
        }
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new RoomException("The room number is required.");
        }
        if (room.getType() == null || room.getType().isBlank()) {
            throw new RoomException("The room type is required.");
        }
        if (room.getCapacity() <= 0) {
            throw new RoomException("Capacity must be greater than zero.");
        }
        if (room.getPricePerNight() == null || room.getPricePerNight().signum() < 0) {
            throw new RoomException("The nightly price must be greater than or equal to zero.");
        }
        if (room.getStatus() == null) {
            throw new RoomException("The room status is required.");
        }
    }
}
