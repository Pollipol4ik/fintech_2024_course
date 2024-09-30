package edu.kudago.exceptions.handler;


import edu.kudago.dto.SuccessResponseDto;
import edu.kudago.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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

}
