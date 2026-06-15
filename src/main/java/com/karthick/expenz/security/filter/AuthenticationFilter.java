package com.karthick.expenz.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.karthick.expenz.security.SecurityConstants;
import com.karthick.expenz.users.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private AuthenticationManager authenticationManager;

  @Override
  public Authentication attemptAuthentication(
    HttpServletRequest request,
    HttpServletResponse response
  ) throws AuthenticationException {
    try {
      User user = new ObjectMapper().readValue(
        request.getInputStream(),
        User.class
      );
      Authentication authentication = new UsernamePasswordAuthenticationToken(
        user.getUsername(),
        user.getPassword()
      );
      return authenticationManager.authenticate(authentication);
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

  @Override
  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authResult
  ) throws IOException, ServletException {
    String token = JWT.create()
      .withSubject(authResult.getName())
      .withExpiresAt(
        new Date(
          System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION
        )
      )
      .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));
    response.setHeader(
      SecurityConstants.AUTHORIZATION,
      SecurityConstants.BEARER + token
    );
  }

  @Override
  protected void unsuccessfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException failed
  ) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write(failed.getMessage());
    response.getWriter().flush();
  }
}
