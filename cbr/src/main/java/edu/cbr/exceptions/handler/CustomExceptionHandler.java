package edu.cbr.exceptions.handler;

import edu.cbr.dto.SuccessResponseDto;
import edu.cbr.exceptions.BaseException;
import edu.cbr.exceptions.CurrencyServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<SuccessResponseDto> baseExceptionHandler(BaseException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new SuccessResponseDto(false, ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SuccessResponseDto methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            return new SuccessResponseDto(
                    false,
                    ex.getStatusCode() + ": " + ex.getBody().getDetail()
            );
        }
        return new SuccessResponseDto(
                false,
                ex.getStatusCode() + ": " + fieldError.getField() + " - " + fieldError.getDefaultMessage()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public SuccessResponseDto handlerMethodValidationException(HandlerMethodValidationException ex) {
        var detailMessageArguments = ex.getDetailMessageArguments();
        if (detailMessageArguments == null || detailMessageArguments.length == 0) {
            return new SuccessResponseDto(
                    false,
                    ex.getMessage()
            );
        }
        return new SuccessResponseDto(
                false,
                ex.getStatusCode() + ": " + detailMessageArguments[0]
        );
    }

    @ExceptionHandler(CurrencyServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(CurrencyServiceUnavailableException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", 503);
        errorResponse.put("message", "Currency service is unavailable");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", "3600");
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.SERVICE_UNAVAILABLE);
    }


}
