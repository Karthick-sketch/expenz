package com.karthick.expenz.exception;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String errorMessage) {
    super(errorMessage);
  }

  public EntityNotFoundException(Long id, Class<?> entity) {
    super(
      String.format(
        "The %s with id '%s' does not exist in our records",
        entity.getSimpleName().toLowerCase(),
        id
      )
    );
  }
}
