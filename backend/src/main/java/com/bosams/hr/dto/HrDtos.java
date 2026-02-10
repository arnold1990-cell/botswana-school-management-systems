package com.bosams.hr.dto;

import com.bosams.hr.entity.AppraisalType;
import com.bosams.hr.entity.LeaveStatus;
import com.bosams.hr.entity.LeaveType;
import com.bosams.hr.entity.RegisterClassRole;
import com.bosams.hr.entity.StaffAttendanceStatus;
import com.bosams.hr.entity.StaffStatus;
import com.bosams.hr.entity.StaffType;
import com.bosams.hr.entity.TrainingCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class HrDtos {

    private HrDtos() {
    }

    public record StaffUpsertRequest(
            @NotNull Long schoolId,
            @NotBlank @Size(max = 40) String staffNumber,
            @NotNull StaffType staffType,
            @Size(max = 20) String title,
            @NotBlank String firstName,
            @NotBlank String lastName,
            String nationalId,
            String gender,
            LocalDate dateOfBirth,
            LocalDate employmentStartDate,
            LocalDate employmentEndDate,
            @Email String email,
            String phone,
            String addressLine1,
            String addressLine2,
            String city,
            String district,
            String postalCode
    ) {
    }

    public record StaffArchiveRequest(@NotBlank String reason) {
    }

    public record StaffResponse(
            Long id,
            Long schoolId,
            String staffNumber,
            StaffType staffType,
            String firstName,
            String lastName,
            StaffStatus status,
            String email,
            String phone,
            Instant archivedAt,
            String archivedReason,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy
    ) {
    }

    public record SubjectExperienceRequest(
            @NotNull Long schoolId,
            @NotNull Long subjectId,
            @NotNull @Min(0) @Max(60) Integer yearsExperience,
            String notes
    ) {
    }

    public record SubjectExperienceResponse(
            Long id,
            Long schoolId,
            Long staffId,
            Long subjectId,
            Integer yearsExperience,
            String notes
    ) {
    }

    public record RegisterClassAssignRequest(
            @NotNull Long schoolId,
            @NotNull Long staffId,
            @NotNull Long classRoomId,
            @NotNull Long academicYearId,
            @NotNull RegisterClassRole role
    ) {
    }

    public record RegisterClassAssignmentResponse(
            Long id,
            Long schoolId,
            Long staffId,
            Long classRoomId,
            Long academicYearId,
            RegisterClassRole role
    ) {
    }

    public record LeaveRequestCreate(
            @NotNull Long schoolId,
            @NotNull Long staffId,
            @NotNull LeaveType leaveType,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            String reason
    ) {
    }

    public record LeaveDecisionRequest(String note) {
    }

    public record LeaveRequestResponse(
            Long id,
            Long schoolId,
            Long staffId,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            LeaveStatus status,
            String decidedBy,
            Instant decidedAt,
            String decisionNote
    ) {
    }

    public record AttendanceRecordInput(
            @NotNull Long staffId,
            @NotNull StaffAttendanceStatus status,
            String notes
    ) {
    }

    public record AttendanceCreateRequest(
            Long schoolId,
            LocalDate date,
            Long staffId,
            StaffAttendanceStatus status,
            String notes,
            @Valid List<AttendanceRecordInput> records
    ) {
    }

    public record AttendanceResponse(
            Long id,
            Long schoolId,
            Long staffId,
            LocalDate date,
            StaffAttendanceStatus status,
            String notes,
            String capturedBy,
            Instant capturedAt
    ) {
    }

    public record TrainingProgramUpsertRequest(
            @NotNull Long schoolId,
            @NotBlank String title,
            String provider,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotNull TrainingCategory category,
            String notes
    ) {
    }

    public record TrainingProgramResponse(
            Long id,
            Long schoolId,
            String title,
            String provider,
            LocalDate startDate,
            LocalDate endDate,
            TrainingCategory category,
            String notes
    ) {
    }

    public record TrainingAttendanceInput(
            @NotNull Long staffId,
            boolean attended,
            String certificateUrl
    ) {
    }

    public record TrainingAttendanceBulkRequest(
            @NotNull Long schoolId,
            @NotNull @Valid List<TrainingAttendanceInput> records
    ) {
    }

    public record TrainingAttendanceResponse(
            Long id,
            Long schoolId,
            Long trainingProgramId,
            Long staffId,
            boolean attended,
            String certificateUrl
    ) {
    }

    public record AppraisalCreateRequest(
            @NotNull Long schoolId,
            @NotNull Long staffId,
            @NotNull LocalDate appraisalDate,
            @NotNull AppraisalType appraisalType,
            @NotBlank String reviewerName,
            @Min(0) @Max(100) Integer score,
            String summary,
            String recommendations
    ) {
    }

    public record AppraisalResponse(
            Long id,
            Long schoolId,
            Long staffId,
            LocalDate appraisalDate,
            AppraisalType appraisalType,
            String reviewerName,
            Integer score,
            String summary,
            String recommendations
    ) {
    }

    public record DashboardTopAbsent(Long staffId, Long absentDays) {
    }

    public record DashboardSummary(
            Long totalStaff,
            double attendanceRate,
            Long absentCount,
            Long lateCount,
            Long pendingLeaveRequests,
            List<DashboardTopAbsent> topAbsentStaff
    ) {
    }
}
