package com.karthick.expenz.security;

import com.karthick.expenz.security.filter.AuthenticationFilter;
import com.karthick.expenz.security.filter.ExceptionHandlerFilter;
import com.karthick.expenz.security.filter.JWTAuthenticationFilter;
import com.karthick.expenz.security.manager.CustomAuthenticationManager;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

  private final CustomAuthenticationManager customAuthenticationManager;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    AuthenticationFilter authenticationFilter = new AuthenticationFilter(
      customAuthenticationManager
    );
    authenticationFilter.setFilterProcessesUrl("/authenticate");

    http
      .csrf(AbstractHttpConfigurer::disable)
      .authorizeHttpRequests(authorize ->
        authorize
          .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH)
          .permitAll()
      )
      .authorizeHttpRequests(authorize ->
        authorize.anyRequest().authenticated()
      )
      .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
      .addFilter(authenticationFilter)
      .addFilterAfter(new JWTAuthenticationFilter(), AuthenticationFilter.class)
      .sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      );

    return http.build();
  }
}
