package com.hotelnova.dao;

import java.util.List;

import com.hotelnova.model.User;

public interface UserDAO {
    
    void save(User user);
    void update(User user);
    void delete(int id);
    User findById(int id);
    User findByUsername(String username);
    List<User> findAll();
}
