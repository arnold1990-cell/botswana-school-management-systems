package com.bosams.hr;

import com.bosams.hr.AppraisalType;
import com.bosams.hr.LeaveStatus;
import com.bosams.hr.LeaveType;
import com.bosams.hr.RegisterClassRole;
import com.bosams.hr.StaffAttendanceStatus;
import com.bosams.hr.StaffStatus;
import com.bosams.hr.StaffType;
import com.bosams.hr.TrainingCategory;
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
