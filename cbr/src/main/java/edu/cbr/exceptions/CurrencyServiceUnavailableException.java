package edu.cbr.exceptions;

import org.springframework.http.HttpStatus;

public class CurrencyServiceUnavailableException extends BaseException {
    public CurrencyServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
