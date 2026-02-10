package com.bosams.hr.repository;

import com.bosams.hr.entity.StaffMember;
import com.bosams.hr.entity.StaffStatus;
import com.bosams.hr.entity.StaffType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {

    boolean existsBySchoolIdAndStaffNumber(Long schoolId, String staffNumber);

    boolean existsBySchoolIdAndStaffNumberAndIdNot(
            Long schoolId,
            String staffNumber,
            Long id
    );

    Page<StaffMember> findBySchoolIdAndStatusAndStaffType(
            Long schoolId,
            StaffStatus status,
            StaffType staffType,
            Pageable p
    );

    Page<StaffMember> findBySchoolIdAndStatus(
            Long schoolId,
            StaffStatus status,
            Pageable p
    );

    Page<StaffMember> findBySchoolIdAndStaffType(
            Long schoolId,
            StaffType staffType,
            Pageable p
    );

    Page<StaffMember> findBySchoolId(Long schoolId, Pageable p);
}
