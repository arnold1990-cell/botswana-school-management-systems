package com.bosams.repository;

import com.bosams.domain.AssessmentTaskEntity;
import com.bosams.domain.Enums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentTaskRepository extends JpaRepository<AssessmentTaskEntity, Long> {
    List<AssessmentTaskEntity> findByTermIdOrderByType(Long termId);
    Optional<AssessmentTaskEntity> findByTermAcademicYearYearAndTermTermNoAndType(Integer year, Integer termNo, Enums.AssessmentType type);
}
