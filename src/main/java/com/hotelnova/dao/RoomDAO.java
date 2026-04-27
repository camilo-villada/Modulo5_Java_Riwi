package com.hotelnova.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;

public interface RoomDAO {
    
    void save(Room room, Connection conn) throws SQLException;
    void update(Room room, Connection conn) throws SQLException;
    void delete(int id, Connection conn) throws SQLException;
    Room findById(int id, Connection conn) throws SQLException;
    Room findByNumber(String roomNumber, Connection conn) throws SQLException;
    List<Room> findAll(Connection conn) throws SQLException;
    List<Room> findByStatus(RoomStatus status, Connection conn) throws SQLException;
    List<Room> findByType(String type, Connection conn) throws SQLException;
}
