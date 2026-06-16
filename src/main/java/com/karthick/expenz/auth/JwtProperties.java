package com.karthick.expenz.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Type-safe configuration properties for JWT authentication.
 * Bound to {@code jwt.*} properties in application.properties.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret;
  private long accessTokenExpiration;
  private long refreshTokenExpiration;
}
