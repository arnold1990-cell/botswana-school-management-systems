package com.bosams.repository;
import com.bosams.domain.AcademicYear;import org.springframework.data.jpa.repository.JpaRepository;import java.util.*;
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> { Optional<AcademicYear> findByActiveTrue(); }
