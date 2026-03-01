package com.bosams.web.dto;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMinutes,
        UserSummary user
) {
    public record UserSummary(
            UUID id,
            String fullName,
            String email,
            String role
    ) {
    }
}
