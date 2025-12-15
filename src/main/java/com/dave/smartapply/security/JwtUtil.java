package com.dave.smartapply.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    // Secret Key für JWT (aus application.properties)
    private final SecretKey secretKey;

    // Token Gültigkeit: 24 Stunden
    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000; // 24h in milliseconds

    public JwtUtil(@Value("${jwt.secret:MySecretKeyForJWTTokenGenerationThatIsLongEnough}") String secret) {
        // Secret Key aus Config oder Default
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // JWT Token generieren
    public String generateToken(String email, Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .subject(email)  // Email als Subject
                .claim("userId", userId)  // User ID als Custom Claim
                .issuedAt(now)  // Ausgestellt am
                .expiration(expiryDate)  // Gültig bis
                .signWith(secretKey)  // Mit Secret Key signieren
                .compact();
    }

    // Email aus Token extrahieren
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // User ID aus Token extrahieren
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("userId", Long.class);
    }

    // Token validieren
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT Token validation error: {}", e.getMessage());
            return false;
        }
    }

    // Token abgelaufen?
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}