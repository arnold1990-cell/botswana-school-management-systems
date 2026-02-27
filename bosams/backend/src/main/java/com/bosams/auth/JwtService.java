package com.bosams.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-expiration-minutes}") long accessMinutes,
                      @Value("${jwt.refresh-expiration-days}") long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public String generateAccessToken(Long userId, String role) {
        return token(userId, role, Duration.ofMinutes(accessMinutes));
    }

    public String generateRefreshToken(Long userId) {
        return token(userId, null, Duration.ofDays(refreshDays));
    }

    private String token(Long userId, String role, Duration duration) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(duration)));

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessExpirationMinutes() {
        return accessMinutes;
    }
}
