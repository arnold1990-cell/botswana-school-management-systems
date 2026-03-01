package com.bosams.repository;

import com.bosams.domain.TeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
    boolean existsByTeacherIdAndAcademicYearIdAndGradeLevelAndSubjectIdAndActiveTrue(UUID teacherId, Long yearId, Integer gradeLevel, Long subjectId);

    List<TeacherAssignment> findByTeacherIdAndAcademicYearIdAndActiveTrue(UUID teacherId, Long yearId);

    List<TeacherAssignment> findByTeacherIdAndAcademicYearIdAndGradeLevelAndActiveTrue(UUID teacherId, Long yearId, Integer gradeLevel);
}
