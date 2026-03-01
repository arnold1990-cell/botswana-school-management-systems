package com.bosams.service;

import com.bosams.domain.AcademicYear;
import com.bosams.domain.UserEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AuthorizationServiceOperations {
    boolean isAdmin(UserEntity u);

    boolean isPrincipal(UserEntity u);

    boolean isTeacher(UserEntity u);

    AcademicYear getActiveAcademicYear();

    boolean teacherHasAssignment(UUID userId, Long activeYearId, Integer gradeLevel, Long subjectId);

    Set<Integer> teacherGradeLevels(UUID userId, Long activeYearId);

    List<Long> teacherStreamIds(UUID teacherUserId, Long academicYearId);

    List<Long> teacherSubjectIdsForStream(UUID teacherUserId, Long academicYearId, Long streamId);
}
