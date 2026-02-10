package com.bosams.schoolsetup.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public final class Dtos {
    private Dtos() {}
    public record SchoolRequest(@NotBlank @Size(max=150) String name, @Size(max=255) String address, @Email String contactEmail,
                                @Size(max=40) String contactPhone, @Size(max=255) String logoUrl, @Size(max=255) String documentHeader,
                                @Size(max=100) String reportLanguages, @Size(max=100) String defaultTermStructure,
                                @Size(max=100) String documentNumberFormat) {}
    public record SchoolResponse(Long id, String name, String address, String contactEmail, String contactPhone, String logoUrl,
                                 String documentHeader, String reportLanguages, String defaultTermStructure, String documentNumberFormat) {}
    public record AcademicYearRequest(@NotNull Long schoolId, @NotBlank @Size(max=100) String name, LocalDate startDate, LocalDate endDate) {}
    public record AcademicYearResponse(Long id, Long schoolId, String name, LocalDate startDate, LocalDate endDate, boolean active) {}
    public record SetActiveAcademicYearRequest(@NotNull Long schoolId) {}
    public record TermRequest(@NotNull Long schoolId, @NotNull Long academicYearId, @NotBlank @Size(max=100) String name, LocalDate startDate, LocalDate endDate) {}
    public record TermResponse(Long id, Long schoolId, Long academicYearId, String name, LocalDate startDate, LocalDate endDate) {}
    public record GradeRequest(@NotNull Long schoolId, @NotBlank @Size(max=80) String name) {}
    public record GradeResponse(Long id, Long schoolId, String name) {}
    public record ClassRoomRequest(@NotNull Long schoolId, @NotNull Long academicYearId, @NotNull Long gradeId, @NotBlank @Size(max=30) String code, @NotBlank @Size(max=100) String name) {}
    public record ClassRoomResponse(Long id, Long schoolId, Long academicYearId, Long gradeId, String code, String name) {}
    public record NamedRequest(@NotNull Long schoolId, @NotBlank @Size(max=120) String name) {}
    public record NamedResponse(Long id, Long schoolId, String name) {}
    public record ExtraMuralActivityRequest(@NotNull Long schoolId, @NotNull Long typeId, @NotBlank @Size(max=120) String name) {}
    public record TeamRequest(@NotNull Long schoolId, @NotNull Long sportId, @NotBlank @Size(max=120) String name) {}
    public record CompetitionRequest(@NotNull Long schoolId, @NotNull Long sportId, @NotBlank @Size(max=120) String name) {}
    public record CodeRequest(@NotNull Long schoolId, @NotBlank @Size(max=40) String code, @NotBlank @Size(max=120) String description) {}
    public record CodeResponse(Long id, Long schoolId, String code, String description) {}
}
