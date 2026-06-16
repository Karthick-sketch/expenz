package com.karthick.expenz.auth;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
  private final long id;

  public CustomUserDetails(
    long id,
    String username,
    String password,
    Collection<? extends GrantedAuthority> authorities
  ) {
    super(username, password, authorities);
    this.id = id;
  }
}
