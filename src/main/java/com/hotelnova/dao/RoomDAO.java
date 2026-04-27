package com.hotelnova.dao;

import java.util.List;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;

public interface RoomDAO {
    
    void save(Room room);
    void update(Room room);
    Room findById(int id);
    Room findByNumber(String Roomnumber);
    List<Room> findAll();
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByType(String type);
}
