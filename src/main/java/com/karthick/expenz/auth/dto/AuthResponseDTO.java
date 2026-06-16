package com.karthick.expenz.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

  private String accessToken;

  @JsonIgnore
  private String refreshToken;

  private String tokenType;
  private Long expiresIn;
}
