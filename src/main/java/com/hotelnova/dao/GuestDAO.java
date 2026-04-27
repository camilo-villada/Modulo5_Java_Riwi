package com.hotelnova.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import com.hotelnova.model.Guest;

public interface GuestDAO {
    
    void save(Guest guest, Connection conn) throws SQLException;
    void update(Guest guest, Connection conn) throws SQLException;
    Guest findById(int id, Connection conn) throws SQLException;
    Guest findByDocument(String documentNumber, Connection conn) throws SQLException;
    List<Guest> findAll(Connection conn) throws SQLException;
}
