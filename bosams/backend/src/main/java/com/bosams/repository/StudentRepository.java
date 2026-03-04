package com.bosams.repository;

import com.bosams.domain.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bosams.domain.Enums;

import java.util.List;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findByStreamId(Long streamId);
    List<StudentEntity> findByGradeLevel(Integer gradeLevel);
    List<StudentEntity> findByGradeLevelAndStreamId(Integer gradeLevel, Long streamId);
    List<StudentEntity> findByStatus(Enums.EntityStatus status);
    List<StudentEntity> findByGradeLevelAndStatus(Integer gradeLevel, Enums.EntityStatus status);
}
