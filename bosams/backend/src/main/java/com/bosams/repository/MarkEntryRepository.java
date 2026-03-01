package com.bosams.repository;

import com.bosams.domain.MarkEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkEntryRepository extends JpaRepository<MarkEntryEntity, Long> {
    Optional<MarkEntryEntity> findByLearnerIdAndSubjectIdAndTaskId(Long learnerId, Long subjectId, Long taskId);
    List<MarkEntryEntity> findByTaskIdAndSubjectId(Long taskId, Long subjectId);
    List<MarkEntryEntity> findByTaskTermAcademicYearYearAndTaskTermTermNoAndSubjectIdAndLearnerGradeLevel(Integer year, Integer termNo, Long subjectId, Integer gradeLevel);
}
