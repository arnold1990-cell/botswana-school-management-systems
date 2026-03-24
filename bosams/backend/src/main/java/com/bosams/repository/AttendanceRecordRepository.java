package com.bosams.repository;

import com.bosams.domain.AttendanceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecordEntity, Long> {
    List<AttendanceRecordEntity> findByStudentIdOrderByAttendanceDateDesc(Long studentId);
    List<AttendanceRecordEntity> findByAttendanceDateAndStudentGradeLevelOrderByStudentFirstNameAsc(LocalDate attendanceDate, Integer gradeLevel);
    Optional<AttendanceRecordEntity> findByAttendanceDateAndStudentId(LocalDate attendanceDate, Long studentId);
}
