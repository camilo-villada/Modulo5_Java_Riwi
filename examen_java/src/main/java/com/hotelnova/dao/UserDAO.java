package com.hotelnova.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.hotelnova.model.User;

public interface UserDAO {
    
    void save(User user, Connection conn) throws SQLException;
    void update(User user, Connection conn) throws SQLException;
    void delete(int id, Connection conn) throws SQLException;
    User findById(int id, Connection conn) throws SQLException;
    User findByUsername(String username, Connection conn) throws SQLException;
    List<User> findAll(Connection conn) throws SQLException;
}
