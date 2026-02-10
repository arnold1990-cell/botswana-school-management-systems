package com.bosams.learnerparent.domain;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerApplicationRepository extends JpaRepository<LearnerApplication, Long> { Page<LearnerApplication> findBySchoolIdAndStatusAndAppliedDateBetween(Long schoolId, ApplicationStatus status, LocalDate from, LocalDate to, Pageable p); Page<LearnerApplication> findBySchoolIdAndAppliedDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p); }
