package com.bosams.learnerparent.domain;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerTransportAssignmentRepository extends JpaRepository<LearnerTransportAssignment, Long> { Page<LearnerTransportAssignment> findBySchoolIdAndLearnerId(Long schoolId, Long learnerId, Pageable p); }
