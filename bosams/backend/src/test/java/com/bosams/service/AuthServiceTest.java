package com.bosams.service;

import com.bosams.audit.AuditService;
import com.bosams.auth.JwtService;
import com.bosams.common.ApiException;
import com.bosams.domain.Enums;
import com.bosams.domain.RefreshToken;
import com.bosams.domain.UserEntity;
import com.bosams.repository.RefreshTokenRepository;
import com.bosams.repository.UserRepository;
import com.bosams.testutil.TestConstants;
import com.bosams.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository users;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock RefreshTokenRepository refreshTokens;
    @Mock AuditService audit;
    @InjectMocks AuthService service;

    @Test
    void loginSuccessReturnsTokens() {
        UserEntity user = TestDataFactory.user(TestConstants.USER_ID, Enums.Role.ADMIN);
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user.getId(), user.getRole().name())).thenReturn("access");
        when(jwtService.generateRefreshToken(user.getId())).thenReturn("refresh");
        when(jwtService.getAccessExpirationMinutes()).thenReturn(15L);

        var response = service.login(user.getEmail(), "password");

        assertThat(response.accessToken()).isEqualTo("access");
        verify(refreshTokens).save(any(RefreshToken.class));
        verify(audit).log(eq(user.getId()), eq("LOGIN"), eq("auth"), eq(user.getId().toString()), isNull());
    }

    @Test
    void loginFailsWhenAuthenticationFails() {
        doThrow(new AuthenticationException("bad"){}).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> service.login("x@x.com", "bad")).isInstanceOf(ApiException.class);
        verify(users, never()).findByEmail(anyString());
    }

    @Test
    void refreshFailsWhenExpired() {
        RefreshToken token = new RefreshToken();
        token.setToken("t");
        token.setExpiresAt(Instant.now().minusSeconds(60));
        when(refreshTokens.findByTokenAndRevokedFalse("t")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.refresh("t")).isInstanceOf(ApiException.class);
    }

    @Test
    void refreshSuccessReturnsAccessToken() {
        UserEntity user = TestDataFactory.user(TestConstants.USER_ID, Enums.Role.PRINCIPAL);
        RefreshToken token = new RefreshToken();
        token.setToken("r");
        token.setUser(user);
        token.setExpiresAt(Instant.now().plusSeconds(120));
        when(refreshTokens.findByTokenAndRevokedFalse("r")).thenReturn(Optional.of(token));
        when(jwtService.parse("r")).thenReturn(io.jsonwebtoken.Jwts.claims().subject(user.getId().toString()).build());
        when(users.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user.getId(), user.getRole().name())).thenReturn("new-access");
        when(jwtService.getAccessExpirationMinutes()).thenReturn(15L);

        var out = service.refresh("r");

        assertThat(out.accessToken()).isEqualTo("new-access");
    }

    @Test
    void logoutRevokesRefreshTokenWhenPresent() {
        UserEntity user = TestDataFactory.user(TestConstants.USER_ID, Enums.Role.ADMIN);
        RefreshToken token = new RefreshToken();
        token.setRevoked(false);
        when(refreshTokens.findByTokenAndRevokedFalse("x")).thenReturn(Optional.of(token));

        service.logout(user, "x");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokens).save(token);
    }
}
