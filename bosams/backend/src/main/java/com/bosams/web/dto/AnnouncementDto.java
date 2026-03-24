package com.bosams.web.dto;

import com.bosams.domain.Enums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AnnouncementDto {
    public record AnnouncementRequest(
            @NotBlank @Size(max = 180) String title,
            @NotBlank String message,
            Enums.Role targetRole,
            Integer targetGradeLevel,
            OffsetDateTime expiresAt
    ) {}

    public record AnnouncementResponse(
            Long id,
            String title,
            String message,
            Enums.Role targetRole,
            Integer targetGradeLevel,
            UUID createdByUserId,
            OffsetDateTime createdAt,
            OffsetDateTime expiresAt
    ) {}
}
