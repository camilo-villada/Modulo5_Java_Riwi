package com.hotelnova.service;

import com.hotelnova.dao.UserDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.exception.UserException;
import com.hotelnova.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    @FunctionalInterface
    interface ConnectionProvider {
        Connection getConnection() throws SQLException;
    }

    private final UserDAO userDAO;
    private final ConnectionProvider connectionProvider;

    public UserService(UserDAO userDAO) {
        this(userDAO, DatabaseConnection::getConnection);
    }

    UserService(UserDAO userDAO, ConnectionProvider connectionProvider) {
        this.userDAO = userDAO;
        this.connectionProvider = connectionProvider;
    }

    public List<User> getAllUsers() throws UserException {
        try (Connection conn = connectionProvider.getConnection()) {
            return userDAO.findAll(conn);
        } catch (SQLException e) {
            throw new UserException("Error listing users.", e);
        }
    }

    public User findById(int id) throws UserException {
        try (Connection conn = connectionProvider.getConnection()) {
            User user = userDAO.findById(id, conn);
            if (user == null) {
                throw new UserException("No user exists with ID " + id + ".");
            }
            return user;
        } catch (SQLException e) {
            throw new UserException("Error retrieving the user.", e);
        }
    }

    public void createUser(User user, String plainPassword) throws UserException {
        validateUser(user, plainPassword, true);
        try (Connection conn = connectionProvider.getConnection()) {
            User existingUser = userDAO.findByUsername(user.getUsername(), conn);
            if (existingUser != null) {
                throw new UserException("A user with username " + user.getUsername() + " already exists.");
            }
            user.setPassword(hashPassword(plainPassword));
            userDAO.save(user, conn);
        } catch (SQLException e) {
            throw new UserException("Error registering the user.", e);
        }
    }

    public void updateUser(User user, String plainPassword) throws UserException {
        validateUser(user, plainPassword, false);
        try (Connection conn = connectionProvider.getConnection()) {
            User existingUser = userDAO.findById(user.getId(), conn);
            if (existingUser == null) {
                throw new UserException("No user exists with ID " + user.getId() + ".");
            }

            User userWithSameUsername = userDAO.findByUsername(user.getUsername(), conn);
            if (userWithSameUsername != null && userWithSameUsername.getId() != user.getId()) {
                throw new UserException("A user with username " + user.getUsername() + " already exists.");
            }

            if (plainPassword == null || plainPassword.isBlank()) {
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(hashPassword(plainPassword));
            }

            userDAO.update(user, conn);
        } catch (SQLException e) {
            throw new UserException("Error updating the user.", e);
        }
    }

    public User toggleUserActive(int userId) throws UserException {
        try (Connection conn = connectionProvider.getConnection()) {
            User user = userDAO.findById(userId, conn);
            if (user == null) {
                throw new UserException("No user exists with ID " + userId + ".");
            }
            user.setActive(!user.isActive());
            userDAO.update(user, conn);
            return user;
        } catch (SQLException e) {
            throw new UserException("Error changing the user status.", e);
        }
    }

    public void deleteUser(int userId) throws UserException {
        try (Connection conn = connectionProvider.getConnection()) {
            User user = userDAO.findById(userId, conn);
            if (user == null) {
                throw new UserException("No user exists with ID " + userId + ".");
            }
            userDAO.delete(userId, conn);
        } catch (SQLException e) {
            throw new UserException("The user could not be deleted. Check whether the user has associated reservations.", e);
        }
    }

    public String hashPassword(String plainPassword) throws UserException {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new UserException("The password is required.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private void validateUser(User user, String plainPassword, boolean creating) throws UserException {
        if (user == null) {
            throw new UserException("The user is required.");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new UserException("The username is required.");
        }
        if (user.getRole() == null) {
            throw new UserException("The user role is required.");
        }
        if (creating && (plainPassword == null || plainPassword.isBlank())) {
            throw new UserException("The password is required.");
        }
    }
}
