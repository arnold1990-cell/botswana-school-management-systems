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
public class AuthorizationService implements AuthorizationServiceOperations {
    private final AcademicYearRepository yearRepository;
    private final TeacherAssignmentRepository assignmentRepository;

    public AuthorizationService(AcademicYearRepository yearRepository, TeacherAssignmentRepository assignmentRepository) {
        this.yearRepository = yearRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public boolean isAdmin(UserEntity u) { return u.getRole() == Enums.Role.ADMIN; }

    @Override
    public boolean isPrincipal(UserEntity u) { return u.getRole() == Enums.Role.PRINCIPAL; }

    @Override
    public boolean isTeacher(UserEntity u) { return u.getRole() == Enums.Role.TEACHER; }

    @Override
    public AcademicYear getActiveAcademicYear() {
        return yearRepository.findByActiveTrue().orElseThrow(() -> new ApiException(HttpStatus.CONFLICT, "NO_ACTIVE_YEAR", "No active academic year"));
    }

    @Override
    public boolean teacherHasAssignment(UUID userId, Long activeYearId, Integer gradeLevel, Long subjectId) {
        return assignmentRepository.existsByTeacherIdAndAcademicYearIdAndGradeLevelAndSubjectIdAndActiveTrue(userId, activeYearId, gradeLevel, subjectId);
    }

    @Override
    public Set<Integer> teacherGradeLevels(UUID userId, Long activeYearId) {
        return assignmentRepository.findByTeacherIdAndAcademicYearIdAndActiveTrue(userId, activeYearId)
                .stream()
                .map(TeacherAssignment::getGradeLevel)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> teacherStreamIds(UUID teacherUserId, Long academicYearId) {
        if (teacherUserId == null || academicYearId == null) {
            return Set.of();
        }
        return assignmentRepository.teacherStreamIds(teacherUserId, academicYearId);
    }

    @Override
    public Set<Long> teacherSubjectIdsForStream(UUID teacherUserId, Long academicYearId, Long streamId) {
        if (teacherUserId == null || academicYearId == null || streamId == null) {
            return Set.of();
        }
        return assignmentRepository.teacherSubjectIdsForStream(teacherUserId, academicYearId, streamId);
    }
}
