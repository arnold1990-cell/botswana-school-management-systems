package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.TeacherAssignmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {
    private final AcademicYearRepository yearRepository;
    private final TeacherAssignmentRepository assignmentRepository;

    public AuthorizationService(AcademicYearRepository yearRepository, TeacherAssignmentRepository assignmentRepository) {
        this.yearRepository = yearRepository;
        this.assignmentRepository = assignmentRepository;
    }

    public boolean isAdmin(UserEntity u) { return u.getRole() == Enums.Role.ADMIN; }
    public boolean isPrincipal(UserEntity u) { return u.getRole() == Enums.Role.PRINCIPAL; }
    public boolean isTeacher(UserEntity u) { return u.getRole() == Enums.Role.TEACHER; }

    public AcademicYear getActiveAcademicYear() {
        return yearRepository.findByActiveTrue().orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "NO_ACTIVE_YEAR", "No active academic year"));
    }

    public boolean teacherHasAssignment(UUID userId, Long activeYearId, Integer gradeLevel, Long subjectId) {
        return assignmentRepository.existsByTeacherIdAndAcademicYearIdAndGradeLevelAndSubjectIdAndActiveTrue(userId, activeYearId, gradeLevel, subjectId);
    }

    public Set<Integer> teacherGradeLevels(UUID userId, Long activeYearId) {
        return assignmentRepository.findByTeacherIdAndAcademicYearIdAndActiveTrue(userId, activeYearId)
                .stream()
                .map(TeacherAssignment::getGradeLevel)
                .collect(Collectors.toSet());
    }
}
