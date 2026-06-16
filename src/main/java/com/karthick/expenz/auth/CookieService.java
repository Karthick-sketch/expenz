package com.karthick.expenz.auth;

import com.karthick.expenz.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Handles creation and clearing of refresh token cookies.
 */
@Component
@RequiredArgsConstructor
public class CookieService {

  private final JwtProperties jwtProperties;

  /**
   * Creates an HttpOnly cookie containing the refresh token.
   */
  public String createRefreshTokenCookie(String refreshToken) {
    long maxAgeSeconds = jwtProperties.getRefreshTokenExpiration() / 1000;
    return buildCookie(refreshToken, maxAgeSeconds);
  }

  /**
   * Creates a cookie that clears the refresh token from the browser.
   */
  public String createExpiredRefreshTokenCookie() {
    return buildCookie("", 0);
  }

  private String buildCookie(String value, long maxAge) {
    return ResponseCookie.from(SecurityConstants.REFRESH_TOKEN_COOKIE, value)
      .httpOnly(true)
      .sameSite("Lax")
      .secure(false)
      .path(SecurityConstants.AUTH_PATH)
      .maxAge(maxAge)
      .build()
      .toString();
  }
}
