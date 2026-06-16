package com.karthick.expenz.exception;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

  private int status;
  private String error;
  private String message;
  private LocalDateTime timestamp;
}
