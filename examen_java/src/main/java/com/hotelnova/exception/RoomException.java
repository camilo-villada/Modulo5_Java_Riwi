package com.hotelnova.exception;

public class RoomException extends HotelNovaException {
    
    public RoomException(String message) {
        super(message);
    }

    public RoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
