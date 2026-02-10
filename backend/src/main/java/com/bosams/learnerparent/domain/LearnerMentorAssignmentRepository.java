package com.bosams.learnerparent.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerMentorAssignmentRepository extends JpaRepository<LearnerMentorAssignment, Long> { Page<LearnerMentorAssignment> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); }
