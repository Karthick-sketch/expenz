package com.karthick.expenz.auth;

import com.karthick.expenz.auth.dto.AuthResponseDTO;
import com.karthick.expenz.auth.dto.LoginRequestDTO;
import com.karthick.expenz.auth.dto.RegisterRequestDTO;
import com.karthick.expenz.constants.SecurityConstants;
import com.karthick.expenz.exception.EmailAlreadyExistsException;
import com.karthick.expenz.exception.EntityNotFoundException;
import com.karthick.expenz.exception.InactiveAccountException;
import com.karthick.expenz.exception.InvalidCredentialsException;
import com.karthick.expenz.exception.InvalidTokenException;
import com.karthick.expenz.users.dto.UserCreateDTO;
import com.karthick.expenz.users.dto.UserDTO;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  public AuthResponseDTO register(RegisterRequestDTO request) {
    try {
      User existingUser = userService.findUserByEmail(
        request.getEmail().toLowerCase()
      );
      if (existingUser.isActive()) {
        throw new EmailAlreadyExistsException(request.getEmail());
      } else {
        throw new InactiveAccountException(request.getEmail());
      }
    } catch (EntityNotFoundException ex) {
      UserCreateDTO newUser = new UserCreateDTO(
        request.getName(),
        request.getEmail().toLowerCase(),
        request.getPassword()
      );
      userService.createUser(newUser);
      return buildAuthResponse(newUser.getEmail());
    }
  }

  public AuthResponseDTO login(LoginRequestDTO request) {
    String email = request.getEmail().toLowerCase();
    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, request.getPassword())
      );
    } catch (BadCredentialsException e) {
      throw new InvalidCredentialsException();
    }

    return buildAuthResponse(email);
  }

  public AuthResponseDTO refreshToken(String refreshToken) {
    String email = jwtService.extractEmail(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    if (
      !jwtService.isTokenValid(refreshToken, userDetails) ||
      !jwtService.isRefreshToken(refreshToken)
    ) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    return buildAuthResponse(email);
  }

  private AuthResponseDTO buildAuthResponse(String email) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    String accessToken = jwtService.generateAccessToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(userDetails);
    UserDTO userDTO = userService.findUserDTOByEmail(email);

    return AuthResponseDTO.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType(SecurityConstants.BEARER)
      .expiresIn(jwtService.getAccessTokenExpiration())
      .user(userDTO)
      .build();
  }
}
