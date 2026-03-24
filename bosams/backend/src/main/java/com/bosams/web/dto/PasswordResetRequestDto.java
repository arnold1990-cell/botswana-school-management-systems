package com.bosams.web.dto;

import com.bosams.domain.PasswordResetRequestEntity;

import java.time.Instant;

public record PasswordResetRequestDto(
        Long id,
        String admissionNo,
        String studentName,
        String guardianEmail,
        String guardianPhone,
        String reason,
        String status,
        String adminNote,
        Instant createdAt,
        Instant resolvedAt
) {
    public static PasswordResetRequestDto from(PasswordResetRequestEntity entity) {
        return new PasswordResetRequestDto(
                entity.getId(),
                entity.getAdmissionNo(),
                entity.getStudentName(),
                entity.getGuardianEmail(),
                entity.getGuardianPhone(),
                entity.getReason(),
                entity.getStatus().name(),
                entity.getAdminNote(),
                entity.getCreatedAt(),
                entity.getResolvedAt()
        );
    }
}
