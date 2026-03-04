package com.bosams.auth;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private static final String SECRET = "1234567890123456789012345678901234567890123456789012345678901234";

    @Test
    void generateAndParseAccessToken() {
        JwtService service = new JwtService(SECRET, 15, 7);
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        String token = service.generateAccessToken(userId, "ADMIN");

        var claims = service.parse(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
    }

    @Test
    void refreshTokenHasNoRoleClaim() {
        JwtService service = new JwtService(SECRET, 15, 7);
        String token = service.generateRefreshToken(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        assertThat(service.parse(token).get("role")).isNull();
    }

    @Test
    void parseFailsWithDifferentSecretSignatureMismatch() {
        JwtService serviceA = new JwtService(SECRET, 15, 7);
        JwtService serviceB = new JwtService("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 15, 7);
        String token = serviceA.generateAccessToken(UUID.randomUUID(), "TEACHER");

        assertThatThrownBy(() -> serviceB.parse(token)).isInstanceOf(JwtException.class);
    }

    @Test
    void parseFailsWhenExpired() throws InterruptedException {
        JwtService service = new JwtService(SECRET, 0, 0);
        String token = service.generateAccessToken(UUID.randomUUID(), "ADMIN");
        Thread.sleep(5);

        assertThatThrownBy(() -> service.parse(token)).isInstanceOf(JwtException.class);
    }
}
