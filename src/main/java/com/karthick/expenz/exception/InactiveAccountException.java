package com.karthick.expenz.exception;

public class InactiveAccountException extends RuntimeException {

  public InactiveAccountException(String email) {
    super("Inactive account: " + email);
  }
}
