package com.hotelnova.exception;

public class GuestException extends HotelNovaException {

    public GuestException(String message) {
        super(message);
    }

    public GuestException(String message, Throwable cause) {
        super(message, cause);
    }
}
