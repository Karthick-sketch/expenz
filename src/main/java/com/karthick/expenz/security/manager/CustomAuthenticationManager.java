package com.karthick.expenz.security.manager;

import com.karthick.expenz.entity.User;
import com.karthick.expenz.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

  private UserService userService;
  private PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication)
    throws AuthenticationException {
    User user = userService.findUserByUsername(authentication.getName());
    if (
      !passwordEncoder.matches(
        authentication.getCredentials().toString(),
        user.getPassword()
      )
    ) {
      throw new BadCredentialsException("You provided an incorrect password.");
    }
    return new UsernamePasswordAuthenticationToken(
      authentication.getName(),
      authentication.getCredentials()
    );
  }
}
