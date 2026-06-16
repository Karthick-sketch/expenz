package com.karthick.expenz.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSession {

  public long getAuthenticatedUserId() {
    Object principal = SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    if (principal instanceof CustomUserDetails) {
      return ((CustomUserDetails) principal).getId();
    }
    throw new IllegalStateException(
      "Authenticated user details not found in security context"
    );
  }
}
