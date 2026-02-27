package com.bosams.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessMinutes;
    private final long refreshDays;
    public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.access-expiration-minutes}") long accessMinutes, @Value("${jwt.refresh-expiration-days}") long refreshDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessMinutes = accessMinutes; this.refreshDays = refreshDays;
    }
    public String accessToken(Long userId, String role) { return token(userId, role, Duration.ofMinutes(accessMinutes)); }
    public String refreshToken(Long userId, String role) { return token(userId, role, Duration.ofDays(refreshDays)); }
    private String token(Long userId, String role, Duration d){ Instant now=Instant.now();
        return Jwts.builder().subject(String.valueOf(userId)).claim("role", role).issuedAt(Date.from(now)).expiration(Date.from(now.plus(d))).signWith(key).compact(); }
    public Claims parse(String token){ return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
