package com.karthick.expenz.users.dto;

public record UserUpdateDTO(
  String name,
  String email,
  String password,
  String currencyCode
) {}
