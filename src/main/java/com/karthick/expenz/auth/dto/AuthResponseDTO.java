package com.karthick.expenz.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.karthick.expenz.users.dto.UserDTO;
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

  private UserDTO user;
}
