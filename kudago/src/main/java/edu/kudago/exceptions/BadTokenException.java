package edu.kudago.exceptions;

import org.springframework.http.HttpStatus;

public class BadTokenException extends BaseException {
    public BadTokenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
