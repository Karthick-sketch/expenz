package com.karthick.expenz.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(
    NoSuchElementException e
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      new ErrorResponse(e.getMessage())
    );
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleBadRequestException(
    BadRequestException e
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      new ErrorResponse(e.getMessage())
    );
  }
}
