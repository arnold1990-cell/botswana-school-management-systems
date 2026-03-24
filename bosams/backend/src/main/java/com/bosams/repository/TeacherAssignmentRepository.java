package com.bosams.repository;

import com.bosams.domain.TeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
    @Query("""
            select (count(ta) > 0)
            from TeacherAssignment ta
            where ta.teacher.id = :teacherId
              and ta.academicYear.id = :yearId
              and ta.gradeLevel = :gradeLevel
              and ta.subject.id = :subjectId
              and ta.active = true
            """)
    boolean existsByTeacherIdAndAcademicYearIdAndGradeLevelAndSubjectIdAndActiveTrue(@Param("teacherId") UUID teacherId,
                                                                                      @Param("yearId") Long yearId,
                                                                                      @Param("gradeLevel") Integer gradeLevel,
                                                                                      @Param("subjectId") Long subjectId);

    @Query("""
            select ta
            from TeacherAssignment ta
            where ta.teacher.id = :teacherId
              and ta.academicYear.id = :yearId
              and ta.active = true
            """)
    List<TeacherAssignment> findByTeacherIdAndAcademicYearIdAndActiveTrue(@Param("teacherId") UUID teacherId,
                                                                           @Param("yearId") Long yearId);

    @Query("""
            select ta
            from TeacherAssignment ta
            where ta.teacher.id = :teacherId
              and ta.academicYear.id = :yearId
              and ta.gradeLevel = :gradeLevel
              and ta.active = true
            """)
    List<TeacherAssignment> findByTeacherIdAndAcademicYearIdAndGradeLevelAndActiveTrue(@Param("teacherId") UUID teacherId,
                                                                                         @Param("yearId") Long yearId,
                                                                                         @Param("gradeLevel") Integer gradeLevel);

    @Query("""
            select distinct ta.stream.id
            from TeacherAssignment ta
            where ta.teacher.id = :teacherUserId
              and ta.academicYear.id = :academicYearId
              and ta.active = true
              and ta.stream is not null
            """)
    Set<Long> teacherStreamIds(@Param("teacherUserId") UUID teacherUserId,
                               @Param("academicYearId") Long academicYearId);

    @Query("""
            select distinct ta.subject.id
            from TeacherAssignment ta
            where ta.teacher.id = :teacherUserId
              and ta.academicYear.id = :academicYearId
              and ta.stream.id = :streamId
              and ta.active = true
            """)
    Set<Long> teacherSubjectIdsForStream(@Param("teacherUserId") UUID teacherUserId,
                                         @Param("academicYearId") Long academicYearId,
                                         @Param("streamId") Long streamId);

    @Query("""
            select ta
            from TeacherAssignment ta
            where ta.teacher.id = :teacherUserId
              and ta.active = true
            """)
    List<TeacherAssignment> findByTeacherUserId(@Param("teacherUserId") UUID teacherUserId);
}
