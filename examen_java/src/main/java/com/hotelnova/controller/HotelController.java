package com.hotelnova.controller;

import com.hotelnova.dao.GuestDAO;
import com.hotelnova.dao.ReservationDAO;
import com.hotelnova.dao.RoomDAO;
import com.hotelnova.dao.UserDAO;
import com.hotelnova.dao.impl.GuestDAOImpl;
import com.hotelnova.dao.impl.ReservationDAOImpl;
import com.hotelnova.dao.impl.RoomDAOImpl;
import com.hotelnova.dao.impl.UserDAOImpl;
import com.hotelnova.exception.AuthenticationException;
import com.hotelnova.exception.GuestException;
import com.hotelnova.exception.InvalidReservationException;
import com.hotelnova.exception.RoomException;
import com.hotelnova.exception.UserException;
import com.hotelnova.model.Guest;
import com.hotelnova.model.Reservation;
import com.hotelnova.model.Room;
import com.hotelnova.model.RoomStatus;
import com.hotelnova.model.User;
import com.hotelnova.service.AuthService;
import com.hotelnova.service.GuestService;
import com.hotelnova.service.ReservationService;
import com.hotelnova.service.RoomService;
import com.hotelnova.service.UserService;
import com.hotelnova.util.CSVExportUtil;

import java.util.List;
import java.util.logging.Logger;

public class HotelController {
    private static final Logger LOGGER = Logger.getLogger(HotelController.class.getName());

    private final AuthService authService;
    private final RoomService roomService;
    private final GuestService guestService;
    private final UserService userService;
    private final ReservationService reservationService;

    public HotelController() {
        UserDAO userDAO = new UserDAOImpl();
        RoomDAO roomDAO = new RoomDAOImpl();
        GuestDAO guestDAO = new GuestDAOImpl();
        ReservationDAO reservationDAO = new ReservationDAOImpl();

        this.authService = new AuthService(userDAO);
        this.roomService = new RoomService(roomDAO);
        this.guestService = new GuestService(guestDAO);
        this.userService = new UserService(userDAO);
        this.reservationService = new ReservationService(reservationDAO, roomDAO, guestDAO);
    }

    public User login(String username, String password) throws AuthenticationException {
        return authService.login(username, password);
    }

    public List<Room> getAllRooms() throws RoomException {
        return roomService.getAllRooms();
    }

    public List<Room> getRoomsByStatus(RoomStatus status) throws RoomException {
        return roomService.getRoomsByStatus(status);
    }

    public List<Room> getRoomsByType(String type) throws RoomException {
        return roomService.getRoomsByType(type);
    }

    public Room getRoomById(int roomId) throws RoomException {
        return roomService.findById(roomId);
    }

    public void saveRoom(Room room) throws RoomException {
        roomService.saveRoom(room);
        LOGGER.info("HTTP Trace: POST /rooms - 201 CREATED");
    }

    public void updateRoom(Room room) throws RoomException {
        roomService.updateRoom(room);
        LOGGER.info("HTTP Trace: PATCH /rooms/" + room.getId() + " - 200 OK");
    }

    public Room toggleRoomActive(int roomId) throws RoomException {
        Room room = roomService.toggleRoomActive(roomId);
        LOGGER.info("HTTP Trace: PATCH /rooms/" + roomId + "/active - 200 OK");
        return room;
    }

    public void deleteRoom(int roomId) throws RoomException {
        roomService.deleteRoom(roomId);
        LOGGER.info("HTTP Trace: DELETE /rooms/" + roomId + " - 200 OK");
    }

    public void registerGuest(Guest guest) throws GuestException {
        guestService.createGuest(guest);
        LOGGER.info("HTTP Trace: POST /guests - 201 CREATED");
    }

    public void updateGuest(Guest guest) throws GuestException {
        guestService.updateGuest(guest);
        LOGGER.info("HTTP Trace: PATCH /guests/" + guest.getId() + " - 200 OK");
    }

    public Guest toggleGuestActive(int guestId) throws GuestException {
        Guest guest = guestService.toggleGuestActive(guestId);
        LOGGER.info("HTTP Trace: PATCH /guests/" + guestId + "/active - 200 OK");
        return guest;
    }

    public Guest findGuestByDocument(String document) throws GuestException {
        return guestService.findByDocument(document);
    }

    public Guest findGuestById(int guestId) throws GuestException {
        return guestService.findById(guestId);
    }

    public List<Guest> getAllGuests() throws GuestException {
        return guestService.getAllGuests();
    }

    public void registerUser(User user, String plainPassword) throws UserException {
        userService.createUser(user, plainPassword);
        LOGGER.info("HTTP Trace: POST /users - 201 CREATED");
    }

    public void updateUser(User user, String plainPassword) throws UserException {
        userService.updateUser(user, plainPassword);
        LOGGER.info("HTTP Trace: PATCH /users/" + user.getId() + " - 200 OK");
    }

    public User toggleUserActive(int userId) throws UserException {
        User user = userService.toggleUserActive(userId);
        LOGGER.info("HTTP Trace: PATCH /users/" + userId + "/active - 200 OK");
        return user;
    }

    public void deleteUser(int userId) throws UserException {
        userService.deleteUser(userId);
        LOGGER.info("HTTP Trace: DELETE /users/" + userId + " - 200 OK");
    }

    public User findUserById(int userId) throws UserException {
        return userService.findById(userId);
    }

    public List<User> getAllUsers() throws UserException {
        return userService.getAllUsers();
    }

    public void checkIn(Reservation reservation) throws Exception {
        reservationService.processCheckIn(reservation);
    }

    public void checkOut(int reservationId) throws Exception {
        reservationService.processCheckOut(reservationId);
    }

    public List<Reservation> getActiveReservations() throws InvalidReservationException {
        return reservationService.getActiveReservations();
    }

    public void exportDataToCSV() throws RoomException, InvalidReservationException {
        List<Room> rooms = roomService.getAllRooms();
        List<Reservation> activeReservations = reservationService.getActiveReservations();

        CSVExportUtil.exportRooms(rooms);
        CSVExportUtil.exportRooms(rooms, CSVExportUtil.LEGACY_ROOMS_EXPORT_FILE);
        CSVExportUtil.exportActiveReservations(activeReservations);
        CSVExportUtil.exportActiveReservations(activeReservations, CSVExportUtil.LEGACY_ACTIVE_RESERVATIONS_EXPORT_FILE);
        LOGGER.info("HTTP Trace: GET /exports/csv - 200 OK");
    }
}
