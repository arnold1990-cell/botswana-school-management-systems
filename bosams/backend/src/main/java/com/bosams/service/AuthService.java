package com.bosams.service;

import com.bosams.audit.AuditService;
import com.bosams.auth.JwtService;
import com.bosams.common.ApiException;
import com.bosams.domain.RefreshToken;
import com.bosams.domain.UserEntity;
import com.bosams.repository.RefreshTokenRepository;
import com.bosams.repository.UserRepository;
import com.bosams.web.dto.LoginResponse;
import com.bosams.web.dto.RefreshResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokens;
    private final AuditService audit;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwtService, RefreshTokenRepository refreshTokens, AuditService audit) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.audit = audit;
    }

    public LoginResponse login(String email, String password) {
        UserEntity user = users.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", "Invalid email or password"));

        if (!encoder.matches(password, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", "Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUser(user);
        rt.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshTokens.save(rt);

        audit.log(user.getId(), "LOGIN", "auth", String.valueOf(user.getId()), null);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpirationMinutes(),
                new LoginResponse.UserSummary(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name())
        );
    }

    public RefreshResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokens.findByTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid refresh token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "Refresh token expired");
        }

        UserEntity user = token.getUser();
        return new RefreshResponse(
                jwtService.generateAccessToken(user.getId(), user.getRole().name()),
                "Bearer",
                jwtService.getAccessExpirationMinutes()
        );
    }

    public void logout(UserEntity actor, String refreshToken) {
        refreshTokens.findByTokenAndRevokedFalse(refreshToken).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokens.save(t);
        });
        audit.log(actor.getId(), "LOGOUT", "auth", String.valueOf(actor.getId()), null);
    }
}
