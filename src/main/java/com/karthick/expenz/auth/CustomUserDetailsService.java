package com.karthick.expenz.auth;

import com.karthick.expenz.users.entity.User;
import com.karthick.expenz.users.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByEmailIgnoreCase(email)
      .orElseThrow(() ->
        new UsernameNotFoundException("User not found with email: " + email)
      );

    return new CustomUserDetails(
      user.getId(),
      user.getEmail(),
      user.getPassword(),
      List.of(new SimpleGrantedAuthority("ROLE_USER"))
    );
  }
}
