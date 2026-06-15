package com.karthick.expenz.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.karthick.expenz.security.SecurityConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String header = request.getHeader(SecurityConstants.AUTHORIZATION);

    if (header != null && header.startsWith(SecurityConstants.BEARER)) {
      String token = header.replace(SecurityConstants.BEARER, "");
      String username = JWT.require(
        Algorithm.HMAC512(SecurityConstants.SECRET_KEY)
      )
        .build()
        .verify(token)
        .getSubject();

      Authentication authentication = new UsernamePasswordAuthenticationToken(
        username,
        null,
        List.of()
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }
}
