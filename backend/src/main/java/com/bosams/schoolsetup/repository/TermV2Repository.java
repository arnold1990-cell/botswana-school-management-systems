package com.bosams.schoolsetup.repository;

import com.bosams.schoolsetup.domain.model.Term;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermV2Repository extends JpaRepository<Term, UUID> {
    Page<Term> findByAcademicYearId(UUID academicYearId, Pageable pageable);
    long countByAcademicYearId(UUID academicYearId);
}
