package com.karthick.expenz.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.karthick.expenz.exception.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (EntityNotFoundException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_NOT_FOUND,
        "Username doesn't exist"
      );
    } catch (JWTVerificationException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_FORBIDDEN,
        "JWT not valid"
      );
    } catch (RuntimeException e) {
      setExceptionResponse(
        response,
        HttpServletResponse.SC_BAD_REQUEST,
        "Bad request"
      );
    }
  }

  private void setExceptionResponse(
    HttpServletResponse response,
    int status,
    String exceptionMessage
  ) throws IOException {
    response.setStatus(status);
    response.getWriter().write(exceptionMessage);
    response.getWriter().flush();
  }
}
