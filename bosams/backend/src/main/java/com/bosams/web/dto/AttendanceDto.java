package com.bosams.web.dto;

import com.bosams.domain.Enums;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDto {
    public record AttendanceMarkItem(
            @NotNull Long studentId,
            @NotNull Enums.AttendanceStatus status,
            String remark
    ) {}

    public record AttendanceMarkRequest(
            @NotNull Integer gradeLevel,
            LocalDate attendanceDate,
            @NotEmpty List<@Valid AttendanceMarkItem> items
    ) {}

    public record AttendanceResponse(
            Long id,
            LocalDate attendanceDate,
            Long studentId,
            String studentName,
            Integer gradeLevel,
            Enums.AttendanceStatus status,
            String remark
    ) {}
}
