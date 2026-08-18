package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.karthick.expenz.auth.AuthService;
import com.karthick.expenz.auth.JwtService;
import com.karthick.expenz.auth.dto.AuthResponseDTO;
import com.karthick.expenz.auth.dto.LoginRequestDTO;
import com.karthick.expenz.auth.dto.RegisterRequestDTO;
import com.karthick.expenz.exception.EmailAlreadyExistsException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.exception.InactiveAccountException;
import com.karthick.expenz.exception.InvalidCredentialsException;
import com.karthick.expenz.exception.InvalidTokenException;
import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  private AuthService authService;

  private static final String EMAIL = "kang@marvel.com";
  private static final String PASSWORD = "password123";
  private static final String ACCESS_TOKEN = "mock.access.token";
  private static final String REFRESH_TOKEN = "mock.refresh.token";
  private static final long EXPIRES_IN = 900_000L;

  private User getActiveUser() {
    User user = new User();
    user.setId(1L);
    user.setName("Kang");
    user.setEmail(EMAIL);
    user.setPassword("encodedpassword");
    user.setActive(true);
    return user;
  }

  private User getInactiveUser() {
    User user = getActiveUser();
    user.setActive(false);
    return user;
  }

  private UserDTO getUserDTO() {
    return new UserDTO(1L, "Kang", EMAIL, "USD");
  }

  private UserDetails getMockUserDetails() {
    return new org.springframework.security.core.userdetails.User(
      EMAIL, "encodedpassword", java.util.Collections.emptyList()
    );
  }

  private void stubBuildAuthResponse() {
    UserDetails userDetails = getMockUserDetails();
    when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
    when(jwtService.generateAccessToken(userDetails)).thenReturn(ACCESS_TOKEN);
    when(jwtService.generateRefreshToken(userDetails)).thenReturn(REFRESH_TOKEN);
    when(jwtService.getAccessTokenExpiration()).thenReturn(EXPIRES_IN);
    when(userService.findUserDTOByEmail(EMAIL)).thenReturn(getUserDTO());
  }

  // ──────────────────────────────────────────────
  //  register()
  // ──────────────────────────────────────────────

  @Test
  public void testRegister_Success() {
    RegisterRequestDTO request = new RegisterRequestDTO("Kang", EMAIL, PASSWORD, "USD");

    // No existing user → throws EntityNotFoundException → register proceeds
    when(userService.findUserByEmail(EMAIL))
      .thenThrow(new EntityNotFoundException("Not found"));
    when(userService.createUser(any(UserCreateDTO.class))).thenReturn(getUserDTO());
    stubBuildAuthResponse();

    AuthResponseDTO response = authService.register(request);

    assertNotNull(response);
    assertEquals(ACCESS_TOKEN, response.getAccessToken());
    assertEquals(REFRESH_TOKEN, response.getRefreshToken());
    assertEquals(EXPIRES_IN, response.getExpiresIn());
    assertNotNull(response.getUser());
    verify(userService, times(1)).createUser(any(UserCreateDTO.class));
  }

  @Test
  public void testRegister_EmailAlreadyExists_ActiveAccount() {
    RegisterRequestDTO request = new RegisterRequestDTO("Kang", EMAIL, PASSWORD, "USD");
    when(userService.findUserByEmail(EMAIL)).thenReturn(getActiveUser());

    assertThrows(EmailAlreadyExistsException.class, () ->
      authService.register(request)
    );
    verify(userService, never()).createUser(any());
  }

  @Test
  public void testRegister_EmailAlreadyExists_InactiveAccount() {
    RegisterRequestDTO request = new RegisterRequestDTO("Kang", EMAIL, PASSWORD, "USD");
    when(userService.findUserByEmail(EMAIL)).thenReturn(getInactiveUser());

    assertThrows(InactiveAccountException.class, () ->
      authService.register(request)
    );
    verify(userService, never()).createUser(any());
  }

  // ──────────────────────────────────────────────
  //  login()
  // ──────────────────────────────────────────────

  @Test
  public void testLogin_Success() {
    LoginRequestDTO request = new LoginRequestDTO(EMAIL, PASSWORD);
    stubBuildAuthResponse();

    AuthResponseDTO response = authService.login(request);

    assertNotNull(response);
    assertEquals(ACCESS_TOKEN, response.getAccessToken());
    assertEquals(REFRESH_TOKEN, response.getRefreshToken());
    verify(authenticationManager, times(1)).authenticate(
      any(UsernamePasswordAuthenticationToken.class)
    );
  }

  @Test
  public void testLogin_InvalidCredentials_ThrowsException() {
    LoginRequestDTO request = new LoginRequestDTO(EMAIL, "wrongpassword");
    when(authenticationManager.authenticate(any()))
      .thenThrow(new BadCredentialsException("Bad credentials"));

    assertThrows(InvalidCredentialsException.class, () ->
      authService.login(request)
    );
  }

  @Test
  public void testLogin_EmailIsCaseInsensitive() {
    LoginRequestDTO request = new LoginRequestDTO("KANG@MARVEL.COM", PASSWORD);
    // Stub using the lowercased email that the service will pass
    UserDetails userDetails = getMockUserDetails();
    when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
    when(jwtService.generateAccessToken(userDetails)).thenReturn(ACCESS_TOKEN);
    when(jwtService.generateRefreshToken(userDetails)).thenReturn(REFRESH_TOKEN);
    when(jwtService.getAccessTokenExpiration()).thenReturn(EXPIRES_IN);
    when(userService.findUserDTOByEmail(EMAIL)).thenReturn(getUserDTO());

    AuthResponseDTO response = authService.login(request);

    assertNotNull(response);
    // Verify authenticate was called with the lowercased email
    verify(authenticationManager).authenticate(
      argThat(auth ->
        auth instanceof UsernamePasswordAuthenticationToken &&
        EMAIL.equals(((UsernamePasswordAuthenticationToken) auth).getPrincipal())
      )
    );
  }

  // ──────────────────────────────────────────────
  //  refreshToken()
  // ──────────────────────────────────────────────

  @Test
  public void testRefreshToken_Success() {
    UserDetails userDetails = getMockUserDetails();
    when(jwtService.extractEmail(REFRESH_TOKEN)).thenReturn(EMAIL);
    when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
    when(jwtService.isTokenValid(REFRESH_TOKEN, userDetails)).thenReturn(true);
    when(jwtService.isRefreshToken(REFRESH_TOKEN)).thenReturn(true);
    // stubBuildAuthResponse stubs loadUserByUsername and token generation
    when(jwtService.generateAccessToken(userDetails)).thenReturn(ACCESS_TOKEN);
    when(jwtService.generateRefreshToken(userDetails)).thenReturn(REFRESH_TOKEN);
    when(jwtService.getAccessTokenExpiration()).thenReturn(EXPIRES_IN);
    when(userService.findUserDTOByEmail(EMAIL)).thenReturn(getUserDTO());

    AuthResponseDTO response = authService.refreshToken(REFRESH_TOKEN);

    assertNotNull(response);
    assertEquals(ACCESS_TOKEN, response.getAccessToken());
  }

  @Test
  public void testRefreshToken_InvalidToken_ThrowsException() {
    UserDetails userDetails = getMockUserDetails();
    when(jwtService.extractEmail(ACCESS_TOKEN)).thenReturn(EMAIL);
    when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
    when(jwtService.isTokenValid(ACCESS_TOKEN, userDetails)).thenReturn(true);
    // This is actually an access token, not a refresh token
    when(jwtService.isRefreshToken(ACCESS_TOKEN)).thenReturn(false);

    assertThrows(InvalidTokenException.class, () ->
      authService.refreshToken(ACCESS_TOKEN)
    );
  }

  @Test
  public void testRefreshToken_ExpiredToken_ThrowsException() {
    UserDetails userDetails = getMockUserDetails();
    when(jwtService.extractEmail(REFRESH_TOKEN)).thenReturn(EMAIL);
    when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
    when(jwtService.isTokenValid(REFRESH_TOKEN, userDetails)).thenReturn(false);

    assertThrows(InvalidTokenException.class, () ->
      authService.refreshToken(REFRESH_TOKEN)
    );
  }
}
