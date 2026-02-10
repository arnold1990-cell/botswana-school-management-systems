package com.bosams.schoolsetup.repository;
import com.bosams.schoolsetup.domain.AcademicYear;
import org.springframework.data.domain.*;import org.springframework.data.jpa.repository.JpaRepository;
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> { Page<AcademicYear> findBySchoolId(Long schoolId, Pageable pageable); }
