package com.bosams.service;

import com.bosams.audit.AuditService;
import com.bosams.auth.JwtService;
import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.RefreshToken;
import com.bosams.domain.UserEntity;
import com.bosams.repository.RefreshTokenRepository;
import com.bosams.repository.UserRepository;
import com.bosams.web.dto.LoginResponse;
import com.bosams.web.dto.RefreshResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {
    private static final String INVALID_LOGIN_MESSAGE = "Invalid email or password. Please try again.";
    private final UserRepository users;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokens;
    private final AuditService audit;

    public AuthService(UserRepository users,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokens,
                       AuditService audit) {
        this.users = users;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.audit = audit;
    }

    public LoginResponse login(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", INVALID_LOGIN_MESSAGE);
        }

        UserEntity user = users.findByEmail(email)
                .filter(u -> u.getStatus() == Enums.UserStatus.ACTIVE)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", INVALID_LOGIN_MESSAGE));

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
