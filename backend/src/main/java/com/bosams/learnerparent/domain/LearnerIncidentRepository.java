package com.bosams.learnerparent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerIncidentRepository extends JpaRepository<LearnerIncident, Long> { Page<LearnerIncident> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); }
