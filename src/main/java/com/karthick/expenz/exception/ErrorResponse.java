package com.karthick.expenz.exception;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {

  private List<String> error;

  public ErrorResponse(String errorMessage) {
    this.error = List.of(errorMessage);
  }
}
