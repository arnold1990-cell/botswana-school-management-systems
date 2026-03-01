package com.bosams.repository;

import com.bosams.domain.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByActiveTrue();
    Optional<AcademicYear> findByYear(Integer year);
}
