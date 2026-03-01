package com.bosams.web;

import com.bosams.domain.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MeController {
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal UserEntity user) {
        return new MeResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public record MeResponse(UUID id, String email, String fullName, String role) {}
}
