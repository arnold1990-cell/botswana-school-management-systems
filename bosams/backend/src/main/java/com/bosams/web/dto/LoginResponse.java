package com.bosams.web.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMinutes,
        UserSummary user
) {
    public record UserSummary(
            Long id,
            String fullName,
            String email,
            String role
    ) {
    }
}
