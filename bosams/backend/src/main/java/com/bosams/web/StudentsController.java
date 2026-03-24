package com.bosams.web;

import com.bosams.domain.UserEntity;
import com.bosams.service.AuthorizationService;
import com.bosams.service.StudentManagementService;
import com.bosams.web.dto.StudentDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentsController {
    private final StudentManagementService studentManagementService;
    private final AuthorizationService auth;

    public StudentsController(StudentManagementService studentManagementService, AuthorizationService auth) {
        this.studentManagementService = studentManagementService;
        this.auth = auth;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRINCIPAL','TEACHER')")
    public List<StudentDto> list(@AuthenticationPrincipal UserEntity user,
                                 @RequestParam(required = false) Integer gradeLevel,
                                 @RequestParam(required = false) String query,
                                 @RequestParam(defaultValue = "true") boolean activeOnly) {
        auth.resolveSchoolId(user).ifPresent(schoolId -> auth.enforceSchoolAccess(user, schoolId));
        return studentManagementService.list(gradeLevel, query, activeOnly)
                .stream()
                .map(StudentDto::from)
                .toList();
    }
}
