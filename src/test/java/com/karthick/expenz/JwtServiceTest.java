package com.karthick.expenz;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.karthick.expenz.auth.JwtProperties;
import com.karthick.expenz.auth.JwtService;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @Mock
  private JwtProperties jwtProperties;

  @InjectMocks
  private JwtService jwtService;

  private UserDetails testUser;

  private static final String BASE64_SECRET = "dGhpc19pc19hX3Zlcnlfc2VjdXJlX3NlY3JldF9rZXlfd2l0aF91bmRlcnNjb3Jlc19hbmRfaHlwaGVuc18xMjM0NTY";

  @BeforeEach
  public void setUp() {
    testUser = new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
      }

      @Override
      public String getPassword() {
        return "password";
      }

      @Override
      public String getUsername() {
        return "user@example.com";
      }

      @Override
      public boolean isAccountNonExpired() {
        return true;
      }

      @Override
      public boolean isAccountNonLocked() {
        return true;
      }

      @Override
      public boolean isCredentialsNonExpired() {
        return true;
      }

      @Override
      public boolean isEnabled() {
        return true;
      }
    };
  }

  @Test
  public void testGenerateAndValidateToken_withBase64Secret() {
    when(jwtProperties.getSecret()).thenReturn(BASE64_SECRET);
    when(jwtProperties.getAccessTokenExpiration()).thenReturn(900000L); // 15 mins

    String token = jwtService.generateAccessToken(testUser);
    assertNotNull(token);

    // Verify token claims and validation
    String email = jwtService.extractEmail(token);
    assertEquals("user@example.com", email);
    assertTrue(jwtService.isTokenValid(token, testUser));
    assertTrue(jwtService.isAccessToken(token));
    assertFalse(jwtService.isRefreshToken(token));
  }
}
