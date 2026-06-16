package com.karthick.expenz.auth;

import com.karthick.expenz.auth.dto.AuthResponseDTO;
import com.karthick.expenz.auth.dto.LoginRequestDTO;
import com.karthick.expenz.auth.dto.RegisterRequestDTO;
import com.karthick.expenz.constants.SecurityConstants;
import com.karthick.expenz.exception.EmailAlreadyExistsException;
import com.karthick.expenz.exception.InactiveAccountException;
import com.karthick.expenz.exception.InvalidCredentialsException;
import com.karthick.expenz.exception.InvalidTokenException;
import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  public AuthResponseDTO register(RegisterRequestDTO request) {
    Optional<User> user = userRepository.findByEmailIgnoreCase(
      request.getEmail().toLowerCase()
    );
    if (user.isEmpty()) {
      User newUser = new User();
      newUser.setName(request.getName());
      newUser.setEmail(request.getEmail().toLowerCase());
      newUser.setPassword(passwordEncoder.encode(request.getPassword()));
      newUser.setActive(true);
      userRepository.save(newUser);
      return buildAuthResponse(newUser.getEmail());
    }

    User existingUser = user.get();
    if (existingUser.isActive()) {
      throw new EmailAlreadyExistsException(request.getEmail());
    } else if (!existingUser.isActive()) {
      throw new InactiveAccountException(request.getEmail());
    }

    existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
    existingUser.setActive(true);
    userRepository.save(existingUser);
    return buildAuthResponse(existingUser.getEmail());
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

  // ──────────────────────────────────────────────
  //  Private helpers
  // ──────────────────────────────────────────────

  /**
   * Generates access + refresh tokens and wraps them in a response DTO.
   */
  private AuthResponseDTO buildAuthResponse(String email) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    String accessToken = jwtService.generateAccessToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(userDetails);

    return AuthResponseDTO.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .tokenType(SecurityConstants.BEARER)
      .expiresIn(jwtService.getAccessTokenExpiration())
      .build();
  }
}
