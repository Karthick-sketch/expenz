package com.karthick.expenz.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

  private static final String CLAIM_TYPE = "type";
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final JwtProperties jwtProperties;

  // ──────────────────────────────────────────────
  //  Token generation
  // ──────────────────────────────────────────────

  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(
      Map.of(CLAIM_TYPE, TOKEN_TYPE_ACCESS),
      userDetails,
      jwtProperties.getAccessTokenExpiration()
    );
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(
      Map.of(CLAIM_TYPE, TOKEN_TYPE_REFRESH),
      userDetails,
      jwtProperties.getRefreshTokenExpiration()
    );
  }

  // ──────────────────────────────────────────────
  //  Token validation
  // ──────────────────────────────────────────────

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String email = extractEmail(token);
    return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isAccessToken(String token) {
    return TOKEN_TYPE_ACCESS.equals(extractTokenType(token));
  }

  public boolean isRefreshToken(String token) {
    return TOKEN_TYPE_REFRESH.equals(extractTokenType(token));
  }

  // ──────────────────────────────────────────────
  //  Claim extraction
  // ──────────────────────────────────────────────

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(extractAllClaims(token));
  }

  // ──────────────────────────────────────────────
  //  Accessors
  // ──────────────────────────────────────────────

  public long getAccessTokenExpiration() {
    return jwtProperties.getAccessTokenExpiration();
  }

  // ──────────────────────────────────────────────
  //  Internal helpers
  // ──────────────────────────────────────────────

  private String buildToken(
    Map<String, Object> extraClaims,
    UserDetails userDetails,
    long expirationMs
  ) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
      .claims(extraClaims)
      .subject(userDetails.getUsername())
      .issuedAt(new Date(now))
      .expiration(new Date(now + expirationMs))
      .signWith(getSigningKey())
      .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private String extractTokenType(String token) {
    try {
      return extractClaim(token, claims ->
        claims.get(CLAIM_TYPE, String.class)
      );
    } catch (Exception e) {
      return null;
    }
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(
      Decoders.BASE64.decode(jwtProperties.getSecret())
    );
  }
}
