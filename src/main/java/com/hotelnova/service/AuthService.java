package com.hotelnova.service;

import com.hotelnova.dao.UserDAO;
import com.hotelnova.exception.AuthenticationException;
import com.hotelnova.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Logger;


public class AuthService {
    
    private final UserDAO userDAO;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO; 
    }

    public User login(String username, String password) throws AuthenticationException {

        User user = userDAO.findByUsername(username);
        
        if(user == null || !user.isActive()){
            throw new AuthenticationException("User not found or inactive");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect password");
            
        }

        logger.info("HTTP Trace: POST /auth/login - user " + username + " logged in.");
        return user;
    }
}



