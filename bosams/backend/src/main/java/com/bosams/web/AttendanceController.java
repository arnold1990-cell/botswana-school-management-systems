package com.bosams.web;

import com.bosams.domain.UserEntity;
import com.bosams.service.AttendanceService;
import com.bosams.web.dto.AttendanceDto;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<AttendanceDto.AttendanceResponse> mark(
            @AuthenticationPrincipal UserEntity actor,
            @Valid @RequestBody AttendanceDto.AttendanceMarkRequest request
    ) {
        return attendanceService.markAttendance(actor, request);
    }

    @GetMapping("/grade/{gradeLevel}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<AttendanceDto.AttendanceResponse> grade(
            @PathVariable Integer gradeLevel,
            @RequestParam(required = false) LocalDate date
    ) {
        return attendanceService.gradeAttendance(gradeLevel, date);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER','STUDENT','PARENT')")
    public List<AttendanceDto.AttendanceResponse> student(@PathVariable Long studentId) {
        return attendanceService.studentAttendance(studentId);
    }
}
