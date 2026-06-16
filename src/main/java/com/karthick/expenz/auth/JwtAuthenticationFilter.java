package com.karthick.expenz.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karthick.expenz.constants.SecurityConstants;
import com.karthick.expenz.exception.ErrorResponseDTO;
import com.karthick.expenz.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String jwt = extractJwtFromRequest(request);
    if (jwt == null) {
      sendError(response, "Token not found");
      return;
    }

    try {
      authenticateRequest(jwt, request);
    } catch (ExpiredJwtException e) {
      sendError(response, "Token has expired");
      return;
    } catch (SignatureException e) {
      sendError(response, "Invalid token signature");
      return;
    } catch (MalformedJwtException e) {
      sendError(response, "Malformed token");
      return;
    } catch (InvalidTokenException e) {
      sendError(response, e.getMessage());
      return;
    } catch (UsernameNotFoundException e) {
      sendError(response, "User not found for token");
      return;
    } catch (Exception e) {
      log.error("JWT authentication failed", e);
      sendError(response, "Authentication failed");
      return;
    }

    filterChain.doFilter(request, response);
  }

  // ──────────────────────────────────────────────
  //  Private helpers
  // ──────────────────────────────────────────────

  /**
   * Extracts the JWT from the Authorization header, or returns null
   * if the header is absent or doesn't start with "Bearer ".
   */
  private String extractJwtFromRequest(HttpServletRequest request) {
    String header = request.getHeader(SecurityConstants.AUTHORIZATION);

    if (header == null || !header.startsWith(SecurityConstants.BEARER_PREFIX)) {
      return null;
    }

    return header.substring(SecurityConstants.BEARER_PREFIX.length());
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return SecurityConstants.PUBLIC_PATH.stream().anyMatch(path::startsWith);
  }

  /**
   * Validates the JWT and sets the SecurityContext if authentication succeeds.
   */
  private void authenticateRequest(String jwt, HttpServletRequest request) {
    String userEmail = jwtService.extractEmail(jwt);
    if (
      userEmail == null ||
      SecurityContextHolder.getContext().getAuthentication() != null
    ) {
      return;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

    if (
      !jwtService.isTokenValid(jwt, userDetails) ||
      !jwtService.isAccessToken(jwt)
    ) {
      throw new InvalidTokenException();
    }

    UsernamePasswordAuthenticationToken authToken =
      new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
      );
    authToken.setDetails(
      new WebAuthenticationDetailsSource().buildDetails(request)
    );

    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * Writes a JSON error response with HTTP 401 status.
   */
  private void sendError(HttpServletResponse response, String message)
    throws IOException {
    ErrorResponseDTO error = ErrorResponseDTO.builder()
      .status(HttpStatus.UNAUTHORIZED.value())
      .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
      .message(message)
      .timestamp(LocalDateTime.now())
      .build();

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), error);
  }
}
