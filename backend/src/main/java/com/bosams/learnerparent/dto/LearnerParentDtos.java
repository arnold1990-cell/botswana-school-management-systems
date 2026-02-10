package com.bosams.learnerparent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public final class LearnerParentDtos {
    private LearnerParentDtos() {}
    public record ApplicationRequest(@NotNull Long schoolId,@NotBlank String applicationNo, LocalDate appliedDate,@NotNull Long preferredGradeId,@NotNull Long preferredAcademicYearId,@NotBlank String learnerFirstName,@NotBlank String learnerLastName,LocalDate dateOfBirth,String gender,String previousSchool,String notes){}
    public record DecisionRequest(String decisionNote, String decidedBy) {}
    public record ArchiveRequest(@NotBlank String reason) {}
    public record LearnerRequest(@NotNull Long schoolId,@NotBlank String learnerNo,@NotBlank String firstName,@NotBlank String lastName,LocalDate dateOfBirth,String gender,String nationalId,String homeLanguage,Long currentAcademicYearId,Long currentGradeId,Long currentClassRoomId,Long houseId,LocalDate admissionDate){}
    public record LearnerTransferRequest(@NotNull Long schoolId,@NotNull String transferType,Long toAcademicYearId,Long toClassRoomId,Long toGradeId,@NotNull LocalDate effectiveDate,String reason,String capturedBy){}
    public record ParentRequest(@NotNull Long schoolId,String parentNo,@NotBlank String firstName,@NotBlank String lastName,String nationalId,@Email String email,@NotBlank String phone,String address){}
    public record LinkParentRequest(@NotNull Long schoolId,@NotNull Long parentId,@NotBlank String relationshipType,boolean isPrimaryContact,boolean livesWithLearner){}
    public record ActivityRequest(@NotNull Long schoolId,@NotBlank String activityType,@NotNull Long activityId,@NotBlank String role,LocalDate startDate,LocalDate endDate){}
    public record TransportRequest(@NotNull Long schoolId,@NotNull Long busRouteId,@NotNull Long ticketTypeId,@NotNull LocalDate startDate,LocalDate endDate){}
    public record LeadershipRequest(@NotNull Long schoolId,@NotBlank String roleType,@NotNull Long academicYearId,LocalDate startDate,LocalDate endDate,String notes){}
    public record IncidentRequest(@NotNull Long schoolId,@NotNull LocalDate incidentDate,@NotBlank String category,@NotBlank String description,String actionTaken,@NotBlank String reportedBy){}
    public record BarrierRequest(@NotNull Long schoolId,@NotBlank String barrierType,String notes,@NotNull LocalDate identifiedDate){}
    public record MentorRequest(@NotNull Long schoolId,@NotNull Long mentorStaffId,@NotNull LocalDate startDate,LocalDate endDate,String notes){}
    public record DisciplineRequest(@NotNull Long schoolId,@NotNull LocalDate entryDate,@NotBlank String entryType,@NotNull Long codeId,@NotNull Integer points,String notes,String capturedBy){}
    public record DetentionRequest(@NotNull Long schoolId,@NotNull LocalDate scheduledDate,@NotNull Integer durationMinutes,String reason,@NotBlank String status){}
    public record AttendanceItem(@NotNull Long learnerId,@NotNull String status,String notes,String capturedBy){}
    public record AttendanceCaptureRequest(@NotNull Long schoolId,Long academicYearId,Long termId,@NotNull LocalDate date,Integer period,Long classRoomId,@NotEmpty List<@Valid AttendanceItem> records){}
    public record AbsenceNotifyRequest(@NotNull Long schoolId,@NotNull LocalDate date,@NotBlank String channel,@NotBlank String message){}
}
