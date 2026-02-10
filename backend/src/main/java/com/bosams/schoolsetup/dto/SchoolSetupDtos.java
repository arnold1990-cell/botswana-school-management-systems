package com.bosams.schoolsetup.dto;

import com.bosams.schoolsetup.domain.enums.CalendarEventType;
import com.bosams.schoolsetup.domain.enums.SchoolType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class SchoolSetupDtos {
    private SchoolSetupDtos() {
    }

    public record SchoolProfileRequest(
            @NotBlank @Size(max = 180) String name,
            @NotBlank @Size(max = 300) String address,
            @Size(max = 30) String phone,
            @Email @Size(max = 120) String email,
            @NotNull SchoolType schoolType,
            @Size(max = 255) String logoFileKey,
            @Size(max = 255) String logoFilename,
            @Size(max = 100) String logoContentType,
            @Positive Long logoSize
    ) {}

    public record SchoolProfileResponse(UUID id, String name, String address, String phone, String email, SchoolType schoolType,
                                        String logoFileKey, String logoFilename, String logoContentType, Long logoSize,
                                        Instant createdAt, Instant updatedAt) {}

    public record AcademicYearRequest(@NotNull @Min(2000) Integer year, @NotNull LocalDate startDate, @NotNull LocalDate endDate,
                                      boolean active) {}
    public record AcademicYearResponse(UUID id, Integer year, LocalDate startDate, LocalDate endDate, boolean active) {}

    public record TermRequest(@NotBlank @Size(max = 80) String name, @NotNull UUID academicYearId,
                              @NotNull LocalDate startDate, @NotNull LocalDate endDate) {}
    public record TermResponse(UUID id, String name, UUID academicYearId, LocalDate startDate, LocalDate endDate) {}

    public record GradeRequest(@NotBlank @Size(max = 60) String name, @NotNull Integer sortOrder) {}
    public record GradeResponse(UUID id, String name, Integer sortOrder) {}

    public record SchoolClassRequest(@NotBlank @Size(max = 40) String classCode, @NotBlank @Size(max = 100) String className,
                                     @NotNull UUID gradeId, @NotNull UUID academicYearId) {}
    public record SchoolClassResponse(UUID id, String classCode, String className, UUID gradeId, UUID academicYearId) {}

    public record CalendarEventRequest(@NotBlank @Size(max = 150) String title, @NotNull CalendarEventType eventType,
                                       @NotNull UUID termId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {}
    public record CalendarEventResponse(UUID id, String title, CalendarEventType eventType, UUID termId,
                                        LocalDate startDate, LocalDate endDate) {}

    public record MasterDataRequest(@NotBlank @Size(max = 120) String name, @Size(max = 50) String code, boolean active) {}
    public record MasterDataResponse(UUID id, String name, String code, boolean active) {}

    public record MediaReferenceRequest(@NotNull UUID ownerId, @NotBlank @Size(max = 255) String fileKey,
                                        @NotBlank @Size(max = 255) String filename,
                                        @NotBlank @Size(max = 100) String contentType,
                                        @Positive long size) {}
    public record MediaReferenceResponse(UUID id, UUID ownerId, String fileKey, String filename, String contentType, long size) {}
}
