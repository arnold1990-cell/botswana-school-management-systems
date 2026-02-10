package com.bosams.schoolsetup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class Dtos {
    private Dtos() {
    }

    public record SchoolRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 255) String address,
            @Email @Size(max = 100) String contactEmail,
            @Size(max = 40) String contactPhone,
            @Size(max = 255) String logoUrl,
            @Size(max = 255) String documentHeader,
            @Size(max = 100) String reportLanguages,
            @Size(max = 100) String defaultTermStructure,
            @Size(max = 100) String documentNumberFormat) {
    }

    public record SchoolResponse(
            Long id,
            String name,
            String address,
            String contactEmail,
            String contactPhone,
            String logoUrl,
            String documentHeader,
            String reportLanguages,
            String defaultTermStructure,
            String documentNumberFormat) {
    }

    public record AcademicYearRequest(
            @NotNull @Positive Long schoolId,
            @NotBlank @Size(max = 100) String name,
            LocalDate startDate,
            LocalDate endDate) {
    }

    public record AcademicYearResponse(
            Long id,
            Long schoolId,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            boolean active) {
    }

    public record SetActiveAcademicYearRequest(@NotNull @Positive Long schoolId) {
    }

    public record TermRequest(
            @NotNull @Positive Long schoolId,
            @NotNull @Positive Long academicYearId,
            @NotBlank @Size(max = 100) String name,
            LocalDate startDate,
            LocalDate endDate) {
    }

    public record TermResponse(
            Long id,
            Long schoolId,
            Long academicYearId,
            String name,
            LocalDate startDate,
            LocalDate endDate) {
    }

    public record GradeRequest(@NotNull @Positive Long schoolId, @NotBlank @Size(max = 80) String name) {
    }

    public record GradeResponse(Long id, Long schoolId, String name) {
    }

    public record ClassRoomRequest(
            @NotNull @Positive Long schoolId,
            @NotNull @Positive Long academicYearId,
            @NotNull @Positive Long gradeId,
            @NotBlank @Size(max = 30) String code,
            @NotBlank @Size(max = 100) String name) {
    }

    public record ClassRoomResponse(Long id, Long schoolId, Long academicYearId, Long gradeId, String code, String name) {
    }

    public record NamedRequest(@NotNull @Positive Long schoolId, @NotBlank @Size(max = 120) String name) {
    }

    public record NamedResponse(Long id, Long schoolId, String name) {
    }

    public record ExtraMuralActivityRequest(@NotNull @Positive Long schoolId, @NotNull @Positive Long typeId, @NotBlank @Size(max = 120) String name) {
    }

    public record TeamRequest(@NotNull @Positive Long schoolId, @NotNull @Positive Long sportId, @NotBlank @Size(max = 120) String name) {
    }

    public record CompetitionRequest(@NotNull @Positive Long schoolId, @NotNull @Positive Long sportId, @NotBlank @Size(max = 120) String name) {
    }

    public record CodeRequest(
            @NotNull @Positive Long schoolId,
            @NotBlank @Size(max = 40) String code,
            @NotBlank @Size(max = 120) String description) {
    }

    public record CodeResponse(Long id, Long schoolId, String code, String description) {
    }
}
