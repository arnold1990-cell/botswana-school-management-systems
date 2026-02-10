package com.bosams.hr;

import com.bosams.hr.LeaveStatus;
import com.bosams.hr.StaffLeaveRequest;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffLeaveRequestRepository extends JpaRepository<StaffLeaveRequest, Long> {

    Page<StaffLeaveRequest> findBySchoolId(Long schoolId, Pageable p);

    Page<StaffLeaveRequest> findBySchoolIdAndStatus(
            Long schoolId,
            LeaveStatus status,
            Pageable p
    );

    Page<StaffLeaveRequest> findBySchoolIdAndStaffId(Long schoolId, Long staffId, Pageable p);

    Page<StaffLeaveRequest> findBySchoolIdAndStatusAndStaffId(
            Long schoolId,
            LeaveStatus status,
            Long staffId,
            Pageable p
    );

    Page<StaffLeaveRequest> findBySchoolIdAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            Long schoolId,
            LocalDate from,
            LocalDate to,
            Pageable p
    );

    long countBySchoolIdAndStatus(Long schoolId, LeaveStatus status);

    boolean existsBySchoolIdAndStaffIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long schoolId,
            Long staffId,
            LeaveStatus status,
            LocalDate date1,
            LocalDate date2
    );
}
