package com.github.prasanna.webcrawler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionMapper {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> mapIllegalArgumentException(IllegalArgumentException exception) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST, "Invalid target URL");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> mapGenericException(Exception exception) {
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, try again in a bit");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, HttpStatus status, String title) {
        ErrorResponse errorResponse = new ErrorResponse(title, exception.getMessage());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}