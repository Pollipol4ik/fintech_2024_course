package edu.cbr.exceptions;

import org.springframework.http.HttpStatus;

public class CurrencyDoesntExistException extends BaseException {
    public CurrencyDoesntExistException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}