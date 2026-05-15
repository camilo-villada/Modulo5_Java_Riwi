package com.hotelnova.exception;

public class HotelNovaException extends Exception {
    
    public HotelNovaException(String message) {
        super(message);
    }

    public HotelNovaException(String message, Throwable cause) {
        super(message, cause);
    }

}
