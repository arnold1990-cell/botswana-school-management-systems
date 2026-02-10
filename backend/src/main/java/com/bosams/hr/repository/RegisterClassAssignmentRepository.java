package com.bosams.hr.repository;

import com.bosams.hr.entity.RegisterClassAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterClassAssignmentRepository
        extends JpaRepository<RegisterClassAssignment, Long> {

    Page<RegisterClassAssignment> findBySchoolIdAndAcademicYearIdAndClassRoomId(
            Long schoolId,
            Long yearId,
            Long classId,
            Pageable p
    );

    Page<RegisterClassAssignment> findBySchoolIdAndAcademicYearId(
            Long schoolId,
            Long yearId,
            Pageable p
    );

    Page<RegisterClassAssignment> findBySchoolId(Long schoolId, Pageable p);
}
