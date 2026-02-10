package com.bosams.hr.repository;

import com.bosams.hr.entity.StaffAttendanceRecord;
import com.bosams.hr.entity.StaffAttendanceStatus;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffAttendanceRecordRepository extends JpaRepository<StaffAttendanceRecord, Long> {

    boolean existsBySchoolIdAndStaffIdAndDate(Long schoolId, Long staffId, LocalDate date);

    Page<StaffAttendanceRecord> findBySchoolIdAndDate(Long schoolId, LocalDate date, Pageable p);

    Page<StaffAttendanceRecord> findBySchoolIdAndStaffIdAndDateBetween(
            Long schoolId,
            Long staffId,
            LocalDate from,
            LocalDate to,
            Pageable p
    );

    Page<StaffAttendanceRecord> findBySchoolIdAndDateBetween(
            Long schoolId,
            LocalDate from,
            LocalDate to,
            Pageable p
    );

    long countBySchoolIdAndDateBetween(Long schoolId, LocalDate from, LocalDate to);

    long countBySchoolIdAndDateBetweenAndStatus(
            Long schoolId,
            LocalDate from,
            LocalDate to,
            StaffAttendanceStatus status
    );
}
