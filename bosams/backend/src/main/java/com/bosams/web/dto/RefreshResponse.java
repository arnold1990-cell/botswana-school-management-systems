package com.bosams.web.dto;

public record RefreshResponse(
        String accessToken,
        String tokenType,
        long expiresInMinutes
) {
}
