package com.bosams.learnerparent.domain;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearnerAttendanceRecordRepository extends JpaRepository<LearnerAttendanceRecord, Long> { Page<LearnerAttendanceRecord> findBySchoolIdAndDateAndPeriod(Long schoolId, LocalDate date, Integer period, Pageable p); Page<LearnerAttendanceRecord> findBySchoolIdAndDateAndPeriodAndLearnerIdIn(Long schoolId, LocalDate date, Integer period, java.util.List<Long> ids, Pageable p); Page<LearnerAttendanceRecord> findBySchoolIdAndLearnerIdAndDateBetween(Long schoolId, Long learnerId, LocalDate from, LocalDate to, Pageable p); Page<LearnerAttendanceRecord> findBySchoolIdAndDateBetween(Long schoolId, LocalDate from, LocalDate to, Pageable p); }
