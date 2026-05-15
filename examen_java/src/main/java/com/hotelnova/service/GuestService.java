package com.hotelnova.service;

import com.hotelnova.dao.GuestDAO;
import com.hotelnova.database.DatabaseConnection;
import com.hotelnova.exception.GuestException;
import com.hotelnova.model.Guest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GuestService {
    @FunctionalInterface
    interface ConnectionProvider {
        Connection getConnection() throws SQLException;
    }

    private final GuestDAO guestDAO;
    private final ConnectionProvider connectionProvider;

    public GuestService(GuestDAO guestDAO) {
        this(guestDAO, DatabaseConnection::getConnection);
    }

    GuestService(GuestDAO guestDAO, ConnectionProvider connectionProvider) {
        this.guestDAO = guestDAO;
        this.connectionProvider = connectionProvider;
    }

    public List<Guest> getAllGuests() throws GuestException {
        try (Connection conn = connectionProvider.getConnection()) {
            return guestDAO.findAll(conn);
        } catch (SQLException e) {
            throw new GuestException("Error listing guests.", e);
        }
    }

    public Guest findByDocument(String documentNumber) throws GuestException {
        try (Connection conn = connectionProvider.getConnection()) {
            return guestDAO.findByDocument(documentNumber, conn);
        } catch (SQLException e) {
            throw new GuestException("Error retrieving the guest.", e);
        }
    }

    public Guest findById(int id) throws GuestException {
        try (Connection conn = connectionProvider.getConnection()) {
            Guest guest = guestDAO.findById(id, conn);
            if (guest == null) {
                throw new GuestException("No guest exists with ID " + id + ".");
            }
            return guest;
        } catch (SQLException e) {
            throw new GuestException("Error retrieving the guest.", e);
        }
    }

    public void createGuest(Guest guest) throws GuestException {
        validateGuest(guest);
        try (Connection conn = connectionProvider.getConnection()) {
            Guest existingGuest = guestDAO.findByDocument(guest.getDocumentNumber(), conn);
            if (existingGuest != null) {
                throw new GuestException("A guest with document " + guest.getDocumentNumber() + " already exists.");
            }
            guestDAO.save(guest, conn);
        } catch (SQLException e) {
            throw new GuestException("Error registering the guest.", e);
        }
    }

    public void updateGuest(Guest guest) throws GuestException {
        validateGuest(guest);
        try (Connection conn = connectionProvider.getConnection()) {
            Guest existingGuest = guestDAO.findById(guest.getId(), conn);
            if (existingGuest == null) {
                throw new GuestException("No guest exists with ID " + guest.getId() + ".");
            }

            Guest guestWithSameDocument = guestDAO.findByDocument(guest.getDocumentNumber(), conn);
            if (guestWithSameDocument != null && guestWithSameDocument.getId() != guest.getId()) {
                throw new GuestException("A guest with document " + guest.getDocumentNumber() + " already exists.");
            }

            guestDAO.update(guest, conn);
        } catch (SQLException e) {
            throw new GuestException("Error updating the guest.", e);
        }
    }

    public Guest toggleGuestActive(int guestId) throws GuestException {
        try (Connection conn = connectionProvider.getConnection()) {
            Guest guest = guestDAO.findById(guestId, conn);
            if (guest == null) {
                throw new GuestException("No guest exists with ID " + guestId + ".");
            }
            guest.setActive(!guest.isActive());
            guestDAO.update(guest, conn);
            return guest;
        } catch (SQLException e) {
            throw new GuestException("Error changing the guest status.", e);
        }
    }

    private void validateGuest(Guest guest) throws GuestException {
        if (guest == null) {
            throw new GuestException("The guest is required.");
        }
        if (guest.getFirstName() == null || guest.getFirstName().isBlank()) {
            throw new GuestException("The guest first name is required.");
        }
        if (guest.getLastName() == null || guest.getLastName().isBlank()) {
            throw new GuestException("The guest last name is required.");
        }
        if (guest.getDocumentNumber() == null || guest.getDocumentNumber().isBlank()) {
            throw new GuestException("The guest document number is required.");
        }
    }
}
