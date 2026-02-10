package com.bosams.hr.repository;

import com.bosams.hr.entity.AppraisalRecord;
import com.bosams.hr.entity.AppraisalType;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppraisalRecordRepository extends JpaRepository<AppraisalRecord, Long> {

    Page<AppraisalRecord> findBySchoolId(Long schoolId, Pageable p);

    Page<AppraisalRecord> findBySchoolIdAndStaffId(Long schoolId, Long staffId, Pageable p);

    Page<AppraisalRecord> findBySchoolIdAndAppraisalType(
            Long schoolId,
            AppraisalType type,
            Pageable p
    );

    Page<AppraisalRecord> findBySchoolIdAndStaffIdAndAppraisalType(
            Long schoolId,
            Long staffId,
            AppraisalType type,
            Pageable p
    );

    Page<AppraisalRecord> findBySchoolIdAndAppraisalDateBetween(
            Long schoolId,
            LocalDate from,
            LocalDate to,
            Pageable p
    );
}
