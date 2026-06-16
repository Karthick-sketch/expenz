package com.karthick.expenz.auth;

import com.karthick.expenz.auth.dto.AuthResponseDTO;
import com.karthick.expenz.auth.dto.LoginRequestDTO;
import com.karthick.expenz.auth.dto.RegisterRequestDTO;
import com.karthick.expenz.constants.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final CookieService cookieService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponseDTO> register(
    @RequestBody RegisterRequestDTO request
  ) {
    AuthResponseDTO authResponse = authService.register(request);
    return buildResponseWithCookie(authResponse, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDTO> login(
    @RequestBody LoginRequestDTO request
  ) {
    AuthResponseDTO authResponse = authService.login(request);
    return buildResponseWithCookie(authResponse, HttpStatus.OK);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponseDTO> refresh(
    @CookieValue(
      name = SecurityConstants.REFRESH_TOKEN_COOKIE,
      required = false
    ) String refreshToken
  ) {
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    AuthResponseDTO authResponse = authService.refreshToken(refreshToken);
    return buildResponseWithCookie(authResponse, HttpStatus.OK);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout() {
    String cookie = cookieService.createExpiredRefreshTokenCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).build();
  }

  // ──────────────────────────────────────────────
  //  Private helpers
  // ──────────────────────────────────────────────

  /**
   * Attaches the refresh token as an HttpOnly cookie and strips
   * it from the response body before sending.
   */
  private ResponseEntity<AuthResponseDTO> buildResponseWithCookie(
    AuthResponseDTO authResponse,
    HttpStatus status
  ) {
    String cookie = cookieService.createRefreshTokenCookie(
      authResponse.getRefreshToken()
    );
    authResponse.setRefreshToken(null);

    return ResponseEntity.status(status)
      .header(HttpHeaders.SET_COOKIE, cookie)
      .body(authResponse);
  }
}
