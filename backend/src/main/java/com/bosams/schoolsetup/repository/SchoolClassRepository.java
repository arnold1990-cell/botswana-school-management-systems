package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.SchoolClass;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {
    Page<SchoolClass> findByAcademicYearId(UUID academicYearId, Pageable pageable);
    long countByGradeId(UUID gradeId);
    long countByAcademicYearId(UUID academicYearId);
}
