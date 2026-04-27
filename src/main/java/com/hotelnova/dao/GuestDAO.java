package com.hotelnova.dao;

import java.util.List;
import com.hotelnova.model.Guest;

public interface GuestDAO {
    
    void save(Guest guest);
    void update(Guest guest);
    Guest findById(int id);
    Guest findByDocument(String documentNumber);
    List<Guest> findAll();
}
