package com.bosams.learnerparent.domain;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerDisciplineEntryRepository extends JpaRepository<LearnerDisciplineEntry, Long> { Page<LearnerDisciplineEntry> findBySchoolIdAndLearnerIdAndEntryDateBetween(Long schoolId, Long learnerId, LocalDate from, LocalDate to, Pageable p); Page<LearnerDisciplineEntry> findBySchoolIdAndEntryDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p); }
