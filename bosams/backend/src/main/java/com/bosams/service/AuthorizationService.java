package com.bosams.service;

import com.bosams.common.ApiException;
import com.bosams.domain.*;
import com.bosams.repository.AcademicYearRepository;
import com.bosams.repository.TeacherAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorizationService implements AuthorizationServiceOperations {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);
    private static final Long DEFAULT_SCHOOL_ID = 1L;

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
    public List<Long> teacherStreamIds(UUID teacherUserId, Long academicYearId) {
        if (teacherUserId == null || academicYearId == null) {
            return List.of();
        }
        return assignmentRepository.teacherStreamIds(teacherUserId, academicYearId).stream().toList();
    }

    @Override
    public List<Long> teacherSubjectIdsForStream(UUID teacherUserId, Long academicYearId, Long streamId) {
        if (teacherUserId == null || academicYearId == null || streamId == null) {
            return List.of();
        }
        return assignmentRepository.teacherSubjectIdsForStream(teacherUserId, academicYearId, streamId).stream().toList();
    }

    @Override
    public void enforceSchoolAccess(UserEntity user, Long schoolId) {
        if (isAdmin(user)) {
            return;
        }

        if (schoolId == null || !DEFAULT_SCHOOL_ID.equals(schoolId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "SCHOOL_SCOPE_DENIED", "User has no access to this school");
        }

        log.info("enforceSchoolAccess userId={} role={} schoolId={}", user.getId(), user.getRole(), schoolId);
    }

    @Override
    public void enforceTeacherAssignment(UserEntity user, Integer gradeLevel, Long subjectId) {
        if (!isTeacher(user)) {
            return;
        }
        Long activeYearId = getActiveAcademicYear().getId();
        boolean assigned = teacherHasAssignment(user.getId(), activeYearId, gradeLevel, subjectId);
        log.info("enforceTeacherAssignment userId={} gradeLevel={} subjectId={} assigned={}", user.getId(), gradeLevel, subjectId, assigned);
        if (!assigned) {
            throw new ApiException(HttpStatus.FORBIDDEN, "NOT_ASSIGNED", "Teacher not assigned for selected grade and subject");
        }
    }

    @Override
    public Optional<Long> resolveSchoolId(UserEntity user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(DEFAULT_SCHOOL_ID);
    }
}
