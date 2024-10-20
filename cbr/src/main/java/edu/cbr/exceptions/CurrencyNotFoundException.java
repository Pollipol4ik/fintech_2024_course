package edu.cbr.exceptions;

import org.springframework.http.HttpStatus;

public class CurrencyNotFoundException extends BaseException {
    public CurrencyNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
