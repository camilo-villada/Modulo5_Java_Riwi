package com.hotelnova.exception;

public class AuthenticationException extends HotelNovaException {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
