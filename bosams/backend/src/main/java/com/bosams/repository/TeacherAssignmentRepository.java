package com.bosams.repository;
import com.bosams.domain.TeacherAssignment;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
 boolean existsByTeacherIdAndAcademicYearIdAndStreamIdAndSubjectIdAndActiveTrue(Long teacherId, Long yearId, Long streamId, Long subjectId);
 List<TeacherAssignment> findByTeacherIdAndAcademicYearIdAndActiveTrue(Long teacherId, Long yearId);
}
