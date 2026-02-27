package com.bosams.web;

import com.bosams.common.ApiException;
import com.bosams.domain.UserEntity;
import com.bosams.service.AuthService;
import com.bosams.web.dto.LoginRequest;
import com.bosams.web.dto.LoginResponse;
import com.bosams.web.dto.RefreshRequest;
import com.bosams.web.dto.RefreshResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            return ResponseEntity.ok(auth.login(req.email(), req.password()));
        } catch (ApiException ex) {
            if (ex.getStatus() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", ex.getMessage()));
            }
            throw ex;
        }
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return auth.refresh(req.refreshToken());
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserEntity user, @RequestBody Map<String, String> req) {
        auth.logout(user, req.get("refreshToken"));
    }
}
