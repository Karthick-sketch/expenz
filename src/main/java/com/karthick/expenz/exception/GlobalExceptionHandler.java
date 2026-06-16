package com.karthick.expenz.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(
    EmailAlreadyExistsException ex
  ) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(
    InvalidCredentialsException ex
  ) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidToken(
    InvalidTokenException ex
  ) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleUserNotFound(
    UserNotFoundException ex
  ) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleUsernameNotFound(
    UsernameNotFoundException ex
  ) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
    return buildResponse(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "An unexpected error occurred"
    );
  }

  private ResponseEntity<ErrorResponseDTO> buildResponse(
    HttpStatus status,
    String message
  ) {
    ErrorResponseDTO error = ErrorResponseDTO.builder()
      .status(status.value())
      .error(status.getReasonPhrase())
      .message(message)
      .timestamp(LocalDateTime.now())
      .build();
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
    EntityNotFoundException ex
  ) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }
}
