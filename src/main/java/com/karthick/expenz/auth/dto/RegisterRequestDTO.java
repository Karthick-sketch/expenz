package com.karthick.expenz.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

  private String name;
  private String email;
  private String password;
  private String currencyCode;
}
