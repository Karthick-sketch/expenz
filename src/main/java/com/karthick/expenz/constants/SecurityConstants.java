package com.karthick.expenz.constants;

import java.util.List;

public final class SecurityConstants {

  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  public static final String AUTH_PATH = "/api/auth";
  public static final List<String> PUBLIC_PATH = List.of(AUTH_PATH);

  private SecurityConstants() {}
}
